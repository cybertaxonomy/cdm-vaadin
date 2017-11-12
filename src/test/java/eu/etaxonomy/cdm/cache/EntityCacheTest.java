/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.cache;


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
    public void EntityKeyTest() {

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

}
