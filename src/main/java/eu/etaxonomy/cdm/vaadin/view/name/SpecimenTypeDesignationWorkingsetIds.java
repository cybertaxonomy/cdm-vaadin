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

import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

/**
 * @author a.kohlbecker
 * @since Jan 20, 2021
 */
public class SpecimenTypeDesignationWorkingsetIds extends TypeDesignationWorkingsetIds<FieldUnit> {

    /**
     * @param registrationUuid
     * @param baseEntityRef
     * @param typifiedNameUuid
     */
    public SpecimenTypeDesignationWorkingsetIds(UUID publishedUnitUuid, UUID registrationUuid, TypedEntityReference<FieldUnit> baseEntityRef, UUID typifiedNameUuid) {
        super(publishedUnitUuid, registrationUuid, baseEntityRef, typifiedNameUuid);
    }

}
