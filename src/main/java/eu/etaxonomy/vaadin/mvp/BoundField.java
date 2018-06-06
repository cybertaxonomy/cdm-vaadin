/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import com.vaadin.ui.Field;

import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * See {@link {@link AbstractPresenter#openPopupEditor(Class, eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction)}}
 *
 * @author a.kohlbecker
 * @since May 18, 2018
 *
 */
public class BoundField {

    Field<?> field;
    PropertyIdPath propertyIdPath;

    /**
     * @param field
     * @param propertyId
     */
    public BoundField(Field<?> field, PropertyIdPath propertyIdPath) {
        super();
        this.field = field;
        this.propertyIdPath = propertyIdPath;
    }

    /**
     * @return the field
     */
    public <T> Field<T> getField(Class<T> fieldValueType) {
        return (Field<T>)field;
    }

    public Field<?> getField() {
        return field;
    }

    /**
     * @return the propertyId
     */
    public PropertyIdPath getPropertyIdPath() {
        return propertyIdPath;
    }

    /**
     * Null-save method to check for equality. Will always return <code>false</code> in case the internal
     * propertyId is <code>null</code>.
     *
     * @param propertyId
     * @return
     */
    public boolean matchesPropertyIdPath(String propertyIdPath) {
        if(this.propertyIdPath == null){
            return false;
        }
        return this.propertyIdPath.matches(propertyIdPath);
    }

}