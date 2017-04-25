/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.ui.Button.ClickEvent;

/**
 * This Event must not undisclose the password when published on
 * the event bus. therefore it does not contain the password.
 *
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
public class AuthenticationAttemptEvent implements AuthenticationEvent{

    ClickEvent e;
    String userName;

    /**
     * @param e
     * @param userName
     */
    public AuthenticationAttemptEvent(ClickEvent e, String userName) {
        this.e = e;
        this.userName = userName;
    }

    /**
     * @return the e
     */
    public ClickEvent getE() {
        return e;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }



}
