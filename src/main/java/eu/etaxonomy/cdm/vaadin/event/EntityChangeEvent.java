/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since May 10, 2017
 *
 */
public class EntityChangeEvent extends AbstractEntityEvent<EntityChangeEvent.Type> {

    public enum Type {
        CREATED,
        MODIFIED,
        REMOVED;
    }

    private Class<?> entityType;

    /**
     * @param type
     * @param entityId
     */
    public EntityChangeEvent(Class<?> entityType, Integer entityId, Type type, AbstractView sourceView) {
        super(type, entityId, sourceView);
        this.entityType = entityType;
    }

    /**
     * @return the entityType
     */
    public Class<?> getEntityType() {
        return entityType;
    }

}
