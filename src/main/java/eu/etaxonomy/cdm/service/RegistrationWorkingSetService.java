/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * Provides RegistrationDTOs and RegistrationWorkingsets for Registrations in the database.
 * <p>
 * Can create missing registrations for names which have Extensions of the Type <code>IAPTRegdata.json</code>.
 * See https://dev.e-taxonomy.eu/redmine/issues/6621 for further details.
 * This feature can be activated by by supplying one of the following jvm command line arguments:
 * <ul>
 * <li><code>-DregistrationCreate=iapt</code>: create all iapt Registrations if missing</li>
 * <li><code>-DregistrationWipeout=iapt</code>: remove all iapt Registrations</li>
 * <li><code>-DregistrationWipeout=all</code>: remove all Registrations</li>
 * </ul>
 * The <code>-DregistrationWipeout</code> commands are executed before the <code>-DregistrationCreate</code> and will not change the name and type designations.
 *
 *
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Service("registrationWorkingSetService")
@Transactional(readOnly=true)
public class RegistrationWorkingSetService implements IRegistrationWorkingSetService {

    /**
     *
     */
    private static final int PAGE_SIZE = 50;

    private static final Logger logger = Logger.getLogger(RegistrationWorkingSetService.class);

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    public RegistrationWorkingSetService() {

    }


    /**
     * @param id the CDM Entity id
     * @return
     */
    @Override
    public RegistrationDTO loadDtoById(Integer id) {
        Registration reg = repo.getRegistrationService().find(id);
        return new RegistrationDTO(reg);
    }


    @Override
    public Collection<RegistrationDTO> listDTOs() {

        List<Registration> regs = repo.getRegistrationService().list(null, PAGE_SIZE, 0, null, null);

        List<RegistrationDTO> dtos = makeDTOs(regs);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RegistrationDTO> listDTOs(User submitter, Collection<RegistrationStatus> includedStatus) {

        Pager<Registration> pager = repo.getRegistrationService().page(submitter, includedStatus, PAGE_SIZE, 0, null, null);
        return makeDTOs(pager.getRecords());
    }

    /**
     * @param  id the CDM Entity id
     * @return
     * @throws RegistrationValidationException
     */
    @Override
    public RegistrationWorkingSet loadWorkingSetByRegistrationID(Integer id) throws RegistrationValidationException {

        RegistrationDTO dto = loadDtoById(id);

        Pager<Registration> pager = repo.getRegistrationService().page(Optional.of((Reference)dto.getCitation()), null, null, null, null);

        return new RegistrationWorkingSet(makeDTOs(pager.getRecords()));
    }


    /**
     * @param regs
     * @return
     */
    private List<RegistrationDTO> makeDTOs(List<Registration> regs) {
        List<RegistrationDTO> dtos = new ArrayList<>(regs.size());
        regs.forEach(reg -> {dtos.add(new RegistrationDTO(reg));});
        return dtos;
    }




}
