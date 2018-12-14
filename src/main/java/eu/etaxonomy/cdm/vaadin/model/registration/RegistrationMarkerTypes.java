/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.UUID;

import eu.etaxonomy.cdm.model.common.MarkerType;

/**
 * RegistrationMarkerTypes specific to the phycobank project
 *
 * @author a.kohlbecker
 * @since Jun 20, 2017
 *
 */
public class RegistrationMarkerTypes {

    private static final UUID UUID_INCORRECT_NAME = UUID.fromString("a5ba6418-11a8-4284-b879-5d53d631010a");

    private static MarkerType incorrect_name = null;

    public static MarkerType INCORRECT_NAME() {
        if(incorrect_name == null){
            incorrect_name = MarkerType.NewInstance("Incorrect name", "Incorrect name", "Incorr.");
            incorrect_name.setUuid(UUID_INCORRECT_NAME);
        }
        return incorrect_name;
    }
}
