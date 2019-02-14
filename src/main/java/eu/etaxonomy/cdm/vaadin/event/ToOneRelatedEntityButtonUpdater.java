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
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;

/**
 * @author a.kohlbecker
 * @since 19.10.2017
 *
 */
public class ToOneRelatedEntityButtonUpdater<CDM extends CdmBase> implements NestedButtonStateUpdater<CDM> {

    private static final long serialVersionUID = 4472031263172275012L;

    boolean addNewEntityAllowed = true;

    ToOneRelatedEntityField<CDM>  toOneRelatedEntityField;

    private Class<? extends CDM> type;

    public ToOneRelatedEntityButtonUpdater(ToOneRelatedEntityField<CDM> toOneRelatedEntityField, boolean addNewEntityAllowed){
        this.toOneRelatedEntityField = toOneRelatedEntityField;
        this.addNewEntityAllowed = addNewEntityAllowed;
        this.type = toOneRelatedEntityField.getType();
        updateButtons(((Field<CDM>)toOneRelatedEntityField).getValue());
        toOneRelatedEntityField.setEditButtonEnabled(false);
    }

    public ToOneRelatedEntityButtonUpdater(ToOneRelatedEntityField<CDM> toOneRelatedEntityField){
        this(toOneRelatedEntityField, true);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChange(ValueChangeEvent event) {

        CDM value = (CDM)event.getProperty().getValue();
        updateButtons(value);
    }

    /**
     * @param value
     */
    @Override
    public void updateButtons(CDM value) {

        boolean userIsAllowedToUpdate = value != null && UserHelperAccess.userHelper().userHasPermission(value, CRUD.UPDATE);
        boolean userIsAllowedToCreate = UserHelperAccess.userHelper().userHasPermission(type, CRUD.CREATE);
        boolean isReadOnlyField = ((Field)toOneRelatedEntityField).isReadOnly();

        toOneRelatedEntityField.setAddButtonEnabled(addNewEntityAllowed && !isReadOnlyField && userIsAllowedToCreate);
        toOneRelatedEntityField.setEditButtonEnabled(!isReadOnlyField && userIsAllowedToUpdate);

    }

}
