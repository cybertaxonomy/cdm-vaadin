/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;

public class TypeDesignationWorkingsetEditorIdSet {

    Integer registrationId;
    TypedEntityReference<IdentifiableEntity<?>> baseEntityRef;
    Integer publicationId;
    Integer typifiedNameId;

    /**
     * @param registrationId
     *            must be present
     * @param workingsetId
     *            can <code>null</code>. In this case the <code>publicationId</code>
     *            and and typifiedNameId must be set.
     *            <code>RegistrationAndWorkingsetId</code> refers to a not yet
     *            existing working set, which should be created by the code in
     *            case this makes sense.
     * @param publicationId
     *            Can <code>null</code> if the <code>workingsetId</code> is given.
     * @param typifiedNameId
     *            Can <code>null</code> if the <code>workingsetId</code> is given
     */
    protected TypeDesignationWorkingsetEditorIdSet(Integer registrationId, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef, Integer publicationId, Integer typifiedNameId) {
        this.registrationId = registrationId;
        this.baseEntityRef = baseEntityRef;
        this.publicationId = publicationId;
        this.typifiedNameId = typifiedNameId;
        if(baseEntityRef == null && publicationId == null|| baseEntityRef == null && typifiedNameId == null){
            throw new NullPointerException("When workingsetId is null, publicationId and typifiedNameId must be non null.");
        }
    }

    public TypeDesignationWorkingsetEditorIdSet(Integer registrationId, TypedEntityReference<IdentifiableEntity<?>> baseEntityRef) {
        this(registrationId, baseEntityRef, null, null);
    }

    public TypeDesignationWorkingsetEditorIdSet(Integer registrationId, Integer publicationId, Integer typifiedNameId) {
        this(registrationId, null, publicationId, typifiedNameId);
    }
}