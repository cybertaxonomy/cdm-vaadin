/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * Provides RegistrationDTOs and RegistrationWorkingsets for Registrations in the database.
 * <p>
 * Can create missing registrations for names which have Extensions of the Type <code>IAPTRegdata.json</code>.
 * See https://dev.e-taxonomy.eu/redmine/issues/6621 for further details.
 * This feature can be activated by by supplying one of the following jvm command line arguments:
 * <ul>
 * <li><code>-DregistrationCreate=iapt</code>: create all iapt Registrations if missing</li>
 * <li><code>-DregistrationWipeout=iapt</code>: remove all iapt Registrations</li>
 * <li><code>-DregistrationWipeout=all</code>: remove all Registrations</li>
 * </ul>
 * The <code>-DregistrationWipeout</code> commands are executed before the <code>-DregistrationCreate</code> and will not change the name and type designations.
 *
 *
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Service("registrationWorkingSetService")
@Transactional(readOnly=true)
public class RegistrationWorkingSetService implements IRegistrationWorkingSetService, ApplicationListener<ContextRefreshedEvent> {

    protected static final String PARAM_NAME_CREATE = "registrationCreate";

    protected static final String PARAM_NAME_WIPEOUT = "registrationWipeout";

    private static final Logger logger = Logger.getLogger(RegistrationWorkingSetService.class);

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private ExtensionType extensionTypeIAPTRegData;

    public RegistrationWorkingSetService() {

    }


    /**
     *
     */

    private void executeSuppliedCommands() {

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
        Pager<AgentBase> pager = repo.getAgentService().findByTitle(Institution.class, office, MatchMode.EXACT, null, null, null, null, null);
        if(!pager.getRecords().isEmpty()){
            return (Institution) pager.getRecords().get(0);
        } else {
            Institution institute = (Institution) repo.getAgentService().save(Institution.NewNamedInstance(office));
            return institute;
        }
    }


    private ExtensionType getExtensionTypeIAPTRegData() {
        if(extensionTypeIAPTRegData == null){
            extensionTypeIAPTRegData = (ExtensionType) repo.getTermService().load(UUID.fromString("9be1bfe3-6ba0-4560-af15-86971ab96e09"));
        }
        return extensionTypeIAPTRegData;
    }


    int minTypeDesignationCount = 1;


    @Override
    public Collection<RegistrationDTO> listDTOs() {

        List<Registration> regs = repo.getRegistrationService().list(null, 50, 0, null, null);

        List<RegistrationDTO> dtos = makeDTOs(regs);
        return dtos;
    }


    /**
     * @param regs
     * @return
     */
    private List<RegistrationDTO> makeDTOs(List<Registration> regs) {
        List<RegistrationDTO> dtos = new ArrayList<>(regs.size());
        regs.forEach(reg -> {dtos.add(new RegistrationDTO(reg));});
        return dtos;
    }

    /**
     * @param id the CDM Entity id
     * @return
     */
    @Override
    public RegistrationDTO loadDtoById(Integer id) {
        Registration reg = repo.getRegistrationService().find(id);
        return new RegistrationDTO(reg);
    }

    /**
     * @param  id the CDM Entity id
     * @return
     * @throws RegistrationValidationException
     */
    @Override
    public RegistrationWorkingSet loadWorkingSetByRegistrationID(Integer id) throws RegistrationValidationException {

        RegistrationDTO dto = loadDtoById(id);

        Pager<Registration> pager = null;

        List<Registration> workingSetRegs;

        if (pager == null){
            //FIXME remove below mock function once the service if fully working
            workingSetRegs = new ArrayList<>();
            workingSetRegs.add(dto.registration());
        } else {
            pager = repo.getRegistrationService().page(null, null, dto.getCitation(), null, null, null);
            workingSetRegs = pager.getRecords();
        }

        return new RegistrationWorkingSet(makeDTOs(workingSetRegs));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        executeSuppliedCommands();
    }


}
