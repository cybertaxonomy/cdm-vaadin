/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Session;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.config.SpecimenDeleteConfigurator;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
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

    static SpecimenDeleteConfigurator specimenDeleteConfigurer = new SpecimenDeleteConfigurator();
    static {
        specimenDeleteConfigurer.setDeleteChildren(true);
        specimenDeleteConfigurer.setDeleteFromDescription(true);
        specimenDeleteConfigurer.setDeleteFromIndividualsAssociation(true);
        specimenDeleteConfigurer.setDeleteFromTypeDesignation(true);
        specimenDeleteConfigurer.setDeleteMolecularData(true);
    }

    public static final List<String> TAXON_NAME_INIT_STRATEGY = Arrays.asList(new String []{
            "name.$",
            "name.nomenclaturalReference.authorship",
            "name.nomenclaturalReference.inReference",
            "name.rank",
            "name.status.type",
            "name.typeDesignations"
            }
    );

    @Autowired
    IRegistrationWorkingSetService registrationWorkingSetService;

    @Qualifier("cdmRepository")
    @Autowired
    CdmRepository repo;


    /**
     * {@inheritDoc}
     */
    @Override
    public SpecimenTypeDesignationWorkingSetDTO<Registration> create(UUID registrationUuid, UUID publicationUuid, UUID typifiedNameUuid) {
        FieldUnit newfieldUnit = FieldUnit.NewInstance();
        Registration reg = repo.getRegistrationService().load(registrationUuid, RegistrationWorkingSetService.REGISTRATION_INIT_STRATEGY);
        TaxonName typifiedName = repo.getNameService().load(typifiedNameUuid, TAXON_NAME_INIT_STRATEGY);
        Reference citation = repo.getReferenceService().load(publicationUuid, Arrays.asList("$"));
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto = new SpecimenTypeDesignationWorkingSetDTO<Registration>(reg, newfieldUnit, citation, typifiedName);
        return workingSetDto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public SpecimenTypeDesignationWorkingSetDTO<Registration> load(UUID registrationUuid, TypedEntityReference<? extends IdentifiableEntity<?>> baseEntityRef) {
        RegistrationDTO regDTO = registrationWorkingSetService.loadDtoByUuid(registrationUuid);
        // find the working set
        TypeDesignationWorkingSet typeDesignationWorkingSet = regDTO.getTypeDesignationWorkingSet(baseEntityRef);
        SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto = regDTO.getSpecimenTypeDesignationWorkingSetDTO(typeDesignationWorkingSet.getBaseEntityReference());
        return workingSetDto;
    }

    @Override
    public SpecimenTypeDesignationWorkingSetDTO<Registration> fixMissingFieldUnit(SpecimenTypeDesignationWorkingSetDTO<Registration> bean) {

        if(bean.getFieldUnit() == null){
            // in case the base unit of the working set is not a FieldUnit all contained TypeDesignations must be modified
            // so that they are based on an empty FieldUnit with an associated Gathering Event

            Registration reg = repo.getRegistrationService().find(bean.getOwner().getUuid());
            RegistrationDTO regDTO = new RegistrationDTO(reg);

            FieldUnit fieldUnit = FieldUnit.NewInstance();
            GatheringEvent gatheringEvent = GatheringEvent.NewInstance();
            fieldUnit.setGatheringEvent(gatheringEvent);
            repo.getOccurrenceService().save(fieldUnit);

            VersionableEntity baseEntity = bean.getBaseEntity();
            Set<TypeDesignationBase> typeDesignations = regDTO.getTypeDesignationsInWorkingSet(
                    new TypedEntityReference(baseEntity.getClass(), baseEntity.getUuid(), baseEntity.toString())
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
    public void save(SpecimenTypeDesignationWorkingSetDTO<? extends VersionableEntity> dto) {

        if(dto.getOwner() instanceof Registration){
            Registration regPremerge = (Registration) dto.getOwner();

            // find the newly created type designations
            Set<SpecimenTypeDesignation> newTypeDesignations = findNewTypeDesignations((SpecimenTypeDesignationWorkingSetDTO<Registration>) dto);

            FieldUnit fieldUnit = (FieldUnit) dto.getBaseEntity();

            // associate the new typeDesignations with the registration
            for(SpecimenTypeDesignation std : newTypeDesignations){
                assureFieldUnit(fieldUnit, std);
                std.setCitation(dto.getCitation());
                dto.getTypifiedName().addTypeDesignation(std, false);
                regPremerge.addTypeDesignation(std);
            }

            for(SpecimenTypeDesignationDTO stdDTO : dto.getSpecimenTypeDesignationDTOs()){
                SpecimenTypeDesignation specimenTypeDesignation = stdDTO.asSpecimenTypeDesignation();
                // associate all type designations with the fieldUnit
                assureFieldUnit(fieldUnit, specimenTypeDesignation);
            }

            Session session = repo.getSession();

//            PersistentContextAnalyzer regAnalyzer = new PersistentContextAnalyzer(dto.getOwner(), session);
//            regAnalyzer.printEntityGraph(System.out);
//            regAnalyzer.printCopyEntities(System.out);

            session.merge(dto.getOwner());
            session.flush();

            // ------------------------ perform delete of removed SpecimenTypeDesignations
            for(SpecimenTypeDesignation std : dto.deletedSpecimenTypeDesignations()){
                deleteSpecimenTypeDesignation(dto, std);
            }
            session.flush();
        }


    }

    /**
     * @param dto
     * @param specimenDeleteConfigurer
     * @param std
     */
    protected void deleteSpecimenTypeDesignation(SpecimenTypeDesignationWorkingSetDTO<? extends VersionableEntity> dto, SpecimenTypeDesignation std) {

//        if(dto.getOwner() instanceof Registration){
//            Registration registration = (Registration) dto.getOwner();
//            registration.getTypeDesignations().clear();
//            repo.getRegistrationService().save(registration);
//        } else {
//            throw new RuntimeException("Unimplemented owner type");
//        }
        DerivedUnit du = std.getTypeSpecimen();
//        DerivationEvent derivationEvent = du.getDerivedFrom();

        //du.removeSpecimenTypeDesignation(std);
        //derivationEvent.removeDerivative(du);
        std.setTypeSpecimen(null);
        repo.getOccurrenceService().delete(du, specimenDeleteConfigurer);
        repo.getNameService().deleteTypeDesignation(dto.getTypifiedName(), std);
//        if(derivationEvent.getDerivatives().size() == 0){
//          getRepo().getEventBaseService().delete(derivationEvent);
//      }
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly=false)
    public void delete(SpecimenTypeDesignationWorkingSetDTO bean, boolean deleteFieldUnit) {

        @SuppressWarnings("unchecked")
        List<SpecimenTypeDesignationDTO> specimenTypeDesignationDTOs = bean.getSpecimenTypeDesignationDTOs();
        for(SpecimenTypeDesignationDTO stdDTO : specimenTypeDesignationDTOs){
          SpecimenTypeDesignation std =  stdDTO.asSpecimenTypeDesignation();
          deleteSpecimenTypeDesignation(bean, std);
          if(bean.getOwner() instanceof Registration){
              ((Registration)bean.getOwner()).getTypeDesignations().remove(std);
          }
        }

        if(deleteFieldUnit){
            FieldUnit fu = bean.getFieldUnit();
            // delete the fieldunit and all derivatives
            DeleteResult result = repo.getOccurrenceService().delete(fu.getUuid(), specimenDeleteConfigurer);
            String msg = result.toString();
        }
    }

}
