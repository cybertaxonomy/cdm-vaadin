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
 * @deprecated replace by {@link EntityEditorAction}
 */
@Deprecated
public class ReferenceEditorAction extends AbstractEditorAction {

    /**
     * @param eventType
     */
    public ReferenceEditorAction(Action type) {
        super(type);
    }

    /**
     * @param edit
     * @param citationId
     */
    public ReferenceEditorAction(Action type, Integer citationId) {
        super(type, citationId);
    }

    /**
     * @param type
     * @param entityId
     * @param source
     */
    public ReferenceEditorAction(Action type, Integer entityId, Component source) {
        super(type, entityId, source);
    }



}
