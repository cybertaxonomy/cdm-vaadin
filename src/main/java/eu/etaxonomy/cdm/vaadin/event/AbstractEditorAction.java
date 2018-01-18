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

import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * Base implementation for an event which
 * which represents the request to start an editor to enable the
 * user to perform the <code>action</code> transported with this event.
 *
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction extends AbstractEntityEvent<EditorActionType> {

    private Component sourceComponent = null;

    private AbstractView sourceView = null;

    public AbstractEditorAction(EditorActionType action) {
        super(action, null);
    }

    public AbstractEditorAction(EditorActionType action, Component source) {
        this(action, null, source);
    }

    /**
     * @param type
     * @param citationId
     */
    public AbstractEditorAction(EditorActionType action, Integer entityId) {
        super(action, entityId);
    }

    public AbstractEditorAction(EditorActionType action, Integer entityId, Component source) {
        super(action, entityId);
        this.sourceComponent = source;
    }

    public AbstractEditorAction(EditorActionType action, Integer entityId, Component source, AbstractView sourceView) {
        super(action, entityId);
        this.sourceComponent = source;
        this.sourceView = sourceView;
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

    public Component getSourceComponent(){
        return sourceComponent;
    }

    public boolean hasSource() {
        return sourceComponent != null;
    }

    /**
     * @return the sourceView
     */
    public AbstractView getSourceView() {
        return sourceView;
    }


}
