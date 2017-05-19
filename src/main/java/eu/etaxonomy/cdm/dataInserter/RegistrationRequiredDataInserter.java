/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.dataInserter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.Extension;
import eu.etaxonomy.cdm.model.common.ExtensionType;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.vaadin.security.RolesAndPermissions;

/**
 * @author a.kohlbecker
 * @since May 9, 2017
 *
 */
@SpringComponent
@Transactional(readOnly=true)
public class RegistrationRequiredDataInserter implements ApplicationListener<ContextRefreshedEvent>{

    protected static final String PARAM_NAME_CREATE = "registrationCreate";

    protected static final String PARAM_NAME_WIPEOUT = "registrationWipeout";

    private static final Logger logger = Logger.getLogger(RegistrationRequiredDataInserter.class);

    private ExtensionType extensionTypeIAPTRegData;

    Map<String, Institution> instituteMap = new HashMap<>();

    public static boolean commandsExecuted = false;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

 // ==================== Registration creation ======================= //

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        insertRequiredData();
        executeSuppliedCommands();
    }

    /**
 *
 */
private void insertRequiredData() {
    Role roleCuration = RolesAndPermissions.ROLE_CURATION;
    if(repo.getGrantedAuthorityService().find(roleCuration.getUuid()) == null){
        repo.getGrantedAuthorityService().saveOrUpdate(roleCuration.asNewGrantedAuthority());
        repo.getGrantedAuthorityService().getSession().flush();
    }

}

    /**
     *
     */

    private void executeSuppliedCommands() {

        if(commandsExecuted){
            // do not run twice
            // a second run could take place during initialization of the web context
            return;
        }
        commandsExecuted  = true;

        String wipeoutCmd = System.getProperty(PARAM_NAME_WIPEOUT);
        String createCmd = System.getProperty(PARAM_NAME_CREATE);

        // ============ DELETE
        if(wipeoutCmd != null && wipeoutCmd.matches("iapt|all")){

            boolean onlyIapt = wipeoutCmd.equals("iapt");
            List<UUID> deleteCandidates = new ArrayList<UUID>();

            TransactionStatus tx = repo.startTransaction(true);
            List<Registration> allRegs = repo.getRegistrationService().list(null, null, null, null, null);
            for(Registration reg : allRegs){
                if(onlyIapt){
                    try {
                        @SuppressWarnings("unchecked")
                        Set<Extension> extensions = reg.getName().getExtensions(getExtensionTypeIAPTRegData());
                        deleteCandidates.add(reg.getUuid());
                    } catch(NullPointerException e){
                        // IGNORE
                    }
                } else {
                    deleteCandidates.add(reg.getUuid());
                }
            }
            repo.commitTransaction(tx);
            repo.getRegistrationService().delete(deleteCandidates);
        }

        // ============ CREATE
        int pageIndex = 0;
        if(createCmd != null && createCmd.equals("iapt")){

            DateTimeFormatter dateFormat = org.joda.time.format.DateTimeFormat.forPattern("dd.MM.yy").withPivotYear(1950);

            TransactionStatus tx = repo.startTransaction(false);
            while(true) {
                Pager<TaxonNameBase> pager = repo.getNameService().page(null, 1000, pageIndex, null, null);
                if(pager.getRecords().isEmpty()){
                    break;
                }
                List<Registration> newRegs = new ArrayList<>(pager.getRecords().size());
                for(TaxonNameBase name : pager.getRecords()){

                    Set<String> extensionValues = name.getExtensions(getExtensionTypeIAPTRegData());

                    // there is for sure only one
                    if(extensionValues.isEmpty()){
                        continue;
                    }
                    String iaptJson = extensionValues.iterator().next();
                    try {

                        IAPTRegData iaptData = new ObjectMapper().readValue(iaptJson, IAPTRegData.class);

                        if(iaptData.getRegId() == null){
                            continue;
                        }

                        DateTime regDate = null;
                        if(iaptData.getDate() != null){
                            try {
                                regDate = dateFormat.parseDateTime(iaptData.getDate());
                                regDate.getYear();
                            } catch (Exception e) {
                                logger.error("Error parsing date: " + iaptData.getDate(), e);
                                continue;
                            }
                        }

                        Registration reg = Registration.NewInstance();
                        reg.setStatus(RegistrationStatus.PUBLISHED);
                        reg.setIdentifier("http://phycobank/" + iaptData.getRegId());
                        reg.setSpecificIdentifier(iaptData.getRegId().toString());
                        reg.setInstitution(getInstitution(iaptData.getOffice()));
                        reg.setName(name);
                        if(name.getTypeDesignations() != null && !name.getTypeDesignations().isEmpty()){
                            // do not add the collection directly to avoid "Found shared references to a collection" problem
                            HashSet<TypeDesignationBase> typeDesignations = new HashSet<>(name.getTypeDesignations().size());
                            typeDesignations.addAll(name.getTypeDesignations());
                            reg.setTypeDesignations(typeDesignations);
                        }
                        reg.setRegistrationDate(regDate);
                        logger.debug("IAPT Registraion for " + name.getTitleCache());
                        newRegs.add(reg);

                    } catch (JsonParseException e) {
                        logger.error("Error parsing IAPTRegData from extension", e);
                    } catch (JsonMappingException e) {
                        logger.error("Error mapping json from extension to IAPTRegData", e);
                    } catch (IOException e) {
                        logger.error(e);
                    }

                }
                repo.getRegistrationService().save(newRegs);
                repo.getRegistrationService().getSession().flush();
                logger.debug("Registrations saved");
                pageIndex++;
            }
            repo.commitTransaction(tx);
        }
    }


    /**
     * @param office
     * @return
     */
    private Institution getInstitution(String office) {
        Institution institution;
        if(instituteMap.containsKey(office)){
            institution = instituteMap.get(office);
        } else {

            Pager<AgentBase> pager = repo.getAgentService().findByTitle(Institution.class, office, MatchMode.EXACT, null, null, null, null, null);
            if(!pager.getRecords().isEmpty()){
                institution =  (Institution) pager.getRecords().get(0);
            } else {
                Institution institute = (Institution) repo.getAgentService().save(Institution.NewNamedInstance(office));
                institution = institute;
            }
            instituteMap.put(office, institution);
        }
        return institution;
    }


    private ExtensionType getExtensionTypeIAPTRegData() {
        if(extensionTypeIAPTRegData == null){
            extensionTypeIAPTRegData = (ExtensionType) repo.getTermService().load(UUID.fromString("9be1bfe3-6ba0-4560-af15-86971ab96e09"));
        }
        return extensionTypeIAPTRegData;
    }



}
