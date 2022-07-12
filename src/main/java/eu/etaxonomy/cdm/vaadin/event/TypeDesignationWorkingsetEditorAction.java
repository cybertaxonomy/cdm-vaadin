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

import com.vaadin.ui.Button;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class TypeDesignationWorkingsetEditorAction extends AbstractEditorAction<TypeDesignationWorkingSet> {

    private TypeDesignationWorkingSetType workingSetType;

    private UUID registrationUuid;

    private UUID typifiedNameUuid;

    private VersionableEntity baseEntity;

    /**
     * Constructor for {@link EditorActionType#EDIT EDIT} actions
     */
    public TypeDesignationWorkingsetEditorAction(VersionableEntity baseEntity,
            TypeDesignationWorkingSetType workingSetType,
            UUID registrationUuid, UUID typifiedNameUuid,
            Button source, Field<TypeDesignationWorkingSet> target, AbstractView sourceView, Stack<EditorActionContext> context) {

        super(EditorActionType.EDIT, null, source, target, sourceView);
        this.baseEntity = baseEntity;
        this.registrationUuid = registrationUuid;
        this.typifiedNameUuid = typifiedNameUuid;
        this.workingSetType = workingSetType;
        this.context = context;
    }

    /**
     * Constructor for {@link EditorActionType#ADD ADD} actions
     *
     * @param workingSetType
     * @param registrationUuid
     * @param source
     * @param sourceView
     */
    public TypeDesignationWorkingsetEditorAction(TypeDesignationWorkingSetType workingSetType,
            UUID registrationUuid, UUID typifiedNameUuid,
            Button source, Field<TypeDesignationWorkingSet> target, AbstractView sourceView) {

        super(EditorActionType.ADD, null, source, target, sourceView);
        this.workingSetType = workingSetType;
        this.registrationUuid = registrationUuid;
        this.typifiedNameUuid = typifiedNameUuid;
        this.context = new Stack<>();
        this.context.push(new EditorActionContext(new TypedEntityReference<Registration>(Registration.class, registrationUuid), sourceView));
    }

    public TypeDesignationWorkingSetType getWorkingSetType() {
        return workingSetType;
    }

    public UUID getRegistrationUuid() {
        return registrationUuid;
    }

    public UUID getTypeDesignationWorkingsetUuid(){
        return getEntityUuid();
    }

    public VersionableEntity getBaseEntity() {
        return baseEntity;
    }

    public UUID getTypifiedNameUuid() {
        return typifiedNameUuid;
    }

}
