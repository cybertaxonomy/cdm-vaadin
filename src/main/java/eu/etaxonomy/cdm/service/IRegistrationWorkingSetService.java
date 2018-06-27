/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.exception.RegistrationValidationException;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;

/**
 * @author a.kohlbecker
 * @since May 4, 2017
 *
 */
public interface IRegistrationWorkingSetService {

    /**
     * @param id the CDM Entity id
     * @return
     */
    public RegistrationDTO loadDtoById(Integer id);

    public RegistrationDTO loadDtoByUuid(UUID uuid);

    public Pager<RegistrationDTO> pageDTOs(Integer pageSize, Integer pageIndex);

    public Pager<RegistrationDTO> pageDTOs(User submitter, Collection<RegistrationStatus> includedStatus,
            String identifierFilterPattern, String taxonNameFilterPattern,
            Integer pageSize, Integer pageIndex);

    /**
     * @param referenceID
     * @param resolveSections resolve the higher publication unit and build the RegistrationWorkingSet for that reference. E.e. For journal sections the
     *  use the inReference which is the journal article.
     *
     * @return
     */
    public RegistrationWorkingSet loadWorkingSetByReferenceID(Integer referenceID, boolean resolveSections) throws RegistrationValidationException;

    /**
     * @param referenceID
     * @param resolveSections resolve the higher publication unit and build the RegistrationWorkingSet for that reference. E.e. For journal sections the
     *  use the inReference which is the journal article.
     * @return
     */
    public RegistrationWorkingSet loadWorkingSetByReferenceUuid(UUID referenceUuid, boolean resolveSections) throws RegistrationValidationException;

    public Set<RegistrationDTO> loadBlockingRegistrations(UUID blockedRegistrationUuid);

}
