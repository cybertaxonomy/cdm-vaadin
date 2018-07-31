/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import eu.etaxonomy.cdm.CdmVaadinIntegrationTest;
import eu.etaxonomy.cdm.api.service.exception.RegistrationValidationException;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.model.common.IdentifiableSource;
import eu.etaxonomy.cdm.model.media.Media;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.occurrence.DerivationEvent;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.MediaSpecimen;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class TypeDesignationSetManagerIT extends CdmVaadinIntegrationTest {


    private NameTypeDesignation ntd;
    private SpecimenTypeDesignation std_IT;
    private SpecimenTypeDesignation std_HT;
    private SpecimenTypeDesignation std_IT_2;
    private SpecimenTypeDesignation std_IT_3;
    private SpecimenTypeDesignation mtd_HT_published;
    private SpecimenTypeDesignation mtd_IT_unpublished;

    @Before
    public void init(){

        ntd = NameTypeDesignation.NewInstance();
        ntd.setId(1);
        TaxonName typeName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typeName.setTitleCache("Prionus L.", true);
        ntd.setTypeName(typeName);
        Reference citation = ReferenceFactory.newGeneric();
        citation.setTitleCache("Species Plantarum", true);
        ntd.setCitation(citation);

        FieldUnit fu_1 = FieldUnit.NewInstance();
        fu_1.setId(1);
        fu_1.setTitleCache("Testland, near Bughausen, A.Kohlbecker 81989, 2017", true);

        FieldUnit fu_2 = FieldUnit.NewInstance();
        fu_2.setId(2);
        fu_2.setTitleCache("Dreamland, near Kissingen, A.Kohlbecker 66211, 2017", true);

        std_HT = SpecimenTypeDesignation.NewInstance();
        std_HT.setId(1);
        DerivedUnit specimen_HT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_HT.setTitleCache("OHA", true);
        createDerivationEvent(fu_1, specimen_HT);
        specimen_HT.getOriginals().add(fu_1);
        std_HT.setTypeSpecimen(specimen_HT);
        std_HT.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());

        std_IT = SpecimenTypeDesignation.NewInstance();
        std_IT.setId(2);
        DerivedUnit specimen_IT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT.setTitleCache("BER", true);
        createDerivationEvent(fu_1, specimen_IT);
        std_IT.setTypeSpecimen(specimen_IT);
        std_IT.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());

        std_IT_2 = SpecimenTypeDesignation.NewInstance();
        std_IT_2.setId(3);
        DerivedUnit specimen_IT_2 = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT_2.setTitleCache("KEW", true);
        createDerivationEvent(fu_1, specimen_IT_2);
        std_IT_2.setTypeSpecimen(specimen_IT_2);
        std_IT_2.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());

        std_IT_3 = SpecimenTypeDesignation.NewInstance();
        std_IT_3.setId(4);
        DerivedUnit specimen_IT_3 = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT_3.setTitleCache("M", true);
        createDerivationEvent(fu_1, specimen_IT_3);
        std_IT_3.setTypeSpecimen(specimen_IT_3);
        std_IT_3.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());

        mtd_HT_published = SpecimenTypeDesignation.NewInstance();
        mtd_HT_published.setId(5);
        MediaSpecimen mediaSpecimen_published = (MediaSpecimen)DerivedUnit.NewInstance(SpecimenOrObservationType.Media);
        Media media = Media.NewInstance();
        Reference ref = ReferenceFactory.newGeneric();
        ref.setTitleCache("A.K. & W.K (2008) Algae of the BGBM", true);
        media.addSource(IdentifiableSource.NewPrimaryMediaSourceInstance(ref, "p.33"));
        mediaSpecimen_published.setMediaSpecimen(media);
        createDerivationEvent(fu_1, mediaSpecimen_published);
        mtd_HT_published.setTypeSpecimen(mediaSpecimen_published);
        mtd_HT_published.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());

        mtd_IT_unpublished = SpecimenTypeDesignation.NewInstance();
        mtd_IT_unpublished.setId(6);
        MediaSpecimen mediaSpecimen_unpublished = (MediaSpecimen)DerivedUnit.NewInstance(SpecimenOrObservationType.Media);
        eu.etaxonomy.cdm.model.occurrence.Collection collection = eu.etaxonomy.cdm.model.occurrence.Collection.NewInstance();
        collection.setCode("B");
        mediaSpecimen_unpublished.setCollection(collection);
        mediaSpecimen_unpublished.setAccessionNumber("Slide A565656");
        createDerivationEvent(fu_1, mediaSpecimen_unpublished);
        mtd_IT_unpublished.setTypeSpecimen(mediaSpecimen_unpublished);
        mtd_IT_unpublished.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());

    }

    /**
     * @param fu_1
     * @param specimen_IT_2
     */
    protected void createDerivationEvent(FieldUnit fu_1, DerivedUnit specimen_IT_2) {
        DerivationEvent derivationEvent_3 = DerivationEvent.NewInstance();
        derivationEvent_3.addOriginal(fu_1);
        derivationEvent_3.addDerivative(specimen_IT_2);
    }

    @Test
    public void test1() throws RegistrationValidationException{

        List<TypeDesignationBase> tds = new ArrayList<>();
        tds.add(ntd);
        tds.add(std_IT);
        tds.add(std_HT);
        tds.add(std_IT_2);
        tds.add(std_IT_3);

        TaxonName typifiedName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typifiedName.setTitleCache("Prionus coriatius L.", true);

        typifiedName.addTypeDesignation(ntd, false);
        typifiedName.addTypeDesignation(std_HT, false);
        typifiedName.addTypeDesignation(std_IT, false);
        typifiedName.addTypeDesignation(std_IT_2, false);
        typifiedName.addTypeDesignation(std_IT_3, false);

        TypeDesignationSetManager typeDesignationManager = new TypeDesignationSetManager(tds);
        String result = typeDesignationManager.print();

        Logger.getLogger(this.getClass()).debug(result);
        assertNotNull(result);
        assertEquals(
                "Prionus coriatius L. Type: Testland, near Bughausen, A.Kohlbecker 81989, 2017 Holotype, OHA; Isotypes: BER, KEW; Type: Isotype, M; NameType: Prionus L. Species Plantarum"
                , result
                );

        LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> orderedTypeDesignations =
                typeDesignationManager.getOrderdTypeDesignationWorkingSets();
        Map<TypeDesignationStatusBase<?>, Collection<TypedEntityReference>> byStatusMap = orderedTypeDesignations.values().iterator().next();
        Iterator<TypeDesignationStatusBase<?>> keyIt = byStatusMap.keySet().iterator();
        assertEquals("Holotype", keyIt.next().getLabel());
        assertEquals("Isotype", keyIt.next().getLabel());
    }

    @Test
    public void test2() throws RegistrationValidationException{

        TaxonName typifiedName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typifiedName.setTitleCache("Prionus coriatius L.", true);

        TypeDesignationSetManager typeDesignationManager = new TypeDesignationSetManager(typifiedName);
        String result = typeDesignationManager.print();
        Logger.getLogger(this.getClass()).debug(result);
        assertNotNull(result);
        assertEquals(
                "Prionus coriatius L."
                , result
                );

        typifiedName.addTypeDesignation(ntd, false);
        typeDesignationManager.addTypeDesigations(null, ntd);

        assertEquals(
                "Prionus coriatius L. NameType: Prionus L. Species Plantarum"
                , typeDesignationManager.print()
                );

        typifiedName.addTypeDesignation(std_HT, false);
        typeDesignationManager.addTypeDesigations(null, std_HT);

        assertEquals(
                "Prionus coriatius L. Type: Testland, near Bughausen, A.Kohlbecker 81989, 2017 Holotype, OHA; NameType: Prionus L. Species Plantarum"
                , typeDesignationManager.print()
                );

    }

    @Test
    public void test_mediaType(){

        for(int i = 0; i < 10; i++ ){

            init();
            // repeat 10 times to assure the order of typedesignations is fix in the representations
            TaxonName typifiedName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
            typifiedName.setTitleCache("Prionus coriatius L.", true);
            typifiedName.addTypeDesignation(mtd_HT_published, false);
            typifiedName.addTypeDesignation(mtd_IT_unpublished, false);

            TypeDesignationSetManager typeDesignationManager = new TypeDesignationSetManager(typifiedName);
            typeDesignationManager.addTypeDesigations(null, mtd_HT_published);
            typeDesignationManager.addTypeDesigations(null, mtd_IT_unpublished);

            assertEquals("failed after repreating " + i + " times",
                    "Prionus coriatius L. Type: Testland, near Bughausen, A.Kohlbecker 81989, 2017 Holotype, [icon] p.33 in A.K. & W.K (2008) Algae of the BGBM; Isotype, [icon] (B Slide A565656)."
                    , typeDesignationManager.print()
                    );
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTestDataSet() throws FileNotFoundException {
        // TODO Auto-generated method stub

    }
}

