/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.api.service.exception.TypeDesignationSetException;
import eu.etaxonomy.cdm.api.service.idminter.RegistrationIdentifierMinter;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;

/**
 * @author a.kohlbecker
 * @since Mar 25, 2019
 */
@Service("registrationWorkflowService")
public class RegistrationWorkflowService implements IRegistrationWorkflowService {

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    private RegistrationIdentifierMinter minter;

    private CdmRepository getRepo() {
        return repo;
    }

    @Override
    public Registration createRegistration(TaxonName taxonName, List<Registration> preparedBlockingRegistrations) {

        if(taxonName.isPersisted()){
            getRepo().getSession().refresh(taxonName);
        }

        Registration reg = getRepo().getRegistrationService().createRegistrationForName(taxonName.getUuid());
        if(!preparedBlockingRegistrations.isEmpty()){
            for(Registration blockingReg : preparedBlockingRegistrations){
                blockingReg = getRepo().getRegistrationService().load(blockingReg.getUuid());
                reg.getBlockedBy().add(blockingReg);
            }
            // save again
            getRepo().getRegistrationService().saveOrUpdate(reg);
            preparedBlockingRegistrations.clear();
        }
        return reg;
    }

    @Override
    public boolean createRegistrationforExistingName(RegistrationWorkingSet workingset, TaxonName typifiedName) throws TypeDesignationSetException {

        boolean doReloadWorkingSet = false;
        Reference citation = getRepo().getReferenceService().load(workingset.getCitationUuid(), Arrays.asList("authorship.$", "inReference.authorship.$"));
        // here we completely ignore the ExistingNameRegistrationType since the user should not have the choice
        // to create a typification only registration in the working (publication) set which contains
        // the protolog. This is known from the nomenclatural reference.
        if(canCreateNameRegistrationFor(workingset, typifiedName)){
            // the citation which is the base for workingset contains the protolog of the name and the name has not
            // been registered before:
            // create a registration for the name and the first typifications
            Registration newRegistrationWithExistingName = getRepo().getRegistrationService().createRegistrationForName(typifiedName.getUuid());
            workingset.add(new RegistrationWrapperDTO(newRegistrationWithExistingName, typifiedName, citation));
            doReloadWorkingSet = true;
        } else {
            if(!checkWokingsetContainsProtolog(workingset, typifiedName)){
                // create a typification only registration
                Registration typificationOnlyRegistration = getRepo().getRegistrationService().newRegistration();
                if(!getRepo().getRegistrationService().checkRegistrationExistsFor(typifiedName)){
                    // oops, yet no registration for this name, so we create it as blocking registration:
                    Registration blockingNameRegistration = getRepo().getRegistrationService().createRegistrationForName(typifiedName.getUuid());
                    typificationOnlyRegistration.getBlockedBy().add(blockingNameRegistration);
                }
                RegistrationWrapperDTO regDTO = new RegistrationWrapperDTO(typificationOnlyRegistration, typifiedName, citation);
                workingset.add(regDTO);
            }
        }
        return doReloadWorkingSet;
    }

    @Override
    public void addTypeDesignation(UUID typeDesignationUuid, Registration registration) {
        registration = reloadRegistration(registration);
        getRepo().getRegistrationService().addTypeDesignation(registration, typeDesignationUuid);
        getRepo().getRegistrationService().saveOrUpdate(registration);
    }

    @Override
    public Registration prepareBlockingRegistration(UUID nameUUID) {

        TaxonName name = getRepo().getNameService().load(nameUUID);

        boolean registrationExists = false;
        for(Registration regForName : name.getRegistrations()){
            if(minter.identifierPattern().matcher(regForName.getIdentifier()).matches()){
                registrationExists = true;
                break;
            }
        }

        if(!registrationExists){
            Registration blockingRegistration = getRepo().getRegistrationService().createRegistrationForName(nameUUID);
            return blockingRegistration;
        }
        return null;
    }

    @Override
    public Registration addBlockingRegistration(UUID nameUUID, Registration registration) {

        Registration blockingRegistration = prepareBlockingRegistration(nameUUID);
        if(blockingRegistration != null){
            registration = reloadRegistration(registration);
            registration.getBlockedBy().add(blockingRegistration);
            if(registration.isPersisted()){ // shoul't this be !registration.isPersisted() ? since the saveOrUpdate might have been hit this code could be useless
                getRepo().getRegistrationService().saveOrUpdate(registration);
                logger.debug("Blocking registration created, added to registion and persisted");
            }
            return blockingRegistration;
        }
        return null;
    }

    @Override
    public Registration reloadRegistration(Registration registration) {
        if(registration.isPersisted()){
             Registration registrationReloaded = getRepo().getRegistrationService().load(registration.getUuid());
             if(registrationReloaded == null){
                 throw new NullPointerException("Registration not found for Registration#" + registration.getUuid() + " which has been hold in the rootContext");
             }
             registration = registrationReloaded;
         } else {
             logger.trace("Registration is not yet persisted.");
         }

        return registration;
    }

    /**
     * Checks
     * <ol>
     * <li>if there is NOT any registration for this name created in the current registration system</li>
     * <li>Checks if the name belongs to the current workingset</li>
     * </ol>
     * If both checks are successful the method returns <code>true</code>.
     */
    @Override
    public boolean canCreateNameRegistrationFor(RegistrationWorkingSet workingset, TaxonName name) {
        return !getRepo().getRegistrationService().checkRegistrationExistsFor(name)
                && checkWokingsetContainsProtolog(workingset, name);
    }

    @Override
    public boolean checkWokingsetContainsProtolog(RegistrationWorkingSet workingset, TaxonName name) {
        Reference nomRef = name.getNomenclaturalReference();
        UUID citationUuid = workingset.getCitationUuid();
        // @formatter:off
        return nomRef != null
                // nomref matches
                && (nomRef.getUuid().equals(citationUuid)
                    // nomref.inreference matches
                    || (nomRef.getType() != null
                         && nomRef.getType() == ReferenceType.Section
                         && nomRef.getInReference() != null
                         && nomRef.getInReference().getUuid().equals(citationUuid)
                )
            );
        // @formatter:on
    }
}