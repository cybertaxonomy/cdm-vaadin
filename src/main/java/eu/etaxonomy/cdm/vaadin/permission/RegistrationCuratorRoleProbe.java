/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import org.springframework.security.core.Authentication;

import eu.etaxonomy.cdm.api.utility.RoleProbe;

/**
 * @author a.kohlbecker
 * @since Jul 16, 2018
 *
 */
public class RegistrationCuratorRoleProbe implements RoleProbe {

    @Override
    public boolean checkForRole(Authentication authentication){

        if(authentication != null) {
            return authentication.getAuthorities().stream().anyMatch(a -> {
                return a.equals(RolesAndPermissions.ROLE_CURATION)
                        // doing faster regex check here instead of using CdmAuthoritiy.fromString()
                        || a.getAuthority().matches("^Registration\\.\\[.*UPDATE");
            });
        }
        return false;
    }

}
