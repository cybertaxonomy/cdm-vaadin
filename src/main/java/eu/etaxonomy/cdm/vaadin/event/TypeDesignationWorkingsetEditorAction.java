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
import java.util.UUID;

import com.vaadin.ui.Component;

import eu.etaxonomy.cdm.api.service.dto.TypedEntityReference;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TypeDesignationWorkingsetEditorAction extends AbstractEditorAction {

    private TypeDesignationWorkingSetType workingSetType;

    private UUID registrationUuid;

    private UUID typifiedNameUuid;

    private TypedEntityReference<IdentifiableEntity<?>> baseEntityRef;

    /**
     *
     * @param action
     * @param baseEntityRef
     * @param workingSetType
     * @param registrationId
     * @param source
     * @param sourceView
     */
    public TypeDesignationWorkingsetEditorAction(EditorActionType action, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef,
            TypeDesignationWorkingSetType workingSetType,
            UUID registrationUuid, UUID typifiedNameUuid,
            Component source, AbstractView sourceView, Stack<EditorActionContext> context) {
        super(action, null, source, sourceView);
        this.baseEntityRef = baseEntityRef;
        this.registrationUuid = registrationUuid;
        this.typifiedNameUuid = typifiedNameUuid;
        this.workingSetType = workingSetType;
        this.context = context;

    }

    /**
     *
     * @param action
     * @param workingSetType
     * @param registrationUuid
     * @param source
     * @param sourceView
     */
    public TypeDesignationWorkingsetEditorAction(EditorActionType action, TypeDesignationWorkingSetType workingSetType,
            UUID registrationUuid, UUID typifiedNameUuid,
            Component source, AbstractView sourceView) {
        super(action, null, source, sourceView);
        this.workingSetType = workingSetType;
        this.registrationUuid = registrationUuid;
        this.typifiedNameUuid = typifiedNameUuid;
        this.context = new Stack<>();
        this.context.push(new EditorActionContext(new TypedEntityReference<Registration>(Registration.class, registrationUuid), sourceView));
    }



    /**
     *
     * @return
     */
    public TypeDesignationWorkingSetType getWorkingSetType() {
        return workingSetType;
    }

    /**
     * @return the registrationUuid
     */
    public UUID getRegistrationUuid() {
        return registrationUuid;
    }

    public UUID getTypeDesignationWorkingsetUuid(){
        return getEntityUuid();
    }

    /**
     * @return the baseEntityRef
     */
    public TypedEntityReference<IdentifiableEntity<?>> getBaseEntityRef() {
        return baseEntityRef;
    }

    /**
     * @return the typifiedNameUuid
     */
    public UUID getTypifiedNameUuid() {
        return typifiedNameUuid;
    }


}
