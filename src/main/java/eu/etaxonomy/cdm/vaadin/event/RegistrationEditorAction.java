/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.UUID;

import com.vaadin.ui.Button;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 */
public class RegistrationEditorAction extends AbstractEditorAction<Registration> {

    public RegistrationEditorAction(EditorActionType type) {
        super(type);
    }

    public RegistrationEditorAction(EditorActionType action, Button source, Field<Registration> target, AbstractView sourceView) {
        super(action, source, target, sourceView);
    }

    public RegistrationEditorAction(EditorActionType action, UUID entityUuid, Button source, Field<Registration> target, AbstractView sourceView) {
        super(action, entityUuid, source, target, sourceView);
    }
}