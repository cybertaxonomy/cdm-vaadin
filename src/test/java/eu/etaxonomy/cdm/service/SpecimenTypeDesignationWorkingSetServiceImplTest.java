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
import java.net.URISyntaxException;
import java.util.UUID;

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
import eu.etaxonomy.cdm.api.util.DerivedUnitConversionException;
import eu.etaxonomy.cdm.common.URI;
import eu.etaxonomy.cdm.model.agent.Team;
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
import eu.etaxonomy.cdm.model.term.DefinedTerm;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;

/**
 * @author a.kohlbecker
 * @since Nov 17, 2017
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

    UUID registrationUuid = UUID.fromString("c8bb4e70-ca85-43c3-ae81-c90a2b41a93f"); // 5000;

    private UUID publicationUuid = UUID.fromString("45804c65-7df9-42fd-b43a-818a8958c264"); // 5000;

    private UUID typifiedNameUuid = UUID.fromString("47d9263e-b32a-42af-98ea-5528f154384f"); //  5000;

    UUID fieldUnitUuid = UUID.fromString("22be718a-6f21-4b74-aae3-bb7d7d659e1c"); // 5001

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

//        printDataSet(System.err, new String[]{"USERACCOUNT", "GROUPS", "USERACCOUNT_GRANTEDAUTHORITYIMPL", "USERACCOUNT_PERMISSIONGROUP"
//                , "PERMISSIONGROUP", "PERMISSIONGROUP_GRANTEDAUTHORITYIMPL", "GRANTEDAUTHORITYIMPL"});

//        printDataSet(System.err, debugTables);
//        writeDbUnitDataSetFile(new String[]{"AUDITEVENT", "DEFINEDTERMBASE", "DEFINEDTERMBASE_AUD", "DEFINEDTERMBASE_REPRESENTATION", "DEFINEDTERMBASE_REPRESENTATION_AUD",
//                "REPRESENTATION", "REPRESENTATION_AUD", "HIBERNATE_SEQUENCES"},
//                "RegistrationTerms");

       SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.create(registrationUuid, typifiedNameUuid);

       Assert.assertNotNull(workingset.getOwner());
       Assert.assertEquals(Registration.class, workingset.getOwner().getClass());

       workingset.getFieldUnit().setFieldNotes("FieldNotes");
       // int baseEntityID = workingset.getFieldUnit().getId();
       workingset.getFieldUnit().setFieldNumber("FieldNumber");
       workingset.getFieldUnit().getGatheringEvent().setLocality(LanguageString.NewInstance("Somewhere", Language.ENGLISH()));

       SpecimenTypeDesignationDTO specimenTypeDesignationDTO = new SpecimenTypeDesignationDTO();
       specimenTypeDesignationDTO.setKindOfUnit((DefinedTerm)cdmRepository.getTermService().load(KindOfUnitTerms.PUBLISHED_IMAGE().getUuid()));
       specimenTypeDesignationDTO.setMediaUri(new URI("http://foo.bar.com/image1"));
       specimenTypeDesignationDTO.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
       specimenTypeDesignationDTO.setAccessionNumber("TEST_1");

       workingset.getSpecimenTypeDesignationDTOs().add(specimenTypeDesignationDTO);

       service.save(workingset);

       SpecimenOrObservationBase<?> baseEntity = cdmRepository.getOccurrenceService().load(workingset.getFieldUnit().getUuid());

//     printDataSet(System.err, new String[]{"TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE"});

       TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<>(FieldUnit.class, baseEntity.getUuid(), baseEntity.getTitleCache());

       workingset = service.load(registrationUuid, baseEntityRef);

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

       workingset = service.load(registrationUuid, baseEntityRef);
       Assert.assertTrue(workingset.getSpecimenTypeDesignationDTOs().size() == 2);

       //FIXME this fails: Assert.assertEquals("There must only be one FieldUnit and one MediaSpecimen", 2, cdmRepository.getOccurrenceService().count(DerivedUnit.class));

       // write test data for delete test
       /*
       // printDataSet(System.err, includeTableNames_delete);
        writeDbUnitDataSetFile(includeTableNames_delete, "deleteTest");
        */
       /* The following audit table fix needs also to be added to the test data:
           <!-- Test data is being used by more than one test - need to reset a couple of *_AUD tables -->
          <AUDITEVENT />
          <TAXONNAME_AUD />
          <DERIVATIONEVENT_AUD />
          <GATHERINGEVENT_AUD />
          <LANGUAGESTRING_AUD />
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
        TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<>(FieldUnit.class, fieldUnitUuid, "Somewhere, FieldNumber.");
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.load(registrationUuid, baseEntityRef);
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

        // printDataSet(System.err, includeTableNames_delete);

        workingset = service.load(registrationUuid, baseEntityRef);
        Registration reg = workingset.getOwner();
        Assert.assertEquals(1, workingset.getSpecimenTypeDesignationDTOs().size());
        reg = workingset.getOwner();
        Assert.assertEquals(1, reg.getTypeDesignations().size());
    }

    @Test
    @DataSet("SpecimenTypeDesignationWorkingSetServiceImplTest-deleteTest.xml")
    public void test02_deleteWorkingset() {

//        printDataSet(System.err, includeTableNames_delete);

        TypedEntityReference<FieldUnit> baseEntityRef = new TypedEntityReference<>(FieldUnit.class, fieldUnitUuid, null);

        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.load(registrationUuid, baseEntityRef);
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

        // printDataSet(System.err, includeTableNames_delete);
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
        nomRef.setUuid(publicationUuid);
        nomRef = cdmRepository.getReferenceService().save(nomRef);

        nomRef.setAuthorship(team);
        nomRef.setTitle("P.M. Novis, J. Braidwood & C. Kilroy, Small diatoms (Bacillariophyta) in cultures from the Styx River, New Zealand, including descriptions of three new species in Phytotaxa 64");
        TaxonName name = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Planothidium", null,  "victori", null, null, nomRef, "11-45", null);
        name.setUuid(typifiedNameUuid);
        name = cdmRepository.getNameService().save(name);

        Registration reg = Registration.NewInstance();
        reg.setName(name);
        reg.setUuid(registrationUuid);
        reg = cdmRepository.getRegistrationService().save(reg);

        //printDataSet(System.err, includeTableNames_create);

        writeDbUnitDataSetFile(includeTableNames_create);
    }
}
