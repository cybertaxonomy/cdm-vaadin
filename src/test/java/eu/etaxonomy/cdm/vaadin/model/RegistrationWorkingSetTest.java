/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.api.service.exception.TypeDesignationSetException;
import eu.etaxonomy.cdm.model.name.NomenclaturalCode;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationType;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;

/**
 * @author a.kohlbecker
 * @since Jan 11, 2018
 *
 */
public class RegistrationWorkingSetTest {

    @Test
    public void test_2_names() throws TypeDesignationSetException {

        Reference article = ReferenceFactory.newArticle();
        article.setTitleCache("Article", true);
        article.setId(1);

        Reference section = ReferenceFactory.newSection();
        section.setTitleCache("Section", true);
        section.setInReference(article);
        section.setId(2);

        TaxonName name1 = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Amphora", null, "exemplaris", null, null, article, null, null);
        TaxonName name2 = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Amphora", null, "nonsensis", null, null, section, null, null);
        Registration reg1 = Registration.NewInstance("http://phycobank/0001", "0001", name1, null);
        Registration reg2 = Registration.NewInstance("http://phycobank/0002", "0002", name2, null);

        List<RegistrationWrapperDTO> dtos = new ArrayList<>();
        dtos.add(new RegistrationWrapperDTO(reg1));
        dtos.add(new RegistrationWrapperDTO(reg2));

        RegistrationWorkingSet ws = new RegistrationWorkingSet(dtos);
        Assert.assertEquals(article.getUuid(), ws.getPublicationUnitUuid());
        Assert.assertEquals(2, ws.getRegistrations().size());
    }


    @Test
    public void test_name_and_type() throws TypeDesignationSetException {

        Reference article = ReferenceFactory.newArticle();
        article.setTitleCache("Article", true);
        article.setId(1);

        Reference section = ReferenceFactory.newSection();
        section.setTitleCache("Section", true);
        section.setInReference(article);
        section.setId(2);

        Reference olderArticle = ReferenceFactory.newArticle();
        olderArticle.setTitleCache("Older article", true);
        olderArticle.setId(1);

        TaxonName name1 = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Amphora", null, "exemplaris", null, null, olderArticle, null, null);
        SpecimenTypeDesignation std = SpecimenTypeDesignation.NewInstance();
        std.setCitation(article);
        std.setTypeSpecimen(DerivedUnit.NewInstance(SpecimenOrObservationType.PreservedSpecimen));
        Set<TypeDesignationBase> typeDesignations = new HashSet<>();
        name1.addTypeDesignation(std, false);
        typeDesignations.add(std);
        TaxonName name2 = TaxonName.NewInstance(NomenclaturalCode.ICNAFP, Rank.SPECIES(), "Amphora", null, "nonsensis", null, null, section, null, null);
        Registration reg1 = Registration.NewInstance("http://phycobank/0001", "0001", null, typeDesignations);
        Registration reg2 = Registration.NewInstance("http://phycobank/0002", "0002", name2, null);

        List<RegistrationWrapperDTO> dtos = new ArrayList<>();
        dtos.add(new RegistrationWrapperDTO(reg1));
        dtos.add(new RegistrationWrapperDTO(reg2));

        RegistrationWorkingSet ws = new RegistrationWorkingSet(dtos);
        Assert.assertEquals(article.getUuid(), ws.getPublicationUnitUuid());
        Assert.assertEquals(2, ws.getRegistrations().size());
    }
}