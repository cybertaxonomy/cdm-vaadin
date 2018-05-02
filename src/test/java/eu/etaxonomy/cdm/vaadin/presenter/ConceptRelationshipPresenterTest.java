/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;

import eu.etaxonomy.cdm.api.application.ICdmRepository;
import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.test.unitils.CleanSweepInsertLoadStrategy;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.component.taxon.ConceptRelationshipPresenter;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree.Direction;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @since 9 Apr 2015
 *
 */
@DataSets({
    @DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class),
    @DataSet("/eu/etaxonomy/cdm/database/FirstData_UsersAndPermissions.xml")
})
public class ConceptRelationshipPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(ConceptRelationshipPresenterTest.class);

    private static ConceptRelationshipPresenter crp;
    private static D3ConceptRelationshipTree crTree;

    private static INameService nameService;

    private static ICdmRepository app;

    @BeforeClass
    public static void init() {
        crTree = new D3ConceptRelationshipTree();
        crp = new ConceptRelationshipPresenter(crTree);

        nameService = CdmSpringContextHelper.getNameService();
        app = CdmSpringContextHelper.getApplicationConfiguration();
    }


    @Test
    @Ignore
    public void testRefreshRelationshipView() throws JSONException {
        UUID taxonUuid = UUID.fromString("5f713f69-e03e-4a11-8a55-700fbbf44805");
        crp.refreshRelationshipView(new IdUuidName(30, taxonUuid, null), Direction.LEFT_RIGHT);
        String expected = "{\"direction\":\"left-right\",\"name\":\"T. d\",\"children\":[{\"name\":\"Congruent to\",\"children\":[{\"name\":\"Taxon e\",\"uuid\":\"84e99e24-f50a-4726-92d0-6088430c492a\",\"type\":\"ttaxon\"}],\"uuid\":\"511f504b-ae3b-4f04-b7b9-35c222f06e10\",\"type\":\"conceptr\"},{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon a\",\"uuid\":\"eaac797e-cac7-4649-97cf-c7b580076895\",\"type\":\"ttaxon\"}],\"uuid\":\"0e8b7922-974d-4389-b71e-af6fc9f98c56\",\"type\":\"conceptr\"},{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon b\",\"uuid\":\"5004a8e7-b907-4744-b67e-44ccb057ab3b\",\"type\":\"ttaxon\"}],\"uuid\":\"6fd9947e-21c3-4190-8748-57d9661e8659\",\"type\":\"conceptr\"},{\"name\":\"Excludes\",\"children\":[{\"name\":\"Taxon c\",\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\",\"type\":\"ttaxon\"}],\"uuid\":\"cc761030-38d2-4b5d-954d-32329c0ea106\",\"type\":\"conceptr\"}],\"uuid\":\"5f713f69-e03e-4a11-8a55-700fbbf44805\",\"type\":\"ftaxon\"}";
        logger.warn(crTree.getState().getConceptRelationshipTree());
        Assert.assertEquals(expected, crTree.getState().getConceptRelationshipTree());

        taxonUuid = UUID.fromString("3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9");
        crp.refreshRelationshipView(new IdUuidName(20, taxonUuid, null), Direction.RIGHT_LEFT);
        expected = "{\"direction\":\"right-left\",\"name\":\"T. c\",\"children\":[],\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\",\"type\":\"ftaxon\"}";
        logger.warn(crTree.getState().getConceptRelationshipTree());
        Assert.assertEquals(expected, crTree.getState().getConceptRelationshipTree());
    }

    @Test
    public void testAbbreviatedNameGeneration() {

        // TransactionStatus tx = app.startTransaction();
        UUID nameUuid = UUID.fromString("7ebe3f1f-c383-4611-95da-4ee633a12d3a");
        TaxonName name = nameService.load(nameUuid);
        name = CdmBase.deproxy(name);

        String abbreviatedName = crTree.getAbbreviatedName(name);
        Assert.assertEquals("T. × withverylongspecificepithet subsp.", abbreviatedName);
        // app.commitTransaction(tx);

    }


}
