/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
public class UserHelper {


    public static boolean userIsAutheticated() {
        Authentication authentication = getAuthentication();
        if(authentication != null){
            return authentication.isAuthenticated();
        }
        return false;
    }


    public static boolean userIsAnnonymous() {
        Authentication authentication = getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && authentication instanceof AnonymousAuthenticationToken;
    }

    public static String userName() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public static boolean userIsAdmin() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(a -> {
                return a.getAuthority().equals(Role.ROLE_ADMIN.getAuthority());
            });
        }
        return false;
    }

    public static boolean userIsRegistrationCurator() {
        Authentication authentication = getAuthentication();
        if(authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(a -> {
                // doing faster regex check here instreas of using CdmAuthoritiy.fromString()
                return a.getAuthority().matches("^Registration\\.\\[.*UPDATE");
            });
        }
        return false;
    }

    /**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    private static SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * @return
     */
    private static Authentication getAuthentication() {
        return currentSecurityContext().getAuthentication();
    }

}
