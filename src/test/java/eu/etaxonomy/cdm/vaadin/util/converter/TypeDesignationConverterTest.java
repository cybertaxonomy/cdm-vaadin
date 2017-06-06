/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
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

        SpecimenTypeDesignation std = SpecimenTypeDesignation.NewInstance();
        DerivedUnit specimen = DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen);
        specimen.setTitleCache("OHA", true);
        std.setTypeSpecimen(specimen);
        std.setTypeStatus(SpecimenTypeDesignationStatus.HOLOTYPE());

        typifiedName.addTypeDesignation(std, false);

        List<TypeDesignationBase> tds = new ArrayList<>();
        tds.add(ntd);
        tds.add(std);

        String result = new TypeDesignationConverter(tds).buildString().print();
        Logger.getLogger(this.getClass()).debug(result);
        assertNotNull(result);
    }

}
