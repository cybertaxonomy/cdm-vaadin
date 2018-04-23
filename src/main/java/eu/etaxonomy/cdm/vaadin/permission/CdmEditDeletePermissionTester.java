/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.vaadin.permission.EditPermissionTester;

/**
 * To be used for {@link ToManyRelatedEntitiesComboboxSelect}
 *
 * @author a.kohlbecker
 * @since Apr 20, 2018
 *
 */
public class CdmEditDeletePermissionTester implements EditPermissionTester {
    @Override
    public boolean userHasEditPermission(Object bean) {
        return  UserHelper.fromSession().userHasPermission((CdmBase)bean, CRUD.UPDATE, CRUD.DELETE);
    }
}