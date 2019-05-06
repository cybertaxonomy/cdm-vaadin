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
import java.util.UUID;

import com.vaadin.ui.Button;
import com.vaadin.ui.Field;

import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * Base implementation for an event which represents the request to start
 * an editor to enable the user to perform the <code>action</code> transported
 * with this event.
 * <p>
 * Which the {@link #context) stack the action can keep track of the chain of editors which where opened before and which lead
 * to this action. This is important information when working with popup editors for which EntitySave events etc
 * need to be also handled by the base view (e.g. see {@link eu.etaxonomy.cdm.vaadin.view.registration.RegistrationWorkingsetPresenter}
 * from which the the first popup editor of the chain has been opened. So the {@link #context) stack is just like a breadcrumbs information.
 *
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction<V> extends AbstractEntityEvent<EditorActionType> {

    private Button source = null;

    private Field<V> target = null;

    // DO NOT initialize here, the context should always be set by the view in which the Action object is created
    protected Stack<EditorActionContext> context;

    public AbstractEditorAction(EditorActionType action) {
        this(action, null, null, null);
    }

    public AbstractEditorAction(EditorActionType action, Button source, Field<V> target, AbstractView sourceView) {
        this(action, null, source, target, sourceView);
    }

    /**
     *
     * @deprecated Consider using the constructor with Stack<EditorActionContext> context, so that the context is set for all popupeditors!!!
     */
    @Deprecated
    public AbstractEditorAction(EditorActionType action, UUID entityUuid, Button source, Field<V> target, AbstractView sourceView) {
        this(action, entityUuid, source, target, sourceView, null);
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
    public AbstractEditorAction(EditorActionType action, UUID entityUuid, Button source, Field<V> target, AbstractView sourceView,
            Stack<EditorActionContext> context) {
        super(action, entityUuid, sourceView);
        this.source = source;
        this.target = target;
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

    public Button getSource() {
        return source;
    }

    /**
     * @return the target
     */
    public Field<V> getTarget() {
        return target;
    }

    public boolean hasSource() {
        return source != null;
    }

    /**
     * Which the {@link #context) stack the action can keep track of the chain of editors
     * which where opened before and which lead
     * to this action.
     *
     * @return the context
     */
    public Stack<EditorActionContext> getContext() {
        return context;
    }

}
