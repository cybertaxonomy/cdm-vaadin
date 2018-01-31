/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.cache;


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import eu.etaxonomy.cdm.model.occurrence.Collection;

/**
 * @author a.kohlbecker
 * @since 09.11.2017
 *
 */
public class EntityCacheTest {

    @Test
    public void entityKeyTest() {

        Collection collection1 = Collection.NewInstance();
        Collection collection2 = Collection.NewInstance();
        collection1.setId(81);
        collection2.setId(81);


        CdmEntityCache cache = new CdmEntityCache(collection1);

        CdmEntityCache.EntityKey key1 = cache.new EntityKey(collection1);

        CdmEntityCache.EntityKey key2 = cache.new EntityKey(collection2);

        Assert.assertEquals(key1, key2);

        Assert.assertEquals(key1.hashCode(), key2.hashCode());

        Assert.assertNotNull(cache.find(collection1));
    }

    @Test
    public void testFindOrUpdate() {

        Collection collection = Collection.NewInstance();
        collection.setId(10);
        collection.setCode("A");

        CdmEntityCache cache = new CdmEntityCache(collection);

        Assert.assertEquals(collection, cache.find(Collection.class, 10));
        Assert.assertEquals("A", cache.find(Collection.class, 10).getCode());

        Collection copyCollection = collection.clone();
        copyCollection.setId(collection.getId()); // clone does not copy the id!
        copyCollection.setUuid(collection.getUuid());
        copyCollection.setCode("B");
        copyCollection.setUpdated(new DateTime(2017, 12, 1, 0, 0));
        Assert.assertEquals("B", cache.findAndUpdate(copyCollection).getCode());

        copyCollection = collection.clone();
        copyCollection.setId(10); // clone does not copy the id!
        copyCollection.setUuid(collection.getUuid());
        copyCollection.setCode("C");
        copyCollection.setUpdated(new DateTime(2018, 1, 1, 0, 0));
        Assert.assertEquals("C", cache.findAndUpdate(copyCollection).getCode());

    }

}
