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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.dao.initializer.IBeanInitializer;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationValidationException;

/**
 * Provides RegistrationDTOs and RegistrationWorkingsets for Registrations in the database.
 *
 *
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Service("registrationWorkingSetService")
@Transactional(readOnly=true)
public class RegistrationWorkingSetService implements IRegistrationWorkingSetService {

    public static final List<String> REGISTRATION_INIT_STRATEGY = Arrays.asList(new String []{
            // typeDesignation
            "typeDesignations.typeStatus",
            "typeDesignations.typifiedNames.typeDesignations", // important !!
            "typeDesignations.typeSpecimen",
            "typeDesignations.typeName",
            "typeDesignations.citation",
            "typeDesignations.citation.authorship.$",
            // name
            "name.$",
            "name.nomenclaturalReference.authorship",
            "name.nomenclaturalReference.inReference",
            "name.rank",
            "name.status.type",
            "name.typeDesignations", // important !!"
            // institution
            "institution",
            }
    );

   /**
    *
    */
   private  List<String> DERIVEDUNIT_INIT_STRATEGY = Arrays.asList(new String[]{
           "collection",
           "storedUnder",
           "preservation",
           "recordBasis",
           "sex",
           "lifeStage",
           "kindOfUnit",
           "derivedFrom.$",
           "derivedFrom.type",
           "derivedFrom.originals.derivationEvents", // important!!
           "specimenTypeDesignations.typifiedNames.typeDesignations", // important!!
   });

   /**
   *
   */
  private List<String> FIELDUNIT_INIT_STRATEGY = Arrays.asList(new String[]{
          "$",
          "gatheringEvent.$",
          "gatheringEvent.country",
          "gatheringEvent.collectingAreas",
          "gatheringEvent.actor",
          "derivationEvents.derivatives" // important, otherwise the DerivedUnits are not included into the graph of initialized entities!!!
  });

    /**
     *
     */
    private static final int PAGE_SIZE = 50;

    private static final Logger logger = Logger.getLogger(RegistrationWorkingSetService.class);

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    protected IBeanInitializer defaultBeanInitializer;

    public RegistrationWorkingSetService() {

    }


    /**
     * @param id the Registration entity id
     * @return
     */
    @Override
    public RegistrationDTO loadDtoById(Integer id) {
        Registration reg = repo.getRegistrationService().find(id);
        inititializeSpecimen(reg);
        return new RegistrationDTO(reg);
    }


    @Override
    public Collection<RegistrationDTO> listDTOs() {

        List<Registration> regs = repo.getRegistrationService().list(null, PAGE_SIZE, 0, null, REGISTRATION_INIT_STRATEGY);

        List<RegistrationDTO> dtos = makeDTOs(regs);
        return dtos;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<RegistrationDTO> listDTOs(User submitter, Collection<RegistrationStatus> includedStatus) {

        Pager<Registration> pager = repo.getRegistrationService().page(submitter, includedStatus, PAGE_SIZE, 0, null, REGISTRATION_INIT_STRATEGY);
        List<Registration> registrations = pager.getRecords();
        return makeDTOs(registrations);
    }

    /**
     * {@inheritDoc}
     * @throws RegistrationValidationException
     */
    @Override
    public RegistrationWorkingSet loadWorkingSetByReferenceID(Integer referenceID) throws RegistrationValidationException {

        Reference reference = repo.getReferenceService().find(referenceID);
        Pager<Registration> pager = repo.getRegistrationService().page(Optional.of(reference), null, null, null, REGISTRATION_INIT_STRATEGY);
        return new RegistrationWorkingSet(makeDTOs(pager.getRecords()));
    }

    /**
     * @param regs
     * @return
     */
    private List<RegistrationDTO> makeDTOs(List<Registration> regs) {
        initializeSpecimens(regs);
        List<RegistrationDTO> dtos = new ArrayList<>(regs.size());
        regs.forEach(reg -> {dtos.add(new RegistrationDTO(reg));});
        return dtos;
    }


    /**
     * @param regs
     */
    private void initializeSpecimens(List<Registration> regs) {
        for(Registration reg : regs){
            inititializeSpecimen(reg);
        }

    }


    /**
     * @param reg
     */
    protected void inititializeSpecimen(Registration reg) {

        for(TypeDesignationBase<?> td : reg.getTypeDesignations()){
            if(td instanceof SpecimenTypeDesignation){

                DerivedUnit derivedUnit = ((SpecimenTypeDesignation) td).getTypeSpecimen();
                @SuppressWarnings("rawtypes")
                Set<SpecimenOrObservationBase> sobs = new HashSet<>();
                sobs.add(derivedUnit);

                while(sobs != null && !sobs.isEmpty()){
                    @SuppressWarnings("rawtypes")
                    Set<SpecimenOrObservationBase> nextSobs = null;
                    for(@SuppressWarnings("rawtypes") SpecimenOrObservationBase sob : sobs){
                        if(sob instanceof DerivedUnit) {
                            defaultBeanInitializer.initialize(sob, DERIVEDUNIT_INIT_STRATEGY);
                            nextSobs = ((DerivedUnit)sob).getOriginals();
                        }
                        if(sob instanceof FieldUnit){
                            defaultBeanInitializer.initialize(sob, FIELDUNIT_INIT_STRATEGY);
                        }
                    }
                    sobs = nextSobs;
                }
            }
        }
    }






}
