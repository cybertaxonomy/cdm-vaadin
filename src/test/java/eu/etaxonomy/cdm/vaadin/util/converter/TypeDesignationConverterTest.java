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
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class TypeDesignationConverterTest extends CdmVaadinBaseTest{


    @Test
    public void test1() throws RegistrationValidationException{

        TaxonName typifiedName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typifiedName.setTitleCache("Prionus coriatius L.", true);

        NameTypeDesignation ntd = NameTypeDesignation.NewInstance();
        TaxonName typeName = TaxonNameFactory.NewBacterialInstance(Rank.SPECIES());
        typeName.setTitleCache("Prionus L.", true);
        ntd.setTypeName(typeName);
        Reference citation = ReferenceFactory.newGeneric();
        citation.setTitleCache("Species Platarum", true);
        ntd.setCitation(citation);

        typifiedName.addTypeDesignation(ntd, false);

        SpecimenTypeDesignation std_HT = SpecimenTypeDesignation.NewInstance();
        DerivedUnit specimen_HT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_HT.setTitleCache("OHA", true);
        std_HT.setTypeSpecimen(specimen_HT);
        std_HT.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());
        typifiedName.addTypeDesignation(std_HT, false);

        SpecimenTypeDesignation std_IT = SpecimenTypeDesignation.NewInstance();
        DerivedUnit specimen_IT = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen_IT.setTitleCache("BER", true);
        std_IT.setTypeSpecimen(specimen_IT);
        std_IT.setTypeStatus(SpecimenTypeDesignationStatus.ISOTYPE());
        typifiedName.addTypeDesignation(std_IT, false);

        List<TypeDesignationBase> tds = new ArrayList<>();
        tds.add(ntd);
        tds.add(std_IT);
        tds.add(std_HT);

        TypeDesignationConverter typeDesignationConverter = new TypeDesignationConverter(tds);
        String result = typeDesignationConverter.buildString().print();
        Logger.getLogger(this.getClass()).debug(result);
        assertNotNull(result);
        Iterator<String> keyIt = typeDesignationConverter.getOrderedTypeDesignationRepresentations().keySet().iterator();
        assertEquals("Holotype", keyIt.next());
        assertEquals("Isotype", keyIt.next());
    }

}
