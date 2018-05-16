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

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TaxonNameEditorAction extends AbstractEditorAction<TaxonName> {


    public TaxonNameEditorAction(EditorActionType eventType) {
        super(eventType);
    }

    /**
     * @param action
     * @param source
     */
    public TaxonNameEditorAction(EditorActionType action, Button source, Field<TaxonName> target, AbstractView sourceView) {
        super(action, source, target, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     */
    public TaxonNameEditorAction(EditorActionType action, UUID entityUuid, Button source, Field<TaxonName> target, AbstractView sourceView) {
        super(action, entityUuid, source, target, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     * @param context
     */
    public TaxonNameEditorAction(EditorActionType action, UUID entityUuid, Button source, Field<TaxonName> target, AbstractView sourceView,
            Stack<EditorActionContext> context) {
        super(action, entityUuid, source, target, sourceView, context);
    }




}
