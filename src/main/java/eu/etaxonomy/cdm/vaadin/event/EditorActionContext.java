/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.ui.Field;

import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

public class EditorActionContext {

    Object parentEntity;

    AbstractView parentView;

    /**
     * The field in the parent view to which the child view is related to.
     */
    Field<?> targetField;

    /**
     * @param parentEntity
     * @param parentView
     */
    public EditorActionContext(Object parentEntity, AbstractView parentView) {
        super();
        this.parentEntity = parentEntity;
        this.parentView = parentView;
    }

    /**
     * TODO rename to getParentBean()
     *
     * @return the parentEntity
     */
    public Object getParentEntity() {
        return parentEntity;
    }

    public AbstractView getParentView() {
        return parentView;
    }

    public void setTargetField(Field<?> targetField) {
        this.targetField = targetField;
    }

    public Field<?> getTargetField() {
        return targetField;
    }

    public PropertyIdPath getTargetPropertyIdPath(){
        if(parentView instanceof AbstractPopupEditor && targetField != null){
            return ((AbstractPopupEditor)getParentView()).boundPropertyIdPath(targetField);
        } else {
            return null;
        }
    }



}