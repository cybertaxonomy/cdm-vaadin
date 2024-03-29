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
 * @author a.kohlbecker
 * @since Apr 25, 2017
 */
public class UserAccountEvent {

    public enum UserAccountAction {
        REQUEST_PASSWORD_RESET,
        RESET_PASSWORD,
        REGISTER_ACCOUNT
    }

    ClickEvent e;

    private UserAccountAction action;

    public UserAccountEvent(UserAccountAction action, ClickEvent e) {
        this.action = action;
        this.e = e;
    }

    public UserAccountAction getAction() {
        return action;
    }
}