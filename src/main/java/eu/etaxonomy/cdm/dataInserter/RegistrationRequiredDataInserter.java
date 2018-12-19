/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.dataInserter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.AbstractDataInserter;
import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.DefinedTerm;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.GrantedAuthorityImpl;
import eu.etaxonomy.cdm.model.common.Group;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatus;
import eu.etaxonomy.cdm.model.name.NomenclaturalStatusType;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonBase;
import eu.etaxonomy.cdm.persistence.dao.hibernate.taxonGraph.AbstractHibernateTaxonGraphProcessor;
import eu.etaxonomy.cdm.persistence.dao.taxonGraph.TaxonGraphException;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmPermissionClass;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;

///*
// * Can create missing registrations for names which have Extensions of the Type <code>IAPTRegdata.json</code>.
//* See https://dev.e-taxonomy.eu/redmine/issues/6621 for further details.
//* This feature can be activated by by supplying one of the following jvm command line arguments:
//* <ul>
//* <li><code>-DregistrationCreate=iapt</code>: create all iapt Registrations if missing</li>
//* <li><code>-DregistrationWipeout=iapt</code>: remove all iapt Registrations</li>
//* <li><code>-DregistrationWipeout=all</code>: remove all Registrations</li>
//* </ul>
//* The <code>-DregistrationWipeout</code> commands are executed before the <code>-DregistrationCreate</code> and will not change the name and type designations.
//*/
/**
 * This feature can be activated by by supplying one of the following jvm command line arguments:
 * <ul>
 *   <li><code>-DtaxonGraphCreate=true</code>: create taxon graph relations for all names below genus level</li>
 * </ul>
 *
 * @author a.kohlbecker
 * @since May 9, 2017
 *
 */
public class RegistrationRequiredDataInserter extends AbstractDataInserter {

//    protected static final String PARAM_NAME_CREATE = "registrationCreate";
//
//    protected static final String PARAM_NAME_WIPEOUT = "registrationWipeout";

    protected static final String TAXON_GRAPH_CREATE = "taxonGraphCreate";

    protected static final UUID GROUP_SUBMITTER_UUID = UUID.fromString("c468c6a7-b96c-4206-849d-5a825f806d3e");

    protected static final UUID GROUP_CURATOR_UUID = UUID.fromString("135210d3-3db7-4a81-ab36-240444637d45");

    private static final EnumSet<CRUD> CREATE_READ = EnumSet.of(CRUD.CREATE, CRUD.READ);
    private static final EnumSet<CRUD> CREATE_READ_UPDATE_DELETE = EnumSet.of(CRUD.CREATE, CRUD.READ, CRUD.UPDATE, CRUD.DELETE);

    private static final Logger logger = Logger.getLogger(RegistrationRequiredDataInserter.class);

//    private ExtensionType extensionTypeIAPTRegData;

    Map<String, Institution> instituteMap = new HashMap<>();

    public static boolean commandsExecuted = false;

    private CdmRepository repo;

    private boolean hasRun = false;

    public void setCdmRepository(CdmRepository repo){
      this.repo = repo;
    }

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
    @Transactional
    private void insertRequiredData() {

        TransactionStatus txStatus = repo.startTransaction(false);

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
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.REFERENCE, CREATE_READ).toString());
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.SPECIMENOROBSERVATIONBASE, CREATE_READ).toString());
        assureGroupHas(groupSubmitter, new CdmAuthority(CdmPermissionClass.COLLECTION, CREATE_READ).toString());
        repo.getGroupService().saveOrUpdate(groupSubmitter);

        TermVocabulary<DefinedTerm> kindOfUnitVocabulary = repo.getVocabularyService().find(KindOfUnitTerms.KIND_OF_UNIT_VOCABULARY().getUuid());
        if(repo.getVocabularyService().find(KindOfUnitTerms.KIND_OF_UNIT_VOCABULARY().getUuid()) == null){
            kindOfUnitVocabulary = repo.getVocabularyService().save(KindOfUnitTerms.KIND_OF_UNIT_VOCABULARY());
        }

        DefinedTermBase kouSpecimen = repo.getTermService().find(KindOfUnitTerms.SPECIMEN().getUuid());
        DefinedTermBase kouImage = repo.getTermService().find(KindOfUnitTerms.PUBLISHED_IMAGE().getUuid());
        DefinedTermBase kouUnpublishedImage = repo.getTermService().find(KindOfUnitTerms.UNPUBLISHED_IMAGE().getUuid());
        DefinedTermBase kouCulture = repo.getTermService().find(KindOfUnitTerms.CULTURE_METABOLIC_INACTIVE().getUuid());

        if(kouSpecimen == null){
            kouSpecimen = repo.getTermService().save(KindOfUnitTerms.SPECIMEN());
        }
        if(kouImage == null){
            kouImage = repo.getTermService().save(KindOfUnitTerms.PUBLISHED_IMAGE());
        }
        if(kouUnpublishedImage == null){
            kouUnpublishedImage = repo.getTermService().save(KindOfUnitTerms.UNPUBLISHED_IMAGE());
        }
        if(kouCulture == null){
            kouCulture = repo.getTermService().save(KindOfUnitTerms.CULTURE_METABOLIC_INACTIVE());
        }

        Set<DefinedTerm> termInVocab = kindOfUnitVocabulary.getTerms();
        List<DefinedTermBase> kouTerms = Arrays.asList(kouCulture, kouImage, kouSpecimen, kouUnpublishedImage);

        for(DefinedTermBase t : kouTerms){
            if(!termInVocab.contains(t)){
                kindOfUnitVocabulary.addTerm((DefinedTerm)t);
            }
        }

        repo.commitTransaction(txStatus);

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

        String taxonGraphCreate = System.getProperty(TAXON_GRAPH_CREATE);

        if(taxonGraphCreate != null){
            AbstractHibernateTaxonGraphProcessor processor = new AbstractHibernateTaxonGraphProcessor() {

                @Override
                public Session getSession() {
                    return repo.getSession();
                }
            };
            logger.setLevel(Level.DEBUG);
            int chunksize = 1000;
            int pageIndex = 0;
            TransactionStatus tx;
            Pager<Taxon> taxonPage;
            List<TaxonBase> taxa = new ArrayList<>();
            logger.debug("======= fixing sec refrences =========");
            while(true){
                tx = repo.startTransaction(false);
                taxonPage = repo.getTaxonService().page(Taxon.class, chunksize, pageIndex++, null, null);
                if(taxonPage.getRecords().size() == 0){
                    repo.commitTransaction(tx);
                    break;
                }
                for(Taxon taxon : taxonPage.getRecords()){
                    taxon.setSec(processor.secReference());
                    repo.getTaxonService().saveOrUpdate(taxon);
                }
                repo.commitTransaction(tx);
            }

            logger.debug("======= creating taxon graph =========");
            pageIndex = 0;
            Pager<TaxonName> page;
            while(true){
               tx = repo.startTransaction(false);
               page = repo.getNameService().page(null, chunksize, pageIndex++, null, null);
               if(page.getRecords().size() == 0){
                   repo.commitTransaction(tx);
                   break;
               }
               logger.debug(TAXON_GRAPH_CREATE + ": chunk " + pageIndex + "/" + Math.ceil(page.getCount() / chunksize));
               taxa = new ArrayList<>();

               for(TaxonName name : page.getRecords()){
                   if(name.getRank() != null && name.getRank().isLower(Rank.GENUS())){
                       NomenclaturalStatusType illegitimType = findILegitimateStatusType(name);
                       if(illegitimType == null){
                           Taxon taxon;
                           try {
                               logger.debug("Processing name: " + name.getTitleCache() + " [" + name.getRank().getLabel() + "]");
                               taxon = processor.assureSingleTaxon(name);
                               processor.updateEdges(taxon);
                               taxa.add(taxon);
                           } catch (TaxonGraphException e) {
                               logger.error(e.getMessage());
                           }
                       } else {
                           logger.debug("Skipping illegitimate name: " + name.getTitleCache() + " " + illegitimType.getLabel() + " [" + name.getRank().getLabel() + "]");
                       }
                   } else {
                       logger.debug("Skipping name: " + name.getTitleCache() + " [" + (name.getRank() != null ? name.getRank().getLabel() : "NULL") + "]");
                   }
               }
               repo.getTaxonService().saveOrUpdate(taxa);
               repo.commitTransaction(tx);
            }
        }

//        String wipeoutCmd = System.getProperty(PARAM_NAME_WIPEOUT);
//        String createCmd = System.getProperty(PARAM_NAME_CREATE);
//
//        // ============ DELETE
//        if(wipeoutCmd != null && wipeoutCmd.matches("iapt|all")){
//
//            boolean onlyIapt = wipeoutCmd.equals("iapt");
//            Set<UUID> deleteCandidates = new HashSet<UUID>();
//
//            TransactionStatus tx = repo.startTransaction(true);
//            List<Registration> allRegs = repo.getRegistrationService().list(null, null, null, null, null);
//            for(Registration reg : allRegs){
//                if(onlyIapt){
//                    try {
//                        @SuppressWarnings("unchecked")
//                        Set<String> extensions = reg.getName().getExtensions(getExtensionTypeIAPTRegData());
//                        if(reg.getUuid() != null){
//                            deleteCandidates.add(reg.getUuid());
//                        }
//                    } catch(NullPointerException e){
//                        // IGNORE
//                    }
//                } else {
//                    if(reg.getUuid() != null){
//                        deleteCandidates.add(reg.getUuid());
//                    }
//                }
//            }
//            repo.commitTransaction(tx);
//            if(!deleteCandidates.isEmpty()){
//                try {
//                    repo.getRegistrationService().delete(deleteCandidates);
//                } catch (Exception e) {
//                    // MySQLIntegrityConstraintViolationException happens here every second run !!!
//                    logger.error(e);
//                }
//            }
//        }
//
//        // ============ CREATE
//        int pageIndex = 0;
//        if(createCmd != null && createCmd.equals("iapt")){
//
//            DateTimeFormatter dateFormat1 = org.joda.time.format.DateTimeFormat.forPattern("dd.MM.yy").withPivotYear(1950);
//            DateTimeFormatter dateFormat2 = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd").withPivotYear(1950);
//
//            TransactionStatus tx = repo.startTransaction(false);
//            while(true) {
//                Pager<TaxonName> pager = repo.getNameService().page(null, 1000, pageIndex, null, null);
//                if(pager.getRecords().isEmpty()){
//                    break;
//                }
//                List<Registration> newRegs = new ArrayList<>(pager.getRecords().size());
//                for(TaxonName name : pager.getRecords()){
//
//
//
//                    Set<String> extensionValues = name.getExtensions(getExtensionTypeIAPTRegData());
//
//                    // there is for sure only one
//                    if(extensionValues.isEmpty()){
//                        continue;
//                    }
//
//                    logger.debug("IAPT Registration for " + name.getTitleCache() + " ...");
//
//                    String iaptJson = extensionValues.iterator().next();
//                    try {
//
//                        IAPTRegData iaptData = new ObjectMapper().readValue(iaptJson, IAPTRegData.class);
//
//                        if(iaptData.getRegId() == null){
//                            continue;
//                        }
//
//                        DateTime regDate = null;
//                        if(iaptData.getDate() != null){
//                            DateTimeFormatter dateFormat;
//                            if(iaptData.getDate().matches("\\d{4}-\\d{2}-\\d{2}")){
//                                dateFormat = dateFormat2;
//                            } else {
//                                dateFormat = dateFormat1;
//                            }
//                            try {
//                                regDate = dateFormat.parseDateTime(iaptData.getDate());
//                                regDate.getYear();
//                            } catch (Exception e) {
//                                logger.error("Error parsing date : " + iaptData.getDate(), e);
//                                continue;
//                            }
//                        }
//
//                        Registration reg = Registration.NewInstance();
//                        reg.setStatus(RegistrationStatus.PUBLISHED);
//                        reg.setIdentifier("http://phycobank.org/" + iaptData.getRegId());
//                        reg.setSpecificIdentifier(iaptData.getRegId().toString());
//                        reg.setInstitution(getInstitution(iaptData.getOffice()));
//
//                        boolean isPhycobankID = Integer.valueOf(reg.getSpecificIdentifier()) >= 100000;
//
//                        Partial youngestDate = null;
//                        Reference youngestPub = null;
//
//                        // find youngest publication
//
//                        // NOTE:
//                        // data imported from IAPT does not have typedesignation citations and sometimes no nomref
//
//                        if(isPhycobankID){
//                            youngestPub = name.getNomenclaturalReference();
//                            youngestDate = partial(youngestPub.getDatePublished());
//
//                            if(name.getTypeDesignations() != null && !name.getTypeDesignations().isEmpty()){
//                                for(TypeDesignationBase<?> td : name.getTypeDesignations()){
//                                    if(td.getCitation() == null){
//                                        continue;
//                                    }
//                                    Partial pubdate = partial(td.getCitation().getDatePublished());
//                                    if(pubdate != null){
//
//                                        try {
//                                            if(youngestDate== null || earlierThanOther(youngestDate, pubdate)){
//                                                youngestDate = pubdate;
//                                                youngestPub = td.getCitation();
//                                            }
//                                        } catch (Exception e) {
//                                            logger.error("Error comparing " + youngestDate + " with" + pubdate , e);
//                                        }
//                                    }
//                                }
//                            }
//                        }
//
//                        if((isPhycobankID && youngestPub == name.getNomenclaturalReference()) || !isPhycobankID) {
//                            reg.setName(name);
//                        } else {
//                            logger.debug("skipping name published in older referece");
//                        }
//                        if(name.getTypeDesignations() != null && !name.getTypeDesignations().isEmpty()){
//                            // do not add the collection directly to avoid "Found shared references to a collection" problem
//                            Set<TypeDesignationBase> typeDesignations = new HashSet<>(name.getTypeDesignations().size());
//                            for(TypeDesignationBase<?> td : name.getTypeDesignations()){
//                                if(td.getCitation() == null && isPhycobankID){
//                                    logger.error("Missing TypeDesignation Citation in Phycobank data");
//                                    continue;
//                                }
//                                if((isPhycobankID && youngestPub == td.getCitation()) || !isPhycobankID){
//                                    typeDesignations.add(td);
//                                } else {
//                                    logger.debug("skipping typedesignation published in older reference");
//                                }
//                            }
//                            reg.setTypeDesignations(typeDesignations);
//                        }
//                        reg.setRegistrationDate(regDate);
//                        newRegs.add(reg);
//
//                    } catch (JsonParseException e) {
//                        logger.error("Error parsing IAPTRegData from extension", e);
//                    } catch (JsonMappingException e) {
//                        logger.error("Error mapping json from extension to IAPTRegData", e);
//                    } catch (IOException e) {
//                        logger.error(e);
//                    }
//
//                }
//                repo.getRegistrationService().save(newRegs);
//                tx.flush();
//                logger.debug("Registrations saved");
//                pageIndex++;
//            }
//            repo.commitTransaction(tx);
//        }

    }

    private NomenclaturalStatusType findILegitimateStatusType(TaxonName name){
        for(NomenclaturalStatus status : name.getStatus()){
            if(status.getType() != null && !status.getType().isLegitimateType()){
                return status.getType();
            }
        }
        return null;
    }


//    /**
//     * @param youngestDate
//     * @param pubdate
//     * @return
//     */
//    private boolean earlierThanOther(Partial basePartial, Partial other) {
//
//        if(basePartial == null || basePartial.getValues().length == 0){
//            return false;
//        }
//        if(other == null || other.getValues().length == 0){
//            return true;
//        }
//        if(basePartial.size() == other.size()) {
//            return basePartial.compareTo(other) < 0;
//        }
//        basePartial = basePartial.without(DateTimeFieldType.dayOfMonth());
//        other = other.without(DateTimeFieldType.dayOfMonth());
//        if(basePartial.size() == other.size()) {
//            return basePartial.compareTo(other) < 0;
//        }
//        basePartial = basePartial.without(DateTimeFieldType.monthOfYear());
//        other = other.without(DateTimeFieldType.monthOfYear());
//        return basePartial.compareTo(other) < 0;
//
//    }


//    /**
//     * @param datePublished
//     * @return
//     */
//    private Partial partial(TimePeriod datePublished) {
//        if(datePublished != null){
//            if(datePublished.getEnd() != null){
//                return datePublished.getEnd();
//            } else {
//                return datePublished.getStart();
//            }
//        }
//        return null;
//    }


//    /**
//     * @param office
//     * @return
//     */
//    private Institution getInstitution(String office) {
//        Institution institution;
//        if(instituteMap.containsKey(office)){
//            institution = instituteMap.get(office);
//        } else {
//
//            Pager<Institution> pager = repo.getAgentService().findByTitleWithRestrictions(Institution.class, office, MatchMode.EXACT, null, null, null, null, null);
//   )         if(!pager.getRecords().isEmpty()){
//                institution =  pager.getRecords().get(0);
//            } else {
//                Institution institute = (Institution) repo.getAgentService().save(Institution.NewNamedInstance(office));
//                institution = institute;
//            }
//            instituteMap.put(office, institution);
//        }
//        return institution;
//    }


//    private ExtensionType getExtensionTypeIAPTRegData() {
//        if(extensionTypeIAPTRegData == null){
//            extensionTypeIAPTRegData = (ExtensionType) repo.getTermService().load(UUID.fromString("9be1bfe3-6ba0-4560-af15-86971ab96e09"));
//        }
//        return extensionTypeIAPTRegData;
//    }


}
