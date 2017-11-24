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
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
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
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.MediaSpecimen;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;

/**
 * @author a.kohlbecker
 * @since Nov 17, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetEditorPresenterTest extends CdmVaadinIntegrationTest{

    @SpringBeanByName
    private CdmRepository cdmRepository;

    @SpringBeanByType
    private ISpecimenTypeDesignationWorkingSetService service;

    int registrationId = 5000;

    private Integer publicationId = 5000;

    private Integer typifiedNameId = 5000;

    String[] includeTableNames_create = new String[]{"TAXONNAME", "REFERENCE", "AGENTBASE", "HOMOTYPICALGROUP", "REGISTRATION",
            "HIBERNATE_SEQUENCES"};

    String[] includeTableNames_delete = new String[]{"TAXONNAME", "REFERENCE", "AGENTBASE", "HOMOTYPICALGROUP", "REGISTRATION",
            "DERIVATIONEVENT", "GATHERINGEVENT", "LANGUAGESTRING", "SPECIMENOROBSERVATIONBASE", "TYPEDESIGNATIONBASE",
            "REGISTRATION_TYPEDESIGNATIONBASE", "TAXONNAME_TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE_DERIVATIONEVENT",
            "MEDIA", "MEDIA_REPRESENTATION", "MEDIAREPRESENTATION", "MEDIAREPRESENTATIONPART",
            "HIBERNATE_SEQUENCES"
            };


    @Test
    @DataSet
    public void createAndEditTest() throws DerivedUnitConversionException, URISyntaxException, FileNotFoundException {

       //printDataSetWithNull(System.err, debugTables);
//        writeDbUnitDataSetFile(new String[]{"AUDITEVENT", "DEFINEDTERMBASE", "DEFINEDTERMBASE_AUD", "DEFINEDTERMBASE_REPRESENTATION", "DEFINEDTERMBASE_REPRESENTATION_AUD",
//                "REPRESENTATION", "REPRESENTATION_AUD", "HIBERNATE_SEQUENCES"},
//                "RegistrationTerms");

       SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.create(registrationId, publicationId, typifiedNameId);

       Assert.assertNotNull(workingset.getOwner());
       Assert.assertEquals(Registration.class, workingset.getOwner().getClass());

       workingset.getFieldUnit().setFieldNotes("FieldNotes");
       workingset.getFieldUnit().setFieldNumber("FieldNumber");
       workingset.getFieldUnit().getGatheringEvent().setLocality(LanguageString.NewInstance("Somewhere", Language.ENGLISH()));

       SpecimenTypeDesignationDTO specimenTypeDesignationDTO = new SpecimenTypeDesignationDTO();
       specimenTypeDesignationDTO.setKindOfUnit((DefinedTerm)cdmRepository.getTermService().load(KindOfUnitTerms.PUBLISHED_IMAGE().getUuid()));
       specimenTypeDesignationDTO.setMediaUri(new URI("http://foo.bar.com/image1"));
       specimenTypeDesignationDTO.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
       specimenTypeDesignationDTO.setAccessionNumber("TEST_1");

       workingset.getSpecimenTypeDesignationDTOs().add(specimenTypeDesignationDTO);

       service.save(workingset);

       printDataSetWithNull(System.err, new String[]{"TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE"});

       workingset = service.loadDtoByIds(registrationId, 0);

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

       workingset = service.loadDtoByIds(registrationId, 0);
       Assert.assertTrue(workingset.getSpecimenTypeDesignationDTOs().size() == 2);


       //FIXME this fails: Assert.assertEquals("There must only be one FieldUnit and one MediaSpecimen", 2, cdmRepository.getOccurrenceService().count(DerivedUnit.class));

       // write test data for delete test
       /*
       // printDataSetWithNull(System.err, includeTableNames_delete);
        writeDbUnitDataSetFile(includeTableNames_delete, "deleteTest");
        */

    }

    @Test
    @DataSet("SpecimenTypeDesignationWorkingsetEditorPresenterTest-deleteTest.xml")
    @Ignore
    public void deleteTypeDesignationTest() {

        // printDataSetWithNull(System.err, includeTableNames_delete);

        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.loadDtoByIds(registrationId, 0);
        Assert.assertTrue(workingset.getSpecimenTypeDesignationDTOs().size() == 2);

        SpecimenTypeDesignationDTO deleteDTO = null;
        for(SpecimenTypeDesignationDTO stdDTO : workingset.getSpecimenTypeDesignationDTOs()){
            if(stdDTO.getAccessionNumber().equals("TEST_1")){
                deleteDTO = stdDTO;
                break;
            }
        }
        workingset.getSpecimenTypeDesignationDTOs().remove(deleteDTO);

        // TODO once https://dev.e-taxonomy.eu/redmine/issues/7077 is fixed dissociating from the Registration could be removed here
        Registration reg = workingset.getOwner();
        SpecimenTypeDesignation std = deleteDTO.asSpecimenTypeDesignation();
        reg.getTypeDesignations().remove(std);

        service.save(workingset);

        printDataSetWithNull(System.err, new String[]{"TYPEDESIGNATIONBASE", "SPECIMENOROBSERVATIONBASE"});

        workingset = service.loadDtoByIds(registrationId, 0);
        Assert.assertEquals(1, workingset.getSpecimenTypeDesignationDTOs().size());

    }

    @Test
    @DataSet("SpecimenTypeDesignationWorkingsetEditorPresenterTest-deleteTest.xml")
    @Ignore
    public void deleteWorkingsetTest() {

        SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = service.loadDtoByIds(registrationId, 0);
        //TODO implement ...


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


        printDataSetWithNull(System.err, includeTableNames_create);

        writeDbUnitDataSetFile(includeTableNames_create);
    }







}
