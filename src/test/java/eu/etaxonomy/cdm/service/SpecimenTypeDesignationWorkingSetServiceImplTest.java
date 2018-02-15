/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.ExpectedDataSet;
import org.unitils.spring.annotation.SpringBeanByName;
import org.unitils.spring.annotation.SpringBeanByType;

import eu.etaxonomy.cdm.CdmVaadinIntegrationTest;
import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.utility.DerivedUnitConversionException;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.common.DefinedTerm;
import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.LanguageString;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;
import eu.etaxonomy.cdm.model.occurrence.MediaSpecimen;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;

/**
 * @author a.kohlbecker
 * @since Nov 17, 2017
 *
 */
@Transactional(TransactionMode.DISABLED)
// IMPORTANT: test03_deleteTypeDesignationTest executed not as last would cause the other tests to fail due to changes in the db
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SpecimenTypeDesignationWorkingSetServiceImplTest extends CdmVaadinIntegrationTest{

    @SpringBeanByName
    private CdmRepository cdmRepository;

    @SpringBeanByType
    private ISpecimenTypeDesignationWorkingSetService service;

    @BeforeClass
    public static void setupLoggers()  {
       // Logger.getLogger("org.dbunit").setLevel(Level.DEBUG);
    }

    int registrationId = 5000;

    private Integer publicationId = 5000;

    private Integer typifiedNameId = 5000;

    private final String[] includeTableNames_create = new String[]{"TAXONNAME", "REFERENCE", "AGENTBASE", "HOMOTYPICALGROUP", "REGISTRATION",
            "HIBERNATE_SEQUENCES"};

    private final String[] includeTableNames_delete = new String[]{"TAXONNAME", "REFERENCE", "AGENTBASE", "HOMOTYPICALGROUP", "REGISTRATION",
            "DERIVATIONEVENT", "GATHERINGEVENT", "LANGUAGESTRING", "SPECIMENOROBSERVATIONBASE", "TYPEDESIGNATIONBASE",
            "REGISTRATION_TYPEDESIGNATIONBASE", "TAXONNAME_TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE_DERIVATIONEVENT",
            "MEDIA", "MEDIA_REPRESENTATION", "MEDIAREPRESENTATION", "MEDIAREPRESENTATIONPART",
            "AUDITEVENT",
            "HIBERNATE_SEQUENCES"
            };


    @Test
    @DataSet("SpecimenTypeDesignationWorkingSetServiceImplTest.xml")
    public void test01_createAndEditTest() throws DerivedUnitConversionException, URISyntaxException, FileNotFoundException {

//        printDataSetWithNull(System.err, new String[]{"USERACCOUNT", "GROUPS", "USERACCOUNT_GRANTEDAUTHORITYIMPL", "USERACCOUNT_PERMISSIONGROUP"
//                , "PERMISSIONGROUP", "PERMISSIONGROUP_GRANTEDAUTHORITYIMPL", "GRANTEDAUTHORITYIMPL"});

       //printDataSetWithNull(System.err, debugTables);
//        writeDbUnitDataSetFile(new String[]{"AUDITEVENT", "DEFINEDTERMBASE", "DEFINEDTERMBASE_AUD", "DEFINEDTERMBASE_REPRESENTATION", "DEFINEDTERMBASE_REPRESENTATION_AUD",
//                "REPRESENTATION", "REPRESENTATION_AUD", "HIBERNATE_SEQUENCES"},
//                "RegistrationTerms");

       SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.create(registrationId, publicationId, typifiedNameId);

       Assert.assertNotNull(workingset.getOwner());
       Assert.assertEquals(Registration.class, workingset.getOwner().getClass());

       workingset.getFieldUnit().setFieldNotes("FieldNotes");
       int baseEntityID = workingset.getFieldUnit().getId();
       workingset.getFieldUnit().setFieldNumber("FieldNumber");
       workingset.getFieldUnit().getGatheringEvent().setLocality(LanguageString.NewInstance("Somewhere", Language.ENGLISH()));

       SpecimenTypeDesignationDTO specimenTypeDesignationDTO = new SpecimenTypeDesignationDTO();
       specimenTypeDesignationDTO.setKindOfUnit((DefinedTerm)cdmRepository.getTermService().load(KindOfUnitTerms.PUBLISHED_IMAGE().getUuid()));
       specimenTypeDesignationDTO.setMediaUri(new URI("http://foo.bar.com/image1"));
       specimenTypeDesignationDTO.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
       specimenTypeDesignationDTO.setAccessionNumber("TEST_1");

       workingset.getSpecimenTypeDesignationDTOs().add(specimenTypeDesignationDTO);

       service.save(workingset);

       SpecimenOrObservationBase baseEntity = cdmRepository.getOccurrenceService().load(workingset.getFieldUnit().getUuid());


       printDataSetWithNull(System.err, new String[]{"TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE"});

       TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<FieldUnit>(FieldUnit.class, baseEntity.getId(), baseEntity.getTitleCache());

       workingset = service.loadDtoByIds(registrationId, baseEntityRef);

       Assert.assertNotNull(specimenTypeDesignationDTO.asSpecimenTypeDesignation().getTypeSpecimen());
       Assert.assertEquals(MediaSpecimen.class, specimenTypeDesignationDTO.asSpecimenTypeDesignation().getTypeSpecimen().getClass());
       Assert.assertNotNull(specimenTypeDesignationDTO.asSpecimenTypeDesignation().getTypeSpecimen().getOriginals().iterator().next());
       Assert.assertEquals(FieldUnit.class, specimenTypeDesignationDTO.asSpecimenTypeDesignation().getTypeSpecimen().getOriginals().iterator().next().getClass());
       Assert.assertEquals("FieldNumber", ((FieldUnit)specimenTypeDesignationDTO.asSpecimenTypeDesignation().getTypeSpecimen().getOriginals().iterator().next()).getFieldNumber());

       SpecimenTypeDesignationDTO specimenTypeDesignationDTO2 = new SpecimenTypeDesignationDTO();
       specimenTypeDesignationDTO2.setKindOfUnit((DefinedTerm)cdmRepository.getTermService().load(KindOfUnitTerms.SPECIMEN().getUuid()));
       specimenTypeDesignationDTO2.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
       specimenTypeDesignationDTO2.setAccessionNumber("TEST_2");

       workingset.getSpecimenTypeDesignationDTOs().add(specimenTypeDesignationDTO2);

       service.save(workingset);

       workingset = service.loadDtoByIds(registrationId, baseEntityRef);
       Assert.assertTrue(workingset.getSpecimenTypeDesignationDTOs().size() == 2);


       //FIXME this fails: Assert.assertEquals("There must only be one FieldUnit and one MediaSpecimen", 2, cdmRepository.getOccurrenceService().count(DerivedUnit.class));

       // write test data for delete test
       /*
       // printDataSetWithNull(System.err, includeTableNames_delete);
        writeDbUnitDataSetFile(includeTableNames_delete, "deleteTest");
        */
       /* The following audit table fix needs also to be added to the test data:
           <!-- Test data is being used by more than one test - need to reset a couple of *_AUD tables -->
          <AUDITEVENT />
          <TAXONNAME_AUD />
          <DERIVATIONEVENT_AUD />
          <TYPEDESIGNATIONBASE_AUD />
          <SPECIMENOROBSERVATIONBASE_DERIVATIONEVENT_AUD />
          <REGISTRATION_AUD />
          <SPECIMENOROBSERVATIONBASE_AUD />
          <TAXONNAME_TYPEDESIGNATIONBASE_AUD />
        */

    }

    @Test
    @DataSet("SpecimenTypeDesignationWorkingSetServiceImplTest-deleteTest.xml")
    @ExpectedDataSet("SpecimenTypeDesignationWorkingSetServiceImplTest.deleteTypeDesignationTest-result.xml")
    public void test03_deleteTypeDesignationTest() {

        // FieldUnit" ID="5001
        TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<FieldUnit>(FieldUnit.class, 5001, "Somewhere, FieldNumber.");
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.loadDtoByIds(registrationId, baseEntityRef);
        Assert.assertTrue(workingset.getSpecimenTypeDesignationDTOs().size() == 2);

        SpecimenTypeDesignationDTO deleteDTO = null;
        for(SpecimenTypeDesignationDTO stdDTO : workingset.getSpecimenTypeDesignationDTOs()){
            if(stdDTO.getAccessionNumber().equals("TEST_1")){
                deleteDTO = stdDTO;
                break;
            }
        }
        workingset.getSpecimenTypeDesignationDTOs().remove(deleteDTO);

        service.save(workingset);

        // printDataSetWithNull(System.err, includeTableNames_delete);

        workingset = service.loadDtoByIds(registrationId, baseEntityRef);
        Registration reg = workingset.getOwner();
        Assert.assertEquals(1, workingset.getSpecimenTypeDesignationDTOs().size());
        reg = workingset.getOwner();
        Assert.assertEquals(1, reg.getTypeDesignations().size());
    }

    @Test
    @DataSet("SpecimenTypeDesignationWorkingSetServiceImplTest-deleteTest.xml")
    public void test02_deleteWorkingset() {

//        printDataSetWithNull(System.err, includeTableNames_delete);

        TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<FieldUnit>(FieldUnit.class, 5001, null);

        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.loadDtoByIds(registrationId, baseEntityRef);
        Assert.assertNotNull(workingset.getOwner());
        Assert.assertEquals(2, workingset.getSpecimenTypeDesignationDTOs().size());
        service.delete(workingset, true);

//        UUID gatheringEventUUID = UUID.fromString("23d40440-38bb-46c1-af11-6e25dcfa0145");
//        UUID fieldUnitUUID = UUID.fromString("22be718a-6f21-4b74-aae3-bb7d7d659e1c");
//        UUID mediaSpecimenUUID = UUID.fromString("10eceb2c-9b51-458e-8dcd-2cb92cc558a9");
//        UUID specimenUUID = UUID.fromString("2e384f8e-fbb0-44eb-9d5f-1b7235493932");
//        UUID typeDesignation1UUID = UUID.fromString("a1896ae2-4396-4243-988e-3d74058b44ab");
//        UUID typeDesignation2UUID = UUID.fromString("a1896ae2-4396-4243-988e-3d74058b44ab");


        Assert.assertEquals("All TypeDesignations should have been deleted", 0, cdmRepository.getNameService().getAllTypeDesignations(10, 0).size());
        Assert.assertEquals("All derived units should have been deleted", 0, cdmRepository.getOccurrenceService().count(DerivedUnit.class));
        Assert.assertEquals("FieldUnit should have been deleted", 0, cdmRepository.getOccurrenceService().count(FieldUnit.class));
        Assert.assertEquals("Gathering event should have been deleted by orphan remove", 0, cdmRepository.getEventBaseService().count(GatheringEvent.class));
        // FIXME Assert.assertEquals("Media should have been deleted ", 0, cdmRepository.getMediaService().count(null));

        // printDataSetWithNull(System.err, includeTableNames_delete);
    }

    // ---------------------- TestData -------------------------------------------


    @Override
    // @Test
    public void createTestDataSet() throws FileNotFoundException {
        createRegistration();
    }

    private void createRegistration() throws FileNotFoundException {

        Team team = Team.NewTitledInstance("Novis, Braidwood & Kilroy", "Novis, Braidwood & Kilroy");
        Reference nomRef = ReferenceFactory.newArticle();
        nomRef = cdmRepository.getReferenceService().save(nomRef);
        publicationId = nomRef.getId();

        nomRef.setAuthorship(team);
        nomRef.setTitle("P.M. Novis, J. Braidwood & C. Kilroy, Small diatoms (Bacillariophyta) in cultures from the Styx River, New Zealand, including descriptions of three new species in Phytotaxa 64");
        TaxonName name = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Planothidium", null,  "victori", null, null, nomRef, "11-45", null);
        name = cdmRepository.getNameService().save(name);
        typifiedNameId = name.getId();

        Registration reg = Registration.NewInstance();
        reg.setName(name);
        reg = cdmRepository.getRegistrationService().save(reg);
        registrationId = reg.getId();


        //printDataSetWithNull(System.err, includeTableNames_create);

        writeDbUnitDataSetFile(includeTableNames_create);
    }







}
