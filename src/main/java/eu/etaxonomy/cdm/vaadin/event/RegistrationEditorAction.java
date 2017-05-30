/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

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

}
