/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.data.Property.ValueChangeEvent;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.permission.UserHelper;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;

/**
 * @author a.kohlbecker
 * @since 19.10.2017
 *
 */
public class ToOneRelatedEntityButtonUpdater<CDM extends CdmBase> implements NestedButtonStateUpdater {

    private static final long serialVersionUID = 4472031263172275012L;

    ToOneRelatedEntityField<CDM>  toOneRelatedEntityField;

    private Class<? extends CDM> type;


    public ToOneRelatedEntityButtonUpdater(ToOneRelatedEntityField<CDM> toOneRelatedEntityField){
        this.toOneRelatedEntityField = toOneRelatedEntityField;
        this.type = toOneRelatedEntityField.getType();
        toOneRelatedEntityField.setEditButtonEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChange(ValueChangeEvent event) {

        CdmBase value = (CdmBase)event.getProperty().getValue();

        boolean userIsAllowedToUpdate = value != null && UserHelper.fromSession().userHasPermission(value, CRUD.UPDATE);
        boolean userIsAllowedToCreate = UserHelper.fromSession().userHasPermission(type, CRUD.CREATE);

        toOneRelatedEntityField.setAddButtonEnabled(userIsAllowedToCreate);
        toOneRelatedEntityField.setEditButtonEnabled(userIsAllowedToUpdate);

    }

}
