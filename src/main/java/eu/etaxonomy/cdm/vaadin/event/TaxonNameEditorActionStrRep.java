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
 * Variant of the TaxonNameEditorAction with to be published from fields like the {@link eu.etaxonomy.vaadin.component.WeaklyRelatedEntityCombobox} which
 * contain string representations of an entity instead of the whole entit.y
 *
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TaxonNameEditorActionStrRep extends AbstractEditorAction<String> {


    public TaxonNameEditorActionStrRep(EditorActionType eventType) {
        super(eventType);
    }

    /**
     * @param action
     * @param source
     */
    public TaxonNameEditorActionStrRep(EditorActionType action, Button source, Field<String> target, AbstractView sourceView) {
        super(action, source, target, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     */
    public TaxonNameEditorActionStrRep(EditorActionType action, UUID entityUuid, Button source, Field<String> target, AbstractView sourceView) {
        super(action, entityUuid, source, target, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     * @param context
     */
    public TaxonNameEditorActionStrRep(EditorActionType action, UUID entityUuid, Button source, Field<String> target, AbstractView sourceView,
            Stack<EditorActionContext> context) {
        super(action, entityUuid, source, target, sourceView, context);
    }




}
