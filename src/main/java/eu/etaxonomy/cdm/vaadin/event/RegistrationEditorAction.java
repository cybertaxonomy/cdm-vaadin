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
 */
public class RegistrationEditorAction extends AbstractEditorAction {

    /**
     * @param eventType
     */
    public RegistrationEditorAction(Action type) {
        super(type);
    }

    /**
     * @param edit
     * @param citationId
     */
    public RegistrationEditorAction(Action type, Integer enitityId) {
        super(type, enitityId);
    }

    /**
     * @param action
     * @param source
     */
    public RegistrationEditorAction(Action action, Component source) {
        super(action, source);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     * @param sourceView
     */
    public RegistrationEditorAction(Action action, Integer entityId, Component source, AbstractView sourceView) {
        super(action, entityId, source, sourceView);
    }

    /**
     * @param action
     * @param entityId
     * @param source
     */
    public RegistrationEditorAction(Action action, Integer entityId, Component source) {
        super(action, entityId, source);
    }



}
