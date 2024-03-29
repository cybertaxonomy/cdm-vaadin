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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;
import org.unitils.dbunit.annotation.DataSets;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.test.unitils.CleanSweepInsertLoadStrategy;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.component.taxon.IStatusComposite;
import eu.etaxonomy.cdm.vaadin.component.taxon.StatusPresenter;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;

/**
 * @author cmathew
 * @since 10 Mar 2015
 */
@DataSets({
    @DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class),
    @DataSet("/eu/etaxonomy/cdm/database/FirstData_UsersAndPermissions.xml")
})
public class StatusPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = LogManager.getLogger();

    private static StatusPresenter statusPresenter;

    @BeforeClass
    public static void init() {
        statusPresenter = new StatusPresenter();
    }

    @Test
    public void testLoadTaxa() throws SQLException {

        LeafNodeTaxonContainer container = statusPresenter.loadTaxa(11);

        Collection<?> itemIds = container.rootItemIds();

        Assert.assertEquals(3, itemIds.size());

        statusPresenter.setUnplacedFilter();
        itemIds = container.getItemIds();
        Assert.assertEquals(1, itemIds.size());

        statusPresenter.removeUnplacedFilter();
        itemIds = container.getItemIds();
        Assert.assertEquals(3, itemIds.size());

        statusPresenter.setNameFilter("Taxon A");
        itemIds = container.getItemIds();
        Assert.assertEquals(1, itemIds.size());

    }

    @Test
    public void testSynonyms() throws SQLException {

        LeafNodeTaxonContainer container = statusPresenter.loadTaxa(11);

        RowId taxonId10 = new RowId(10);
        RowId taxonId11 = new RowId(11);
        Collection<?> childIds = container.getChildren(taxonId10);
        Assert.assertEquals(2, childIds.size());

        Assert.assertEquals(true, container.areChildrenAllowed(taxonId10));
        Assert.assertEquals(false, container.areChildrenAllowed(taxonId11));
    }

    @Test
    @Ignore
    public void updatePublishFlag() throws SQLException {

        LeafNodeTaxonContainer container = statusPresenter.loadTaxa(11);
        RowId taxonId = new RowId(10);
        Item item = container.getItem(taxonId);
        Property<?> itemProperty = item.getItemProperty(LeafNodeTaxonContainer.PB_ID);
        boolean pb = (Boolean) itemProperty.getValue();
        Assert.assertTrue(pb);
        statusPresenter.updatePublished(false, taxonId);
        container.refresh();
        pb = (Boolean) itemProperty.getValue();
        Assert.assertFalse(pb);
    }

    @Test
    public void testGetClassificationId() throws SQLException {
        statusPresenter.loadClassifications();
        Object classificationId = statusPresenter.getClassificationId("Classification1");
        Assert.assertEquals("11", classificationId.toString());
        classificationId = statusPresenter.getClassificationId("ClassificationDoesNotExist");
        Assert.assertNull(classificationId);
    }

    @Test
    public void testLoadClassifications() throws SQLException {
        CdmSQLContainer container = statusPresenter.loadClassifications();
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

        @Override
        public void setListener(StatusComponentListener listener) {
            // TODO Auto-generated method stub
        }
    }
}
