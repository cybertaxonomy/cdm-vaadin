/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.phycobank;

import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public class RegistrationStartEvent {

    private RegistrationType type;

    RegistrationStartEvent(RegistrationType type){
        this.type = type;
    }

    /**
     * @return the type
     */
    public RegistrationType getType() {
        return type;
    }

}
