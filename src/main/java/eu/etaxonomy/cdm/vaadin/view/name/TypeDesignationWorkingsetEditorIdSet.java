/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.UUID;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

public class TypeDesignationWorkingsetEditorIdSet {

    UUID registrationUuid;
    TypedEntityReference<IdentifiableEntity<?>> baseEntityRef;
    UUID typifiedNameUuid;

    /**
     * @param registrationUuid
     *            must be present
     * @param baseEntityRef
     *
     * @param typifiedNameUuid
     *            Can <code>null</code> if the <code>workingsetId</code> is given
     */
    protected TypeDesignationWorkingsetEditorIdSet(UUID registrationUuid, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef, UUID typifiedNameUuid) {
        this.registrationUuid = registrationUuid;
        this.baseEntityRef = baseEntityRef;
        this.typifiedNameUuid = typifiedNameUuid;
        if(baseEntityRef == null && typifiedNameUuid == null){
            throw new NullPointerException("When workingsetId is null the typifiedNameId must be non null.");
        }
    }

    public TypeDesignationWorkingsetEditorIdSet(UUID registrationUuid, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef) {
        this(registrationUuid, baseEntityRef, null);
    }

    public TypeDesignationWorkingsetEditorIdSet(UUID registrationUuid, UUID typifiedNameUuid) {
        this(registrationUuid, null, typifiedNameUuid);
    }
}