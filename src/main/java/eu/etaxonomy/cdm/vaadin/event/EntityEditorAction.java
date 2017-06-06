/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.ui.Component;

/**
 * @author a.kohlbecker
 * @since May 25, 2017
 *
 */
public class EntityEditorAction extends AbstractEditorAction {

    private Class<?> entityType;

    /**
     * @param type
     * @param entityId
     * @param source
     */
    public EntityEditorAction(Action action, Class<?> entityType, Integer entityId, Component source) {
        super(action, entityId, source);
        this.entityType = entityType;
    }

    /**
     * @param type
     * @param entityId
     */
    public EntityEditorAction(Action action, Class<?> entityType, Integer entityId) {
        super(action, entityId);
        this.entityType = entityType;
    }

    /**
     * @param type
     */
    public EntityEditorAction(Action action, Class<?> entityType) {
        super(action);
        this.entityType = entityType;
    }

    /**
     * @param action
     * @param source
     */
    public EntityEditorAction(Action action, Class<?> entityType, Component source) {
        super(action, source);
        this.entityType = entityType;
    }

    /**
     * @return the entityType
     */
    public Class<?> getEntityType() {
        return entityType;
    }


}
