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
 * This Event reports a successful authentication
 *
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
public class AuthenticationSuccessEvent implements AuthenticationEvent {

    String userName;

    /**
     * @param e
     * @param userName
     */
    public AuthenticationSuccessEvent(String userName) {
        this.userName = userName;
    }

}
