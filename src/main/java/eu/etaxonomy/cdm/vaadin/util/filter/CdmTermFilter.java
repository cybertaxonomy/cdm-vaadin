/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.filter;

import java.util.List;

import com.vaadin.data.Container.Filter;

import eu.etaxonomy.cdm.model.term.TermBase;

import com.vaadin.data.Item;
import com.vaadin.data.Property;

/**
 * @author a.kohlbecker
 * @since Jun 21, 2018
 *
 */
public class CdmTermFilter<T extends TermBase> implements Filter {

    private static final long serialVersionUID = -613582375956129270L;

    private List<T> includeFilter;
    private Object propertyId;

    /**
     *
     * @param propertyId
     * @param includeFilter
     * @param includeNullValues true by default
     */
    public CdmTermFilter(Object propertyId, List<T> includeFilter){
        this.propertyId = propertyId;
        this.includeFilter = includeFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean passesFilter(Object itemId, Item item) throws UnsupportedOperationException {
        Property property = item.getItemProperty(propertyId);
        Object value = property.getValue();
        if(includeFilter.contains(value)){
            return true;
        }
        return false;
    }

    @Override
    public boolean appliesToProperty(Object propertyId) {
        return this.propertyId.equals(propertyId);
    }

}
