/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.api.utility.UserHelper;

/**
 * VaadinUserHelper interface. Implementations should use the {@link #VADDIN_SESSION_KEY} to auto registers
 * in the VaadinSession.
 *
 * @author a.kohlbecker
 * @since May 23, 2017
 *
 */
public interface VaadinUserHelper extends UserHelper {

    public static final String VADDIN_SESSION_KEY = "USER_HELPER";

    /**
     * Static accessor method to obtain the auto-registered VaadinUserHelper-Bean from the
     * VaadinSession.
     *
     * @return
     */
    public static VaadinUserHelper fromSession() {
       return (VaadinUserHelper)VaadinSession.getCurrent().getAttribute(VADDIN_SESSION_KEY);
    }

    // ---- methods special to the registration ------------

    public boolean userIsRegistrationCurator();


}
