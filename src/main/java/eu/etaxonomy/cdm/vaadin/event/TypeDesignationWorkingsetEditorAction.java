/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.ui.Component;

import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TypeDesignationWorkingsetEditorAction extends AbstractEditorAction {

    private Class<? extends TypeDesignationBase<?>> newEntityType;

    private int registrationId;

    /**
     * @param edit
     * @param ids
     */
    public TypeDesignationWorkingsetEditorAction(Action action, Integer typeDesignationWorkingsetId, int registrationId, Component source, AbstractView sourceView) {
        super(action, typeDesignationWorkingsetId, source, sourceView);
        this.registrationId = registrationId;
    }

    /**
     * Constructor which is mainly suitable for ADD actions.
     * @param
     */
    public TypeDesignationWorkingsetEditorAction(Action action, Class<? extends TypeDesignationBase<?>> newEntityType, int registrationId,
            Component source, AbstractView sourceView) {
        super(action, null, source, sourceView);
        this.newEntityType = newEntityType;
        this.registrationId = registrationId;
    }

    /**
     * In case of an ADD action the receiver of the event needs to know the specific type of the
     * TypeDesignationBase instance to be created.
     *
     * @return the newEntityType
     */
    public Class<? extends TypeDesignationBase<?>> getNewEntityType() {
        return newEntityType;
    }

    /**
     * @return the registrationId
     */
    public int getRegistrationId() {
        return registrationId;
    }





}
