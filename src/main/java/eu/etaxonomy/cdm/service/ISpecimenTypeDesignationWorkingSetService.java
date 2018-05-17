/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.UUID;

import eu.etaxonomy.cdm.api.service.dto.TypedEntityReference;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;

/**
 * @author a.kohlbecker
 * @since May 4, 2017
 *
 */
public interface ISpecimenTypeDesignationWorkingSetService {

    public SpecimenTypeDesignationWorkingSetDTO<Registration> create(UUID registrationUuid, UUID publicationUuid, UUID typifiedNameUuid);

    /**
     * @param id the CDM Entity id
     * @return
     */
    public SpecimenTypeDesignationWorkingSetDTO<Registration> load(UUID registrationUuid,
            TypedEntityReference<? extends IdentifiableEntity<?>> baseEntityRef);

    SpecimenTypeDesignationWorkingSetDTO<Registration> fixMissingFieldUnit(SpecimenTypeDesignationWorkingSetDTO<Registration> bean);


    /**
     * Saves the SpecimenTypeDesignationWorkingSetDTO and takes care for consistency in the working set:
     * <ul>
     *  <li>New TypeDesignations are associated with the OWNER.</li>
     *  <li>The citation and typified name of newly created TypeDesignations are set.</li>
     *  <li>All type specimens are assured to be derivatives of the FieldUnit which is the base entity of the set.</li>
     * </ul>
     *
     * @param dto the DTO to be persisted
     */
    void save(SpecimenTypeDesignationWorkingSetDTO<? extends VersionableEntity> dto);

    /**
     * @param bean The SpecimenTypeDesignationWorkingSetDTO  to be deleted
     * @param deleteFieldUnit The fieldunit and all derivatives which is the base entity of the workingset will
     * also be deleted once this is set to <code>true</code>.
     */
    public void delete(SpecimenTypeDesignationWorkingSetDTO bean, boolean deleteFieldUnit);

}
