/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.List;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.exception.RegistrationValidationException;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonName;

/**
 * @author a.kohlbecker
 * @since Mar 26, 2019
 *
 */
public interface IRegistrationWorkflowService {

    /**
     * @param taxonName
     * @param relatedBlockingRegistrations
     *  The blocking registrations to be added to the new Registration
     */
    @Transactional
    Registration createRegistration(TaxonName taxonName, List<Registration> relatedBlockingRegistrations);

    @Transactional
    boolean createRegistrationforExistingName(RegistrationWorkingSet workingset, TaxonName typifiedName) throws RegistrationValidationException;

    @Transactional(readOnly=true)
    Registration reloadRegistration(Registration registration);

    /**
     * Creates a new registration for the name referenced by the {@code taxonNameUUID} parameter
     * if there isn't one already and adds this new registration as blocking registration to the passed
     * {@code registration}.
     *
     * Already existing registrations must not be added as blocking registration.
     *
     * @param taxonNameUUID
     * @param registration
     */
    @Transactional
    Registration addBlockingRegistration(UUID nameUUID, Registration registration);

    @Transactional
    void addTypeDesignation(UUID typeDesignationUuid, Registration registration);

    @Transactional(readOnly=true)
    boolean canCreateNameRegistrationFor(RegistrationWorkingSet workingset, TaxonName name);

    boolean checkWokingsetContainsProtologe(RegistrationWorkingSet workingset, TaxonName name);

}
