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

import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

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

    public Pager<RegistrationDTO> pageDTOs(Integer pageSize, Integer pageIndex);

    public Pager<RegistrationDTO> pageDTOs(User submitter, Collection<RegistrationStatus> includedStatus,
            String identifierFilterPattern, String taxonNameFilterPattern,
            Integer pageSize, Integer pageIndex);

    /**
     * @param referenceID
     * @return
     */
    public RegistrationWorkingSet loadWorkingSetByReferenceID(Integer referenceID) throws RegistrationValidationException;

    public Set<RegistrationDTO> loadBlockingRegistrations(Integer blockedRegistrationId);

}
