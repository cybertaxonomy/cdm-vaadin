/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.DerivationEvent;
import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;

/**
 * @author a.kohlbecker
 * @since Nov 13, 2017
 *
 */
@Service("specimenTypeDesignationWorkingSetService")
@Transactional(readOnly=true)
public class SpecimenTypeDesignationWorkingSetServiceImpl implements ISpecimenTypeDesignationWorkingSetService {

    private final Logger logger = Logger.getLogger(SpecimenTypeDesignationWorkingSetServiceImpl.class);

    @Autowired
    IRegistrationWorkingSetService registrationWorkingSetService;

    @Qualifier("cdmRepository")
    @Autowired
    CdmRepository repo;


    /**
     * {@inheritDoc}
     */
    @Override
    public SpecimenTypeDesignationWorkingSetDTO<Registration> create(int registrationId, int publicationId, int typifiedNameId) {
        FieldUnit newfieldUnit = FieldUnit.NewInstance();
        Registration reg = repo.getRegistrationService().load(registrationId, RegistrationWorkingSetService.REGISTRATION_INIT_STRATEGY);
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto = new SpecimenTypeDesignationWorkingSetDTO<Registration>(reg, newfieldUnit, publicationId, typifiedNameId);
        return workingSetDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SpecimenTypeDesignationWorkingSetDTO<Registration> loadDtoByIds(int registrationId, int workingsetId) {
        RegistrationDTO regDTO = registrationWorkingSetService.loadDtoById(registrationId);
        // find the working set
        TypeDesignationWorkingSet typeDesignationWorkingSet = regDTO.getTypeDesignationWorkingSet(workingsetId);
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto = regDTO.getSpecimenTypeDesignationWorkingSetDTO(typeDesignationWorkingSet.getBaseEntityReference());
        workingSetDto.setCitationEntityID(regDTO.getCitation().getId());
        workingSetDto.setTypifiedNameEntityID(regDTO.getTypifiedName().getId());
        return workingSetDto;
    }

    @Override
    public SpecimenTypeDesignationWorkingSetDTO<Registration> fixMissingFieldUnit(SpecimenTypeDesignationWorkingSetDTO<Registration> bean) {

        if(bean.getFieldUnit() == null){
            // in case the base unit of the working set is not a FieldUnit all contained TypeDesignations must be modified
            // so that they are based on an empty FieldUnit with an associated Gathering Event

            Registration reg = repo.getRegistrationService().find(bean.getOwner().getId());
            RegistrationDTO regDTO = new RegistrationDTO(reg);

            FieldUnit fieldUnit = FieldUnit.NewInstance();
            GatheringEvent gatheringEvent = GatheringEvent.NewInstance();
            fieldUnit.setGatheringEvent(gatheringEvent);
            repo.getOccurrenceService().save(fieldUnit);

            VersionableEntity baseEntity = bean.getBaseEntity();
            Set<TypeDesignationBase> typeDesignations = regDTO.getTypeDesignationsInWorkingSet(
                    new TypedEntityReference(baseEntity.getClass(), baseEntity.getId(), baseEntity.toString())
                    );
            for(TypeDesignationBase td : typeDesignations){
                DerivationEvent de = DerivationEvent.NewInstance();//
                de.addOriginal(fieldUnit);
                de.addDerivative(((SpecimenTypeDesignation)td).getTypeSpecimen());
                de.setType(DerivationEventType.GATHERING_IN_SITU());
            }

            repo.getRegistrationService().saveOrUpdate(reg);
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly=false)
    public void save(SpecimenTypeDesignationWorkingSetDTO<? extends VersionableEntity> dto, Reference citation, TaxonName typifiedName) {

        if(dto.getOwner() instanceof Registration){
            Registration regPremerge = (Registration) dto.getOwner();

            // find the newly created type designations
            Set<SpecimenTypeDesignation> newTypeDesignations = findNewTypeDesignations((SpecimenTypeDesignationWorkingSetDTO<Registration>) dto);

            FieldUnit fieldUnit = (FieldUnit) dto.getBaseEntity();

            // associate the new typeDesignations with the registration
            for(SpecimenTypeDesignation std : newTypeDesignations){
                assureFieldUnit(fieldUnit, std);
                std.setCitation(citation);
                typifiedName.addTypeDesignation(std, false);
                regPremerge.addTypeDesignation(std);
            }

            for(SpecimenTypeDesignationDTO stdDTO : dto.getSpecimenTypeDesignationDTOs()){
                SpecimenTypeDesignation specimenTypeDesignation = stdDTO.asSpecimenTypeDesignation();
                // associate all type designations with the fieldUnit
                assureFieldUnit(fieldUnit, specimenTypeDesignation);
            }

            Session session = repo.getSession();

            session.merge(dto.getOwner());

            session.flush();
        }


    }

    /**
     * @param session
     * @param fieldUnit
     * @param specimenTypeDesignation
     */
    protected void assureFieldUnit(FieldUnit fieldUnit,
            SpecimenTypeDesignation specimenTypeDesignation) {
        try {
            SpecimenOrObservationBase<?> original = findEarliestOriginal(specimenTypeDesignation.getTypeSpecimen());
            if(original instanceof DerivedUnit){
                DerivedUnit du = (DerivedUnit)original;
                du.getDerivedFrom().addOriginal(fieldUnit);
            }
        } catch (Exception e) {
            // has more than one originals !!!
            logger.error(e);
        }
    }

    /**
     * @param std
     * @return
     * @throws Exception
     */
    private SpecimenOrObservationBase<?> findEarliestOriginal(DerivedUnit du) throws Exception {

        SpecimenOrObservationBase original = du;

        while(du != null && du.getDerivedFrom() != null && !du.getDerivedFrom().getOriginals().isEmpty()) {
            Iterator<SpecimenOrObservationBase> it = du.getDerivedFrom().getOriginals().iterator();
            SpecimenOrObservationBase nextOriginal = it.next();
            if(nextOriginal == null){
                break;
            }
            original = nextOriginal;
            if(original instanceof DerivedUnit){
                du = (DerivedUnit)original;
            } else {
                // so this must be a FieldUnit,
               break;
            }
            if(it.hasNext()){
                throw new Exception(String.format("%s has more than one originals", du.toString()));
            }
        }
        return original;
    }

    private Set<SpecimenTypeDesignation> findNewTypeDesignations(SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto) {

        Registration reg = workingSetDto.getOwner();
        Set<SpecimenTypeDesignation> addCandidates = new HashSet<>();
        for(SpecimenTypeDesignationDTO stdDTO : workingSetDto.getSpecimenTypeDesignationDTOs()){
            SpecimenTypeDesignation std = stdDTO.asSpecimenTypeDesignation();
            if(reg.getTypeDesignations().isEmpty() || !reg.getTypeDesignations().stream().filter(td -> td.equals(std)).findFirst().isPresent()){
                addCandidates.add(std);
            }
        }
        return addCandidates;
    }

}
