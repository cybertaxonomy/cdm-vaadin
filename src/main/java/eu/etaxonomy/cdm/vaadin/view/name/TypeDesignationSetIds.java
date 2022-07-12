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

public abstract class TypeDesignationSetIds<BASE_T extends VersionableEntity> {

    protected UUID registrationUuid;
    protected BASE_T baseEntity;
    protected UUID typifiedNameUuid;
    /**
     * The UUID of the article, book, book section in which the type designation is being published.
     * Never a section. This information is only relevant for type designation to be initially created.
     */
    protected UUID publishedUnitUuid;

    protected TypeDesignationSetIds(UUID publishedUnitUuid, UUID registrationUuid, BASE_T baseEntity, UUID typifiedNameUuid) {
        this.publishedUnitUuid = publishedUnitUuid;
        this.registrationUuid = registrationUuid;
        this.baseEntity = baseEntity;
        this.typifiedNameUuid = typifiedNameUuid;
    }

    public UUID getTypifiedNameUuid(){
        return typifiedNameUuid;
    }

    public UUID getRegistrationUUID() {
        return registrationUuid;
    }

    public BASE_T getBaseEntity() {
        return baseEntity;
    }

    public UUID getPublishedUnitUuid() {
        return publishedUnitUuid;
    }
}