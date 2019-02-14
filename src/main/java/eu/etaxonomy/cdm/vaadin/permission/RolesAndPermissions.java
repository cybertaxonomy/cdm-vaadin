/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import java.util.UUID;

import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;

/**
 * Provides the Roles required by the
 * vaadin applications.
 *
 * @author a.kohlbecker
 * @since May 8, 2017
 *
 */
public class RolesAndPermissions {

    public static final Role ROLE_CURATION = new Role(UUID.fromString("642d9ea7-f18c-4ac3-b437-ed05ce5461c3"), "ROLE_CURATION");

}
