/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.Stack;

import com.vaadin.ui.Component;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TypeDesignationWorkingsetEditorAction extends AbstractEditorAction {

    private TypeDesignationWorkingSetType workingSetType;

    private int registrationId;

    private int typifiedNameId;

    private TypedEntityReference<IdentifiableEntity<?>> baseEntityRef;

    /**
     *
     * @param action
     * @param typeDesignationWorkingsetId
     * @param workingSetType
     * @param registrationId
     * @param source
     * @param sourceView
     */
    public TypeDesignationWorkingsetEditorAction(EditorActionType action, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef,
            TypeDesignationWorkingSetType workingSetType,
            int registrationId, int typifiedNameId,
            Component source, AbstractView sourceView, Stack<EditorActionContext> context) {
        super(action, null, source, sourceView);
        this.baseEntityRef = baseEntityRef;
        this.registrationId = registrationId;
        this.typifiedNameId = typifiedNameId;
        this.workingSetType = workingSetType;
        this.context = context;

    }

    /**
     *
     * @param action
     * @param workingSetType
     * @param registrationId
     * @param source
     * @param sourceView
     */
    public TypeDesignationWorkingsetEditorAction(EditorActionType action, TypeDesignationWorkingSetType workingSetType,
            int registrationId, int typifiedNameId,
            Component source, AbstractView sourceView) {
        super(action, null, source, sourceView);
        this.workingSetType = workingSetType;
        this.registrationId = registrationId;
        this.typifiedNameId = typifiedNameId;
        this.context = new Stack<>();
        this.context.push(new EditorActionContext(new TypedEntityReference<Registration>(Registration.class, registrationId), sourceView));
    }



    /**
     *
     * @return
     */
    public TypeDesignationWorkingSetType getWorkingSetType() {
        return workingSetType;
    }

    /**
     * @return the registrationId
     */
    public int getRegistrationId() {
        return registrationId;
    }

    public Integer getTypeDesignationWorkingsetId(){
        return getEntityId();
    }

    /**
     * @return the baseEntityRef
     */
    public TypedEntityReference<IdentifiableEntity<?>> getBaseEntityRef() {
        return baseEntityRef;
    }

    /**
     * @return the typifiedNameId
     */
    public int getTypifiedNameId() {
        return typifiedNameId;
    }


}
