/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.debug;

import com.vaadin.data.Container.Sortable;
import com.vaadin.data.util.ItemSorter;

import eu.etaxonomy.cdm.cache.EntityCacherDebugResult.CdmEntityInfo;

/**
 * @author a.kohlbecker
 * @since Jan 23, 2018
 *
 */
public class CdmEntityInfoSorter implements ItemSorter {

    private static final long serialVersionUID = 1008554008146041297L;

    private Object[] propertyId;
    private boolean[] ascending;

    @Override
    public void setSortProperties(Sortable container, Object[] propertyId, boolean[] ascending) {
        this.propertyId = propertyId;
        this.ascending = ascending;

    }

    @Override
    public int compare(Object itemId1, Object itemId2) {
        if(! (itemId1 instanceof CdmEntityInfo && itemId2 instanceof CdmEntityInfo) ){
            throw new RuntimeException("Objects must be CdmEntityInfo");
        }
        CdmEntityInfo infoItem1 = (CdmEntityInfo)itemId1;
        CdmEntityInfo infoItem2 = (CdmEntityInfo)itemId2;

        return infoItem1.getField().getName().compareToIgnoreCase(infoItem2.getField().getName());
    }
}