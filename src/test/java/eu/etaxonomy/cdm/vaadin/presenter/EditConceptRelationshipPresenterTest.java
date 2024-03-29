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
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.test.unitils.CleanSweepInsertLoadStrategy;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.component.taxon.EditConceptRelationshipPresenter;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @since 13 Apr 2015
 */
@DataSets({
    @DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class),
    @DataSet("/eu/etaxonomy/cdm/database/FirstData_UsersAndPermissions.xml")
})
public class EditConceptRelationshipPresenterTest extends CdmVaadinBaseTest {

    @SuppressWarnings("unused")
    private static final Logger logger = LogManager.getLogger();

    public static EditConceptRelationshipPresenter conceptRelationshipPresenter;

    private static List<String> FROM_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
            "relationsFromThisTaxon"
    });

    @BeforeClass
    public static void init() {
        conceptRelationshipPresenter = new EditConceptRelationshipPresenter();
    }

    @Test
    public void testLoadTaxonRelationshipTypeContainer() throws SQLException {
        CdmSQLContainer container = conceptRelationshipPresenter.loadTaxonRelationshipTypeContainer();
        Assert.assertEquals(30, container.size());
    }

    @Test
    public void testCreateRelationship() {
        UUID fromTaxonUuid = UUID.fromString("77e7d93e-75c6-4dd4-850d-7b5809654378");
        UUID toTaxonUuid = UUID.fromString("5004a8e7-b907-4744-b67e-44ccb057ab3b");
        UUID relTypeUuid = UUID.fromString("60974c98-64ab-4574-bb5c-c110f6db634d");

        IdUuidName trId =conceptRelationshipPresenter.createRelationship(fromTaxonUuid, relTypeUuid, toTaxonUuid);

        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(1, taxon.getRelationsFromThisTaxon().size());

        Assert.assertEquals(trId.getUuid(),taxon.getRelationsFromThisTaxon().iterator().next().getUuid());
    }

    @Test
    public void testUpdateRelationship() {
        UUID fromTaxonUuid = UUID.fromString("666b484f-dc1e-4578-b404-86bc6d2e47fa");
        // RelType "Not Congruent to" id=864 before: (935)
        UUID taxonRelUuid = UUID.fromString("9634d870-bab1-4fdc-8845-c7e71aa8dc6b");
        UUID newToTaxonUuid = UUID.fromString("5004a8e7-b907-4744-b67e-44ccb057ab3b");
        // RelType "Contradiction"
        UUID newRelTypeUuid = UUID.fromString("a8f03491-2ad6-4fae-a04c-2a4c117a2e9b");

        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        TaxonRelationship tr = getFromRelwithUuid(taxon, taxonRelUuid);

        UUID oldToTaxonUuid = tr.getToTaxon().getUuid();
        UUID oldRelTypeUuid = tr.getType().getUuid();

        Assert.assertNotNull(tr);
        Assert.assertNotEquals(newToTaxonUuid, oldToTaxonUuid);
        Assert.assertNotEquals(newRelTypeUuid, oldRelTypeUuid);

        // change both to taxon and relationship type
        conceptRelationshipPresenter.updateRelationship(fromTaxonUuid, taxonRelUuid, newRelTypeUuid, newToTaxonUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(newToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(newRelTypeUuid, tr.getType().getUuid());

        // reset old values
        conceptRelationshipPresenter.updateRelationship(fromTaxonUuid, taxonRelUuid, oldRelTypeUuid, oldToTaxonUuid);

        // change only relationship type
        conceptRelationshipPresenter.updateRelationship(fromTaxonUuid, taxonRelUuid, newRelTypeUuid, null);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(oldToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(newRelTypeUuid, tr.getType().getUuid());

        // reset old values
        conceptRelationshipPresenter.updateRelationship(fromTaxonUuid, taxonRelUuid, oldRelTypeUuid, oldToTaxonUuid);

        // change only to taxon
        conceptRelationshipPresenter.updateRelationship(fromTaxonUuid, taxonRelUuid, null, newToTaxonUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        tr = getFromRelwithUuid(taxon, taxonRelUuid);

        Assert.assertNotNull(tr);
        Assert.assertEquals(newToTaxonUuid, tr.getToTaxon().getUuid());
        Assert.assertEquals(oldRelTypeUuid, tr.getType().getUuid());
    }

    @Test
    public void testDeleteRelationship() {
        UUID fromTaxonUuid = UUID.fromString("5f713f69-e03e-4a11-8a55-700fbbf44805");

        // RelType "Not Included in" id=865 before (924)
        UUID taxonRelUuid = UUID.fromString("cac9fa65-9b15-445f-80e4-56f77952f7ec");

        conceptRelationshipPresenter.deleteRelationship(fromTaxonUuid, taxonRelUuid);
        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(4, taxon.getRelationsFromThisTaxon().size());

        TaxonRelationship tr = getFromRelwithUuid(taxon, taxonRelUuid);
        Assert.assertNull(tr);

        fromTaxonUuid = UUID.fromString("666b484f-dc1e-4578-b404-86bc6d2e47fa");
        // Reltype "Includes or Overlaps or Excludes" id = 866 (before: 934)
        taxonRelUuid = UUID.fromString("9634d870-bab1-4fdc-8845-c7e71aa8dc6b");

        conceptRelationshipPresenter.deleteRelationship(fromTaxonUuid, taxonRelUuid);
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(fromTaxonUuid,FROM_TAXON_INIT_STRATEGY),Taxon.class);
        Assert.assertEquals(0, taxon.getRelationsFromThisTaxon().size());
    }


    @Test
    public void testGetRelTypeToTaxonIunMap() {
        UUID fromTaxonUuid = UUID.fromString("5f713f69-e03e-4a11-8a55-700fbbf44805");
        UUID taxonRelUuid = UUID.fromString("cc761030-38d2-4b5d-954d-32329c0ea106");
        Map<String, IdUuidName> map = conceptRelationshipPresenter.getRelTypeToTaxonIunMap(fromTaxonUuid, taxonRelUuid);

        IdUuidName relTypeIun = map.get(EditConceptRelationshipPresenter.REL_TYPE_KEY);
        Assert.assertEquals(865, relTypeIun.getId());

        IdUuidName toTaxonIun = map.get(EditConceptRelationshipPresenter.TO_TAXON_KEY);
        Assert.assertEquals(20, toTaxonIun.getId());
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