/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBusListenerMethodFilter;

import eu.etaxonomy.vaadin.event.EditorActionType;

/**
 * @author a.kohlbecker
 * @since Jan 31, 2018
 */
public class EditorActionTypeFilter implements EventBusListenerMethodFilter {

    private EditorActionType action;

    private EditorActionTypeFilter(EditorActionType action) {
        this.action = action;
    }

    @Override
    public boolean filter(Event<?> event) {
        if (event.getPayload() instanceof AbstractEditorAction) {
            AbstractEditorAction<?> editorAction = (AbstractEditorAction<?>) event.getPayload();
            return this.action.equals(editorAction.type);
        }
        return false;
    }

    public static class Add extends EditorActionTypeFilter {
        public Add() {
            super(EditorActionType.ADD);
        }
    }

    public static class Edit extends EditorActionTypeFilter {
        public Edit() {
            super(EditorActionType.EDIT);
        }
    }

    public static class Remove extends EditorActionTypeFilter {
        public Remove() {
            super(EditorActionType.REMOVE);
        }
    }

}
