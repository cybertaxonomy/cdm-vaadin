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

    public NameTypeDesignationWorkingsetIds(UUID registrationUuid, TypedEntityReference<NameTypeDesignation> baseEntityRef) {
        super(registrationUuid, baseEntityRef, null);
    }

    public NameTypeDesignationWorkingsetIds(UUID registrationUuid, UUID typifiedNameUuid) {
        super(registrationUuid, null, typifiedNameUuid);
    }
}
