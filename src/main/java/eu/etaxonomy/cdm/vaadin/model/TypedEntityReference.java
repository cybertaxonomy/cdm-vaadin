/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model;

import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author a.kohlbecker
 * @since Jun 12, 2017
 *
 */
public class TypedEntityReference extends EntityReference {

    /**
     * @param id
     * @param label
     */
    public TypedEntityReference(Class type, int id, String label) {
        super(id, label);
        this.type = type;
    }

    /**
     * @return the type
     */
    public Class getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.type = type;
    }

    private Class type;



    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .appendSuper(super.hashCode())
                .appendSuper(type.hashCode())
                .hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        try {
            TypedEntityReference other = (TypedEntityReference) obj;
            return id == other.id && label.equals(other.label) && type.equals(other.type);

        } catch (Exception e) {
            return false;
        }
    }

}
