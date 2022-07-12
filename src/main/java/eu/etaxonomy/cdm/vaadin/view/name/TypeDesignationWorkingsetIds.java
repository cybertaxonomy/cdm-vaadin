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

import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

public abstract class TypeDesignationWorkingsetIds<BASE_T extends VersionableEntity> {

    protected UUID registrationUuid;
    protected TypedEntityReference<BASE_T> baseEntityRef;
    protected UUID typifiedNameUuid;
    /**
     * The UUID of the article, book, book section in which the type designation is being published.
     * Never a section. This information is only relevant for type designation to be initially created.
     */
    protected UUID publishedUnitUuid;

    protected TypeDesignationWorkingsetIds(UUID publishedUnitUuid, UUID registrationUuid, TypedEntityReference<BASE_T> baseEntityRef, UUID typifiedNameUuid) {
        this.publishedUnitUuid = publishedUnitUuid;
        this.registrationUuid = registrationUuid;
        this.baseEntityRef = baseEntityRef;
        this.typifiedNameUuid = typifiedNameUuid;
    }

    public UUID getTypifiedNameUuid(){
        return typifiedNameUuid;
    }

    public UUID getRegistrationUUID() {
        return registrationUuid;
    }

    public TypedEntityReference<BASE_T> getBaseEntityRef() {
        return baseEntityRef;
    }

    public UUID getPublishedUnitUuid() {
        return publishedUnitUuid;
    }
}