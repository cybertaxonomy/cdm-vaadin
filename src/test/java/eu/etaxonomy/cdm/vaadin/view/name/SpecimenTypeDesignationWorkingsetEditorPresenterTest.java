/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.TransactionStatus;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.utility.DerivedUnitConversionException;
import eu.etaxonomy.cdm.database.DataBaseTablePrinter;
import eu.etaxonomy.cdm.model.agent.Team;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.SpringVaadinMvpTest;
import eu.etaxonomy.vaadin.ui.view.PopupEditorFactory;

/**
 * @author a.kohlbecker
 * @since Nov 17, 2017
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    DirtiesContextTestExecutionListener.class,
//    TransactionalTestExecutionListener.class,
    DbUnitTestExecutionListener.class })
//@DatabaseTearDown("/eu/etaxonomy/cdm/database/ClearDBDataSet.xml")
public class SpecimenTypeDesignationWorkingsetEditorPresenterTest extends SpringVaadinMvpTest {

    @Autowired
    private PopupEditorFactory popupEditorFactory;

    @Autowired
    private CdmRepository cdmRepository;

    @Autowired
    private DataBaseTablePrinter dbPrinter;

    SpecimenTypeDesignationWorkingsetEditorPresenter presenter;

    int registrationId = 5000;

    private Integer publicationId = 5000;

    private Integer typifiedNameId = 5000;

    String[] includeTableNames = new String[]{"TAXONNAME", "REFERENCE", "AGENTBASE", "HOMOTYPICALGROUP", "REGISTRATION",
            "DERIVATIONEVENT", "GATHERINGEVENT", "LANGUAGESTRING", "SPECIMENOROBSERVATIONBASE", "TYPEDESIGNATIONBASE",
            "HIBERNATE_SEQUENCES"};

    @Before
    public void init() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, FileNotFoundException{
        presenter = new SpecimenTypeDesignationWorkingsetEditorPresenter();
        popupEditorFactory.injectPresenterBeans(SpecimenTypeDesignationWorkingsetEditorPresenter.class, presenter);
        // createRegistration();
    }

    private void createRegistration() throws FileNotFoundException {

        TransactionStatus tx = cdmRepository.startTransaction();
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

        tx.flush();
        cdmRepository.commitTransaction(tx);

        dbPrinter.writeDbUnitDataSetFile(includeTableNames, this.getClass());
    }

    @Test
    @DatabaseSetup(value="SpecimenTypeDesignationWorkingsetEditorPresenterTest.xml")
    public void createAndEditTest() throws DerivedUnitConversionException, FileNotFoundException {

        dbPrinter.printDataSetWithNull(System.err, includeTableNames);

/*
       TypeDesignationWorkingsetEditorIdSet idsetNew = new TypeDesignationWorkingsetEditorIdSet(registrationId, publicationId, typifiedNameId);
       SpecimenTypeDesignationWorkingSetDTO<Registration> workingset = presenter.loadBeanById(idsetNew);
       workingset.getFieldUnit().setFieldNotes("FieldNotes");
       workingset.getFieldUnit().setFieldNotes("FieldNumber");
       workingset.getFieldUnit().getGatheringEvent().setLocality(LanguageString.NewInstance("Somewhere", Language.ENGLISH()));

       SpecimenTypeDesignationDTO specimenTypeDesignationDTO = new SpecimenTypeDesignationDTO();
       specimenTypeDesignationDTO.setKindOfUnit((DefinedTerm) cdmRepository.getTermService().load(KindOfUnitTerms.SPECIMEN().getUuid()));
       specimenTypeDesignationDTO.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
       specimenTypeDesignationDTO.setAccessionNumber("TEST1234");

       workingset.getSpecimenTypeDesignationDTOs().add(specimenTypeDesignationDTO);

       presenter.saveBean(workingset);

       // dbPrinter.writeDbUnitDataSetFile(includeTableNames, this.getClass(), "deleteTest");
*/

    }







}
