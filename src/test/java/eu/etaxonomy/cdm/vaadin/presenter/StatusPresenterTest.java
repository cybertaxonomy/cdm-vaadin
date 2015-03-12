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
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import com.vaadin.data.Item;

import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
@DataSet
public class StatusPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(StatusPresenterTest.class);

    private static StatusPresenter sp;

    @BeforeClass
    public static void init() {
        sp = new StatusPresenter(null);
    }

    @Test
    public void testLoadTaxa() throws SQLException {
        LeafNodeTaxonContainer container = sp.loadTaxa(11);
        Collection<?> propIds = container.getContainerPropertyIds();
        Collection<?> itemIds = container.getItemIds();
        for(Object itemId : itemIds) {
            Item item = container.getItem(itemId);
            // column names need to be uppercase for h2 in the test environment
            //String taxon = (String)item.getItemProperty("TAXON").getValue();
            //logger.info("taxon : " + taxon);
        }
        Assert.assertEquals(3,itemIds.size());
    }

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
}
