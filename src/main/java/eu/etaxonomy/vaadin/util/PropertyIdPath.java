/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.util;

import java.util.Vector;

import org.apache.commons.lang3.StringUtils;

/**
 * @author a.kohlbecker
 * @since May 18, 2018
 *
 */
public class PropertyIdPath {

    Vector propertyIds = new Vector<>();

    public PropertyIdPath() {

    }

    /**
     * @param propertyId
     */
    public PropertyIdPath(Object propertyId) {
        propertyIds.add(propertyId);
    }

    /**
     *
     * @param propertyId
     */
    public void addParent(Object propertyId){
        propertyIds.add(0, propertyId);
    }

    public void addChild(Object propertyId){
        propertyIds.add(propertyId);
    }

    @Override
    public String toString(){
        return StringUtils.join(propertyIds, ".");
    }

    public boolean matches(String propertyIdPath){
        return toString().equals(propertyIdPath);
    }

    public boolean isEmpty(){
        return propertyIds.isEmpty();
    }

}
