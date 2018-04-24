/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import com.vaadin.data.fieldgroup.FieldGroup;

/**
 * @author a.kohlbecker
 * @since May 12, 2017
 *
 */
public interface NestedFieldGroup {

    /**
     * Implementations return the local fieldGroup
     *
     * @return
     */
    public abstract FieldGroup getFieldGroup();

    public abstract void registerParentFieldGroup(FieldGroup parent);

    /**
     * @param parent
     */
    public abstract void unregisterParentFieldGroup(FieldGroup parent);

}
