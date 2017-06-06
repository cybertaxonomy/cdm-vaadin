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
public class TaxonNameEditorAction extends AbstractEditorAction {


    public TaxonNameEditorAction(Action eventType) {
        super(eventType);
    }

    /**
     * @param type
     * @param entityId
     */
    public TaxonNameEditorAction(Action type, Integer entityId) {
        super(type, entityId);
    }

    /**
     * @param type
     * @param entityId
     * @param source
     */
    public TaxonNameEditorAction(Action type, Integer entityId, Component source) {
        super(type, entityId, source);
    }




}
