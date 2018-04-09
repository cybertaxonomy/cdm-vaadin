/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.EnumSet;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since May 10, 2017
 *
 */
public class EntityChangeEvent<T extends CdmBase> extends AbstractEntityEvent<EntityChangeEvent.Type> {

    public enum Type {
        CREATED,
        MODIFIED,
        REMOVED;
    }

    public static final EnumSet<Type> CREATE_OR_MODIFIED = EnumSet.of(EntityChangeEvent.Type.CREATED, EntityChangeEvent.Type.MODIFIED);

    private Class<T> entityType;

    private T entity;

    public EntityChangeEvent(T entity, Type type, AbstractView sourceView) {
        super(type, entity.getId(), sourceView);
        this.entityType = (Class<T>) entity.getClass();
        this.entity = entity;
    }

    /**
     * @return the entityType
     */
    public Class<?> getEntityType() {
        return entityType;
    }

    /**
     * @return the entity
     */
    public T getEntity() {
        return entity;
    }

    public boolean isCreateOrModifiedType() {
       return CREATE_OR_MODIFIED.contains(type);
    }

    public boolean isRemovedType() {
        return Type.REMOVED.equals(type);
     }

}
