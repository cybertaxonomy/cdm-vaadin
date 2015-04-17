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

import java.util.UUID;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
@DataSet
public class ConceptRelationshipPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(ConceptRelationshipPresenterTest.class);

    private static ConceptRelationshipPresenter crp;
    private static D3ConceptRelationshipTree crTree;

    @BeforeClass
    public static void init() {
        crTree = new D3ConceptRelationshipTree();
        crp = new ConceptRelationshipPresenter(crTree);
    }

    @Ignore
    @Test
    public void testRefreshRelationshipView() throws JSONException {
        UUID taxonUuid = UUID.fromString("5f713f69-e03e-4a11-8a55-700fbbf44805");
        crp.refreshRelationshipView(new IdUuidName(null, taxonUuid, null));
        String expected = "{\"name\":\"Taxon D sec. ???\",\"children\":[{\"name\":\"Includes\",\"children\":[{\"name\":\"Taxon A sec. Journal Reference 1\",\"uuid\":\"eaac797e-cac7-4649-97cf-c7b580076895\"},{\"name\":\"Taxon B sec. ???\",\"uuid\":\"77e7d93e-75c6-4dd4-850d-7b5809654378\"}],\"uuid\":\"0501c385-cab1-4fbe-b945-fc747419bb13\"},{\"name\":\"Excludes\",\"children\":[{\"name\":\"Taxon C sec. ???\",\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\"}],\"uuid\":\"4535a63c-4a3f-4d69-9350-7bf02e2c23be\"}],\"uuid\":\"5f713f69-e03e-4a11-8a55-700fbbf44805\"}";
        Assert.assertEquals(expected, crTree.getState().getConceptRelationshipTree());

        taxonUuid = UUID.fromString("3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9");
        crp.refreshRelationshipView(new IdUuidName(null, taxonUuid, null));
        expected = "{\"name\":\"Taxon C sec. ???\",\"uuid\":\"3d71c8b8-3bec-4f5f-ba23-6f9d55ef84e9\"}";
        Assert.assertEquals(expected, crTree.getState().getConceptRelationshipTree());
    }

}
