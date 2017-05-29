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
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEditorAction extends AbstractEntityEvent<AbstractEditorAction.Action> {

    public enum Action {
        ADD,
        EDIT,
        REMOVE;
    }

    private Component source = null;

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
        this.source = source;
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

    public Component getSource(){
        return source;
    }

    public boolean hasSource() {
        return source != null;
    }


}
