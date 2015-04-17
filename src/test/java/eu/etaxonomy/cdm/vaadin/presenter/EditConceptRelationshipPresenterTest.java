// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @date 13 Apr 2015
 *
 */
@DataSet
public class EditConceptRelationshipPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(EditConceptRelationshipPresenterTest.class);
    public static EditConceptRelationshipPresenter ecrp;

    private static List<String> FROM_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
            "relationsFromThisTaxon"
    });

    @BeforeClass
    public static void init() {
        ecrp = new EditConceptRelationshipPresenter();
    }

    @Test
    public void testLoadTaxonRelationshipTypeContainer() throws SQLException {
        CdmSQLContainer container = ecrp.loadTaxonRelationshipTypeContainer();
        Assert.assertEquals(27, container.size());
    }


    @Test
    public void testCreateRelationship() {
        UUID fromTaxonUuid = UUID.fromString("77e7d93e-75c6-4dd4-850d-7b5809654378");
        UUID toTaxonUuid = UUID.fromString("5004a8e7-b907-4744-b67e-44ccb057ab3b");
        UUID relTypeUuid = UUID.fromString("60974c98-64ab-4574-bb5c-c110f6db634d");

        IdUuidName trId =ecrp.createRelationship(fromTaxonUuid, relTypeUuid, toTaxonUuid);

        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(1, taxon.getRelationsFromThisTaxon().size());

        Assert.assertEquals(trId.getUuid(),taxon.getRelationsFromThisTaxon().iterator().next().getUuid());
    }

    @Test
    public void testUpdateRelationship() {
        UUID fromTaxonUuid = UUID.fromString("666b484f-dc1e-4578-b404-86bc6d2e47fa");
        UUID taxonRelUuid = UUID.fromString("9634d870-bab1-4fdc-8845-c7e71aa8dc6b");
        UUID newToTaxonUuid = UUID.fromString("5004a8e7-b907-4744-b67e-44ccb057ab3b");
        UUID newRelTypeUuid = UUID.fromString("a8f03491-2ad6-4fae-a04c-2a4c117a2e9b");

        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        TaxonRelationship tr = getFromRelwithUuid(taxon, taxonRelUuid);

        UUID oldToTaxonUuid = tr.getToTaxon().getUuid();
        UUID oldRelTypeUuid = tr.getType().getUuid();

        Assert.assertNotNull(tr);
        Assert.assertNotEquals(newToTaxonUuid, oldToTaxonUuid);
        Assert.assertNotEquals(newRelTypeUuid, oldRelTypeUuid);

        // change both to taxon and relationship type
       ecrp.updateRelationship(fromTaxonUuid, taxonRelUuid, newRelTypeUuid, newToTaxonUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(newToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(newRelTypeUuid, tr.getType().getUuid());

        // reset old values
       ecrp.updateRelationship(fromTaxonUuid, taxonRelUuid, oldRelTypeUuid, oldToTaxonUuid);

        // change only relationship type
       ecrp.updateRelationship(fromTaxonUuid, taxonRelUuid, newRelTypeUuid, null);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(oldToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(newRelTypeUuid, tr.getType().getUuid());

        // reset old values
       ecrp.updateRelationship(fromTaxonUuid, taxonRelUuid, oldRelTypeUuid, oldToTaxonUuid);

        // change only to taxon
       ecrp.updateRelationship(fromTaxonUuid, taxonRelUuid, null, newToTaxonUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(newToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(oldRelTypeUuid, tr.getType().getUuid());

    }

    @Test
    public void testDeleteRelationship() {
        UUID fromTaxonUuid = UUID.fromString("5f713f69-e03e-4a11-8a55-700fbbf44805");
        UUID taxonRelUuid = UUID.fromString("cac9fa65-9b15-445f-80e4-56f77952f7ec");

       ecrp.deleteRelationship(fromTaxonUuid, taxonRelUuid);
        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(3, taxon.getRelationsFromThisTaxon().size());

        TaxonRelationship tr = getFromRelwithUuid(taxon, taxonRelUuid);
        Assert.assertNull(tr);

        fromTaxonUuid = UUID.fromString("666b484f-dc1e-4578-b404-86bc6d2e47fa");
        taxonRelUuid = UUID.fromString("9634d870-bab1-4fdc-8845-c7e71aa8dc6b");

       ecrp.deleteRelationship(fromTaxonUuid, taxonRelUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(0, taxon.getRelationsFromThisTaxon().size());
    }

    public TaxonRelationship getFromRelwithUuid(Taxon taxon, UUID taxonRelUuid) {
        for(TaxonRelationship tr : taxon.getRelationsFromThisTaxon()) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                return tr;
            }
        }
        return null;
    }
}
