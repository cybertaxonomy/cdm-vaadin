/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.Stack;

import com.vaadin.ui.Component;

import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * Base implementation for an event which represents the request to start
 * an editor to enable the user to perform the <code>action</code> transported
 * with this event.
 *
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction extends AbstractEntityEvent<EditorActionType> {

    private Component sourceComponent = null;

    protected Stack<EditorActionContext> context;

    public AbstractEditorAction(EditorActionType action) {
        this(action, null, null);
    }

    public AbstractEditorAction(EditorActionType action, Component source, AbstractView sourceView) {
        this(action, null, source, sourceView);
    }

    public AbstractEditorAction(EditorActionType action, Integer entityId, Component source, AbstractView sourceView) {
        this(action, entityId, source, sourceView, null);
    }

    /**
     *
     * @param action
     *            the action being requested
     * @param entityId
     *            the id of the entity to which the action
     * @param source
     *            The vaadin ui component from which the action was triggered
     * @param sourceView
     *            The view from which the action is send
     * @param context
     *            Editor actions can hold a stack of entities to represent the chain of
     *            Editor actions from previous views and editors that lead to the point
     *            from where this action is spawned.
     */
    public AbstractEditorAction(EditorActionType action, Integer entityId, Component source, AbstractView sourceView,
            Stack<EditorActionContext> context) {
        super(action, entityId, sourceView);
        this.sourceComponent = source;
        this.context = context;
    }

    public boolean isAddAction() {
        return type.equals(EditorActionType.ADD);
    }

    public boolean isEditAction() {
        return type.equals(EditorActionType.EDIT);
    }

    public boolean isRemoveAction() {
        return type.equals(EditorActionType.REMOVE);
    }

    public Component getSourceComponent() {
        return sourceComponent;
    }

    public boolean hasSource() {
        return sourceComponent != null;
    }

    /**
     * @return the context
     */
    public Stack<EditorActionContext> getContext() {
        return context;
    }

    public static class EditorActionContext {

        Object parentEntity;

        AbstractView parentView;

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
         * @return the parentEntity
         */
        public Object getParentEntity() {
            return parentEntity;
        }

        /**
         * @return the parentView
         */
        public AbstractView getParentView() {
            return parentView;
        }



    }

}
