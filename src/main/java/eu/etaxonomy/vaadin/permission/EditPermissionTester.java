/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.permission;

/**
 * Implementations check if the currently authenticated user has edit permissions
 * on the object passed to the userHasEditPermission method.
 *
 * @author a.kohlbecker
 * @since Jan 17, 2018
 *
 */
public interface EditPermissionTester {

    /**
     * Checks if the currently authenticated user has edit permissions
     * on the <code>bean</code>.
     *
     * @param bean the Object in question
     *
     * @return true if the current user is allowed to edit the <code>bean</code> passed as parameter.
     */
    public boolean userHasEditPermission(Object bean);

}
