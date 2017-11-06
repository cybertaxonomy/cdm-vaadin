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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.TransactionStatus;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import eu.etaxonomy.cdm.api.application.AbstractDataInserter;
import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.ExtensionType;
import eu.etaxonomy.cdm.model.common.GrantedAuthorityImpl;
import eu.etaxonomy.cdm.model.common.Group;
import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmPermissionClass;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.vaadin.model.registration.DerivationEventTypes;
import eu.etaxonomy.cdm.vaadin.security.RolesAndPermissions;

/**
 * @author a.kohlbecker
 * @since May 9, 2017
 *
 */
public class RegistrationRequiredDataInserter extends AbstractDataInserter {

    protected static final String PARAM_NAME_CREATE = "registrationCreate";

    protected static final String PARAM_NAME_WIPEOUT = "registrationWipeout";

    protected static final UUID GROUP_SUBMITTER_UUID = UUID.fromString("c468c6a7-b96c-4206-849d-5a825f806d3e");

    protected static final UUID GROUP_CURATOR_UUID = UUID.fromString("135210d3-3db7-4a81-ab36-240444637d45");

    private static final EnumSet<CRUD> CREATE_READ = EnumSet.of(CRUD.CREATE, CRUD.READ);
    private static final EnumSet<CRUD> CREATE_READ_UPDATE_DELETE = EnumSet.of(CRUD.CREATE, CRUD.READ, CRUD.UPDATE, CRUD.DELETE);

    private static final Logger logger = Logger.getLogger(RegistrationRequiredDataInserter.class);

    private ExtensionType extensionTypeIAPTRegData;

    Map<String, Institution> instituteMap = new HashMap<>();

    public static boolean commandsExecuted = false;

    private CdmRepository repo;

    private boolean hasRun = false;

    public void setCdmRepository(CdmRepository repo){
      this.repo = repo;
    }


 // ==================== Registration creation ======================= //

    /**
     * {@inheritDoc}
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if(hasRun){
            return;
        }

        runAsAuthentication(Role.ROLE_ADMIN);

        insertRequiredData();
        executeSuppliedCommands();

        restoreAuthentication();

        hasRun = true;
    }

    /**
     *
     */
    private void insertRequiredData() {

        Role roleCuration = RolesAndPermissions.ROLE_CURATION;
        if(repo.getGrantedAuthorityService().find(roleCuration.getUuid()) == null){
            repo.getGrantedAuthorityService().saveOrUpdate(roleCuration.asNewGrantedAuthority());
        }

        Group groupCurator = repo.getGroupService().load(GROUP_CURATOR_UUID, Arrays.asList("grantedAuthorities"));
        if(groupCurator == null){
            groupCurator = Group.NewInstance();
            groupCurator.setUuid(GROUP_CURATOR_UUID);
            groupCurator.setName("Curator");
        }
        assureGroupHas(groupCurator, new CdmAuthority(CdmPermissionClass.REGISTRATION, CREATE_READ_UPDATE_DELETE).toString());
        repo.getGroupService().saveOrUpdate(groupCurator);

        Group groupSubmitter = repo.getGroupService().load(GROUP_SUBMITTER_UUID, Arrays.asList("grantedAuthorities"));
        if(groupSubmitter == null){
            groupSubmitter = Group.NewInstance();
            groupSubmitter.setUuid(GROUP_SUBMITTER_UUID);
            groupSubmitter.setName("Submitter");
        }
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.TAXONNAME, CREATE_READ).toString());
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.TEAMORPERSONBASE, CREATE_READ).toString());
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.REGISTRATION, CREATE_READ).toString());
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.SPECIMENOROBSERVATIONBASE, CREATE_READ).toString());
        repo.getGroupService().saveOrUpdate(groupSubmitter);

        if(repo.getTermService().find(DerivationEventTypes.PUBLISHED_IMAGE().getUuid()) == null){
            repo.getTermService().save(DerivationEventTypes.PUBLISHED_IMAGE());
        }
        if(repo.getTermService().find(DerivationEventTypes.UNPUBLISHED_IMAGE().getUuid()) == null){
            repo.getTermService().save(DerivationEventTypes.UNPUBLISHED_IMAGE());
        }
        if(repo.getTermService().find(DerivationEventTypes.CULTURE_METABOLIC_INACTIVE().getUuid()) == null){
            repo.getTermService().save(DerivationEventTypes.CULTURE_METABOLIC_INACTIVE());
        }
        repo.getSession().flush();

    }

    private void assureGroupHas(Group group, String authorityString){
        boolean authorityExists = false;

        for(GrantedAuthority ga : group.getGrantedAuthorities()){
            if((authorityExists = ga.getAuthority().equals(authorityString)) == true){
                break;
            }
        }
        if(!authorityExists){
            group.addGrantedAuthority(findGrantedAuthority(authorityString));
        }
    }

    private GrantedAuthorityImpl findGrantedAuthority(String authorityString){
        GrantedAuthorityImpl ga = null;
        try{
            ga = repo.getGrantedAuthorityService().findAuthorityString(authorityString);
        } catch (AuthenticationCredentialsNotFoundException e){
            e.printStackTrace();
        }
        if(ga == null){
            ga = GrantedAuthorityImpl.NewInstance(authorityString);
            repo.getGrantedAuthorityService().save(ga);
        }
        return ga;
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
                        Set<String> extensions = reg.getName().getExtensions(getExtensionTypeIAPTRegData());
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

            DateTimeFormatter dateFormat1 = org.joda.time.format.DateTimeFormat.forPattern("dd.MM.yy").withPivotYear(1950);
            DateTimeFormatter dateFormat2 = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd").withPivotYear(1950);

            TransactionStatus tx = repo.startTransaction(false);
            while(true) {
                Pager<TaxonName> pager = repo.getNameService().page(null, 1000, pageIndex, null, null);
                if(pager.getRecords().isEmpty()){
                    break;
                }
                List<Registration> newRegs = new ArrayList<>(pager.getRecords().size());
                for(TaxonName name : pager.getRecords()){

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
                            DateTimeFormatter dateFormat;
                            if(iaptData.getDate().matches("\\d{4}-\\d{2}-\\d{2}")){
                                dateFormat = dateFormat2;
                            } else {
                                dateFormat = dateFormat1;
                            }
                            try {
                                regDate = dateFormat.parseDateTime(iaptData.getDate());
                                regDate.getYear();
                            } catch (Exception e) {
                                logger.error("Error parsing date : " + iaptData.getDate(), e);
                                continue;
                            }
                        }

                        Registration reg = Registration.NewInstance();
                        reg.setStatus(RegistrationStatus.PUBLISHED);
                        reg.setIdentifier("http://phycobank.org/" + iaptData.getRegId());
                        reg.setSpecificIdentifier(iaptData.getRegId().toString());
                        reg.setInstitution(getInstitution(iaptData.getOffice()));

                        boolean isPhycobankID = false; // Integer.valueOf(reg.getSpecificIdentifier()) >= 100000;

                        Partial youngestDate = null;
                        Reference youngestPub = null;

                        // find youngest publication

                        // NOTE:
                        // data imported from IAPT does not have typedesignation citations and sometimes no nomref

                        if(isPhycobankID){
                            youngestPub = (Reference) name.getNomenclaturalReference();
                            youngestDate = partial(youngestPub.getDatePublished());

                            if(name.getTypeDesignations() != null && !name.getTypeDesignations().isEmpty()){
                                for(TypeDesignationBase td : name.getTypeDesignations()){
                                    if(td.getCitation() == null){
                                        continue;
                                    }
                                    Partial pubdate = partial(td.getCitation().getDatePublished());
                                        if(youngestDate.compareTo(pubdate) < 0){
                                            youngestDate = pubdate;
                                            youngestPub = td.getCitation();
                                        }
                                }
                            }
                        }

                        if((isPhycobankID && youngestPub == name.getNomenclaturalReference()) || !isPhycobankID) {
                            reg.setName(name);
                        } else {
                            logger.debug("skipping name published in older referece");
                        }
                        if(name.getTypeDesignations() != null && !name.getTypeDesignations().isEmpty()){
                            // do not add the collection directly to avoid "Found shared references to a collection" problem
                            HashSet<TypeDesignationBase> typeDesignations = new HashSet<>(name.getTypeDesignations().size());
                            for(TypeDesignationBase td : name.getTypeDesignations()){
                                if(td.getCitation() == null && isPhycobankID){
                                    logger.error("Missing TypeDesignation Citation in Phycobank data");
                                    continue;
                                }
                                if((isPhycobankID && youngestPub == td.getCitation()) || !isPhycobankID){
                                    typeDesignations.add(td);
                                } else {
                                    logger.debug("skipping typedesignation published in older reference");
                                }
                            }
                            reg.setTypeDesignations(typeDesignations);
                        }
                        reg.setRegistrationDate(regDate);
                        logger.debug("IAPT Registration for " + name.getTitleCache());
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
     * @param datePublished
     * @return
     */
    private Partial partial(TimePeriod datePublished) {
        if(datePublished != null){
            if(datePublished.getEnd() != null){
                return datePublished.getEnd();
            } else {
                return datePublished.getStart();
            }
        }
        return null;
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
