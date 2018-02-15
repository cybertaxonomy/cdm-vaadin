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
 * @author a.kohlbecker
 * @since Mar 22, 2017
 */
public class CollectionEditorAction extends AbstractEditorAction {

    /**
     * @param eventType
     */
    public CollectionEditorAction(EditorActionType type) {
        super(type);
    }

    /**
     * @param action
     * @param source
     */
    public CollectionEditorAction(EditorActionType action, Component source, AbstractView sourceView) {
        super(action, source, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     */
    public CollectionEditorAction(EditorActionType action, Integer entityId, Component source, AbstractView sourceView) {
        super(action, entityId, source, sourceView);
    }

}
