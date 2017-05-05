/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

import java.util.Collection;

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


    public Collection<RegistrationDTO> listDTOs();

    /**
     * @param  id the CDM Entity id
     * @return
     * @throws RegistrationValidationException
     */
    public RegistrationWorkingSet loadWorkingSetByRegistrationID(Integer id) throws RegistrationValidationException;

    public static final String ACTIVE_IMPL = "registrationWorkingSetService";

}
