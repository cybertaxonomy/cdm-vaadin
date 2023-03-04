/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.UUID;

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since May 10, 2017
 */
public abstract class AbstractEntityEvent<T extends Enum<?>> {

    protected T type;

    private UUID entityUuid = null;

    private AbstractView<?> sourceView = null;

    public AbstractEntityEvent(T type, UUID entityUuid, AbstractView<?> sourceView) {
        this.entityUuid = entityUuid;
        this.type = type;
        this.sourceView = sourceView;
        if(type == null){
            throw new NullPointerException();
        }
    }

    public UUID getEntityUuid() {
        return entityUuid;
    }

    public T getType() {
        return type;
    }

    public AbstractView<?> getSourceView() {
        return sourceView;
    }
}