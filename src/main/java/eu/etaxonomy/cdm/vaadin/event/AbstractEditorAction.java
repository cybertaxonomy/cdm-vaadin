/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction {


    private EditorActionType actionType;

    private Integer entityId = null;

    public AbstractEditorAction(EditorActionType eventType, Integer entityId) {
        this.actionType = eventType;
        if(eventType == null){
            throw new NullPointerException();
        }
        this.setEntityId(entityId);
    }

    public AbstractEditorAction(EditorActionType actionType) {
        this(actionType, null);
    }

    public EditorActionType getActionType() {
        return actionType;
    }

    public boolean isAddAction() {
        return actionType.equals(EditorActionType.ADD);
    }
    public boolean isEditAction() {
        return actionType.equals(EditorActionType.EDIT);
    }
    public boolean isRemoveAction() {
        return actionType.equals(EditorActionType.REMOVE);
    }

    /**
     * @return the entityId
     */
    public Integer getEntityId() {
        return entityId;
    }

    /**
     * @param entityId the entityId to set
     */
    private void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

}
