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

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction extends AbstractEntityEvent<AbstractEditorAction.Action> {

    public enum Action {
        ADD,
        EDIT,
        REMOVE;
    }

    private Component sourceComponent = null;

    private AbstractView sourceView = null;

    public AbstractEditorAction(Action action) {
        super(action, null);
    }

    public AbstractEditorAction(Action action, Component source) {
        this(action, null, source);
    }

    /**
     * @param type
     * @param citationId
     */
    public AbstractEditorAction(Action action, Integer entityId) {
        super(action, entityId);
    }

    public AbstractEditorAction(Action action, Integer entityId, Component source) {
        super(action, entityId);
        this.sourceComponent = source;
    }

    public AbstractEditorAction(Action action, Integer entityId, Component source, AbstractView sourceView) {
        super(action, entityId);
        this.sourceComponent = source;
        this.sourceView = sourceView;
    }

    public boolean isAddAction() {
        return type.equals(Action.ADD);
    }

    public boolean isEditAction() {
        return type.equals(Action.EDIT);
    }

    public boolean isRemoveAction() {
        return type.equals(Action.REMOVE);
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
