/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.UUID;

import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

/**
 * @author a.kohlbecker
 * @since Jan 20, 2021
 */
public class NameTypeDesignationWorkingsetIds extends TypeDesignationWorkingsetIds<NameTypeDesignation> {

    public static NameTypeDesignationWorkingsetIds forNewTypeDesignation(UUID publishedUnitUuid, UUID typifiedNameUuid) {
        return new NameTypeDesignationWorkingsetIds(publishedUnitUuid, typifiedNameUuid);
    }

    public static Object forExistingTypeDesignation(UUID publishedUnitUuid, TypedEntityReference<NameTypeDesignation> baseEntityRef) {
        return new NameTypeDesignationWorkingsetIds(publishedUnitUuid, baseEntityRef);
    }


    private NameTypeDesignationWorkingsetIds(UUID publishedUnitUuid, TypedEntityReference<NameTypeDesignation> baseEntityRef) {
        super(null, baseEntityRef, null);
        this.publishedUnitUuid = publishedUnitUuid;
    }

    private NameTypeDesignationWorkingsetIds(UUID publishedUnitUuid, UUID typifiedNameUuid) {
        super(null, null, typifiedNameUuid);
        this.publishedUnitUuid = publishedUnitUuid;
    }

    /**
     * @return
     */
    public boolean isForNewTypeDesignation() {
        return getBaseEntityRef() == null;
    }

}
