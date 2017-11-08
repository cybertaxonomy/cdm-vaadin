/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.EnumSet;

import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;

/**
 * @author a.kohlbecker
 * @since 20.10.2017
 *
 */
public interface PerEntityAuthorityGrantingEditor {

    /**
     * The editor will create a per entity authority for the bean being loaded in the editor and will
     * grant this authority to the current user.
     *
     * @param crud
     */
    public void grantToCurrentUser(EnumSet<CRUD> crud);

}
