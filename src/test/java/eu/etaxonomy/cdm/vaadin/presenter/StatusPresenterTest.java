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
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import com.vaadin.data.Item;
import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite;


/**
 * @author cmathew
 * @date 10 Mar 2015
 */
@DataSet
public class StatusPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(StatusPresenterTest.class);

    private static StatusPresenter sp;

    @BeforeClass
    public static void init() {
        sp = new StatusPresenter();
    }



    @Test
    public void testLoadTaxa() throws SQLException {
        LeafNodeTaxonContainer container = sp.loadTaxa(11);

        Collection<?> itemIds = container.rootItemIds();

        Assert.assertEquals(3, itemIds.size());

        sp.setUnplacedFilter();
        itemIds = container.getItemIds();
        Assert.assertEquals(1, itemIds.size());

        sp.removeUnplacedFilter();
        itemIds = container.getItemIds();
        Assert.assertEquals(3, itemIds.size());

        sp.setNameFilter("Taxon A");
        itemIds = container.getItemIds();
        Assert.assertEquals(1, itemIds.size());

    }

    @Test
    public void testSynonyms() throws SQLException {
        LeafNodeTaxonContainer container = sp.loadTaxa(11);

        RowId taxonId10 = new RowId(10);
        RowId taxonId11 = new RowId(11);
        Collection<?> childIds = container.getChildren(taxonId10);
        Assert.assertEquals(2, childIds.size());

        Assert.assertEquals(true, container.areChildrenAllowed(taxonId10));
        Assert.assertEquals(false, container.areChildrenAllowed(taxonId11));
    }

    @Test
    public void updatePublishFlag() throws SQLException {
        LeafNodeTaxonContainer container = sp.loadTaxa(11);
        RowId taxonId = new RowId(10);
        boolean pb = (Boolean) container.getItem(taxonId).getItemProperty(LeafNodeTaxonContainer.PB_ID).getValue();
        Assert.assertTrue(pb);
        sp.updatePublished(false, taxonId);
        container.refresh();
        pb = (Boolean) container.getItem(taxonId).getItemProperty(LeafNodeTaxonContainer.PB_ID).getValue();
        Assert.assertFalse(pb);
    }

    @Test
    public void testGetClassificationId() throws SQLException {
        CdmSQLContainer container = sp.loadClassifications();
        Object classificationId = sp.getClassificationId("Classification1");
        Assert.assertEquals("11", classificationId.toString());
        classificationId = sp.getClassificationId("ClassificationDoesNotExist");
        Assert.assertNull(classificationId);
    }

    @Ignore
    @Test
    public void testLoadClassifications() throws SQLException {
        CdmSQLContainer container = sp.loadClassifications();
        Collection<?> itemIds = container.getItemIds();
        String[] uuids = {"6595638e-4993-421a-9fe5-76b09d94f36a", "1ef8aada-de72-4023-bbe1-14465b6bc60d"};
        int count = 0;
        for(Object itemId : itemIds) {
            Item item = container.getItem(itemId);

            String uuid = (String)item.getItemProperty("UUID").getValue();
            Assert.assertEquals(uuids[count], uuid);
            String titleCache = (String)item.getItemProperty("TITLECACHE").getValue();
            logger.info("titleCache : " + titleCache);
            count++;

        }
    }

    public static class MockStatusComposite implements IStatusComposite {

        /* (non-Javadoc)
         * @see eu.etaxonomy.cdm.vaadin.view.IStatusComposite#setListener(eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener)
         */
        @Override
        public void setListener(StatusComponentListener listener) {
            // TODO Auto-generated method stub

        }

    }
}
