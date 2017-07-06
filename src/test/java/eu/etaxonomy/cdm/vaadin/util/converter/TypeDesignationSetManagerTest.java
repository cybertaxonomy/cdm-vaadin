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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

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
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class TypeDesignationSetManagerTest extends CdmVaadinBaseTest{


    @Test
    public void test1() throws RegistrationValidationException{


        TaxonName typifiedName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typifiedName.setTitleCache("Prionus coriatius L.", true);

        NameTypeDesignation ntd = NameTypeDesignation.NewInstance();
        ntd.setId(1);
        TaxonName typeName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typeName.setTitleCache("Prionus L.", true);
        ntd.setTypeName(typeName);
        Reference citation = ReferenceFactory.newGeneric();
        citation.setTitleCache("Species Platarum", true);
        ntd.setCitation(citation);
        typifiedName.addTypeDesignation(ntd, false);

        FieldUnit fu_1 = FieldUnit.NewInstance();
        fu_1.setId(1);
        fu_1.setTitleCache("Testland, near Bughausen, A.Kohlbecker 81989, 2017", true);

        FieldUnit fu_2 = FieldUnit.NewInstance();
        fu_2.setId(2);
        fu_2.setTitleCache("Dreamland, near Kissingen, A.Kohlbecker 66211, 2017", true);

        SpecimenTypeDesignation std_HT = SpecimenTypeDesignation.NewInstance();
        std_HT.setId(1);
        DerivedUnit specimen_HT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_HT.setTitleCache("OHA", true);
        DerivationEvent derivationEvent_1 = DerivationEvent.NewInstance();
        derivationEvent_1.addOriginal(fu_1);
        derivationEvent_1.addDerivative(specimen_HT);
        specimen_HT.getOriginals().add(fu_1);
        std_HT.setTypeSpecimen(specimen_HT);
        std_HT.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
        typifiedName.addTypeDesignation(std_HT, false);

        SpecimenTypeDesignation std_IT = SpecimenTypeDesignation.NewInstance();
        std_IT.setId(2);
        DerivedUnit specimen_IT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT.setTitleCache("BER", true);
        DerivationEvent derivationEvent_2 = DerivationEvent.NewInstance();
        derivationEvent_2.addOriginal(fu_1);
        derivationEvent_2.addDerivative(specimen_IT);
        std_IT.setTypeSpecimen(specimen_IT);
        std_IT.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());
        typifiedName.addTypeDesignation(std_IT, false);

        SpecimenTypeDesignation std_IT_2 = SpecimenTypeDesignation.NewInstance();
        std_IT_2.setId(3);
        DerivedUnit specimen_IT_2 = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT_2.setTitleCache("KEW", true);
        DerivationEvent derivationEvent_3 = DerivationEvent.NewInstance();
        derivationEvent_3.addOriginal(fu_1);
        derivationEvent_3.addDerivative(specimen_IT_2);
        std_IT_2.setTypeSpecimen(specimen_IT_2);
        std_IT_2.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());
        typifiedName.addTypeDesignation(std_IT_2, false);

        SpecimenTypeDesignation std_IT_3 = SpecimenTypeDesignation.NewInstance();
        std_IT_3.setId(4);
        DerivedUnit specimen_IT_3 = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT_3.setTitleCache("M", true);
        std_IT_3.setTypeSpecimen(specimen_IT_3);
        std_IT_3.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());
        typifiedName.addTypeDesignation(std_IT_3, false);

        List<TypeDesignationBase> tds = new ArrayList<>();
        tds.add(ntd);
        tds.add(std_IT);
        tds.add(std_HT);
        tds.add(std_IT_2);
        tds.add(std_IT_3);

        TypeDesignationSetManager typeDesignationManager = new TypeDesignationSetManager(typifiedName, tds);
        String result = typeDesignationManager.buildString().print();
        System.err.println(result);

        Logger.getLogger(this.getClass()).debug(result);
        assertNotNull(result);
        assertEquals(
                "Prionus coriatius L. Type: Testland, near Bughausen, A.Kohlbecker 81989, 2017 (Holotype, OHA; Isotypes: BER, KEW); Type: (Isotype, M); NameType: Prionus L. Species Platarum"
                , result
                );

        LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> orderedTypeDesignations =
                typeDesignationManager.getOrderdTypeDesignationWorkingSets();
        Map<TypeDesignationStatusBase<?>, Collection<EntityReference>> byStatusMap = orderedTypeDesignations.values().iterator().next();
        Iterator<TypeDesignationStatusBase<?>> keyIt = byStatusMap.keySet().iterator();
        assertEquals("Holotype", keyIt.next().getLabel());
        assertEquals("Isotype", keyIt.next().getLabel());
    }

}
