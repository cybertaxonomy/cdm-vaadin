/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;
import org.vaadin.viritin.fields.AbstractElementCollection;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.location.Country;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.occurrence.DerivationEvent;
import eu.etaxonomy.cdm.model.occurrence.DerivationEventType;
import eu.etaxonomy.cdm.model.occurrence.DerivedUnit;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.occurrence.GatheringEvent;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.DerivationEventTypes;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
/**
 * SpecimenTypeDesignationWorkingsetPopupEditorView implementation must override the showInEditor() method,
 * see {@link #prepareAsFieldGroupDataSource()} for details.
 *
 * @author a.kohlbecker
 * @since Jun 13, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetEditorPresenter
    extends AbstractEditorPresenter<SpecimenTypeDesignationWorkingSetDTO , SpecimenTypeDesignationWorkingsetPopupEditorView> {

    private static final long serialVersionUID = 4255636253714476918L;

    CdmStore<Registration, IRegistrationService> store;

    private Reference citation;

    private TaxonName typifiedName;

    protected CdmStore<Registration, IRegistrationService> getStore() {
        if(store == null){
            store = new CdmStore<>(getRepo(), getRepo().getRegistrationService());
        }
        return store;
    }


    /**
     * Loads an existing working set from the database. This process actually involves
     * loading the Registration specified by the <code>RegistrationAndWorkingsetId.registrationId</code> and in
     * a second step to find the workingset by the <code>registrationAndWorkingsetId.workingsetId</code>.
     * <p>
     * The <code>identifier</code> must be of the type {@link TypeDesignationWorkingsetEditorIdSet} whereas the field <code>egistrationId</code>
     * must be present, the field <code>workingsetId</code>,  however can be null. I this case a new workingset with a new {@link FieldUnit} as
     * base entity is being created.
     *
     * @param identifier a {@link TypeDesignationWorkingsetEditorIdSet}
     */
    @Override
    protected SpecimenTypeDesignationWorkingSetDTO loadBeanById(Object identifier) {

        SpecimenTypeDesignationWorkingSetDTO workingSetDto;
        if(identifier != null){
            TypeDesignationWorkingsetEditorIdSet idset = (TypeDesignationWorkingsetEditorIdSet)identifier;
            Registration reg = getRepo().getRegistrationService().loadByIds(Arrays.asList(idset.registrationId), null).get(0);
            if(idset.workingsetId != null){
                RegistrationDTO regDTO = new RegistrationDTO(reg);
                // find the working set
                TypeDesignationWorkingSet typeDesignationWorkingSet = regDTO.getTypeDesignationWorkingSet(idset.workingsetId);
                workingSetDto = regDTO.getSpecimenTypeDesignationWorkingSetDTO(typeDesignationWorkingSet.getBaseEntityReference());
                citation = (Reference) regDTO.getCitation();
            } else {
                // create a new workingset, for a new fieldunit which is the base for the workingset
                FieldUnit newfieldUnit = FieldUnit.NewInstance();
                workingSetDto = new SpecimenTypeDesignationWorkingSetDTO(reg, newfieldUnit, null);
                citation = getRepo().getReferenceService().find(idset.publicationId);
                typifiedName = getRepo().getNameService().find(idset.typifiedNameId);
            }
        } else {
            workingSetDto = null;
        }
        return workingSetDto;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("serial")
    @Override
    public void handleViewEntered() {

        CdmBeanItemContainerFactory selectFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getCountrySelectField().setContainerDataSource(selectFactory.buildBeanItemContainer(Country.uuidCountryVocabulary));

        getView().getTypeDesignationsCollectionField().setEditorInstantiator(new AbstractElementCollection.Instantiator<SpecimenTypeDesignationDTORow>() {

            CdmFilterablePagingProvider<Collection> collectionPagingProvider = new CdmFilterablePagingProvider<Collection>(getRepo().getCollectionService(), SpecimenTypeDesignationWorkingsetEditorPresenter.this);

            CdmFilterablePagingProvider<Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference>(getRepo().getReferenceService(), SpecimenTypeDesignationWorkingsetEditorPresenter.this);

            @Override
            public SpecimenTypeDesignationDTORow create() {

                SpecimenTypeDesignationDTORow row = new SpecimenTypeDesignationDTORow();

                row.derivationEventType.setContainerDataSource(selectFactory.buildTermItemContainer(
                        RegistrationTermLists.DERIVATION_EVENT_TYPE_UUIDS())
                        );
                row.derivationEventType.setNullSelectionAllowed(false);

                row.derivationEventType.addValueChangeListener(e -> {
                    SpecimenTypeDesignationDTORow currentRow = row;
                    updateRowItemEnablement(currentRow);
                });

                row.typeStatus.setContainerDataSource(selectFactory.buildTermItemContainer(
                        RegistrationTermLists.SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS())
                        );
                row.typeStatus.setNullSelectionAllowed(false);


                row.collection.loadFrom(
                        collectionPagingProvider,
                        collectionPagingProvider,
                        collectionPagingProvider.getPageSize()
                        );
                row.collection.getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Collection>());
                row.collection.getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Collection>(row.collection));

                row.mediaSpecimenReference.loadFrom(
                        referencePagingProvider,
                        referencePagingProvider,
                        collectionPagingProvider.getPageSize()
                        );
                row.mediaSpecimenReference.getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
                row.mediaSpecimenReference.getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(row.mediaSpecimenReference));

                getView().applyDefaultComponentStyle(row.components());

                updateRowItemEnablement(row);

                return row;
            }

            private void updateRowItemEnablement(SpecimenTypeDesignationDTORow row) {

                DerivationEventType derivationEventType = (DerivationEventType)row.derivationEventType.getValue();

                boolean publishedImageType = derivationEventType != null && derivationEventType.equals(DerivationEventTypes.PUBLISHED_IMAGE());
                boolean unPublishedImageType = derivationEventType != null && derivationEventType.equals(DerivationEventTypes.UNPUBLISHED_IMAGE());

                row.mediaSpecimenReference.setEnabled(publishedImageType);
                row.mediaSpecimenReferenceDetail.setEnabled(publishedImageType);
                row.mediaUri.setEnabled(unPublishedImageType);

            }
        });
    }

    /**
     * {@inheritDoc}
     *
     * see also {@link SpecimenTypeDesignationWorkingsetPopupEditor#showInEditor()}
     */
    @Override
    protected SpecimenTypeDesignationWorkingSetDTO prepareAsFieldGroupDataSource(SpecimenTypeDesignationWorkingSetDTO bean) {

        if(bean.getFieldUnit() == null){
            // in case the base unit of the working set is not a FieldUnit all contained TypeDesignations must be modified
            // so that they are based on an empty FieldUnit with an associated Gathering Event
            if(Registration.class.isAssignableFrom(bean.getOwner().getClass())){
                // FIXME open Dialog to warn user about adding an empty fieldUnit to the typeDesignations
                logger.info("Basing all typeDesignations on a new fieldUnit");
                Session session = getSession();
                Registration reg = getRepo().getRegistrationService().find(bean.getOwner().getId());
                RegistrationDTO regDTO = new RegistrationDTO(reg);

                FieldUnit fieldUnit = FieldUnit.NewInstance();
                GatheringEvent gatheringEvent = GatheringEvent.NewInstance();
                fieldUnit.setGatheringEvent(gatheringEvent);
                getRepo().getOccurrenceService().save(fieldUnit);

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

                getRepo().getRegistrationService().saveOrUpdate(reg);
                session.flush();
                session.close();
            } else {
                throw new RuntimeException("Usupported owner type " + bean.getOwner().getClass() + ", needs to be implemented.");
            }
        }
        return super.prepareAsFieldGroupDataSource(bean);
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(SpecimenTypeDesignationWorkingSetDTO dto) {

        Registration reg = (Registration) dto.getOwner();

        // associate all type designations with the fieldUnit
        // 1. new ones are not yet associated
        // 2. ones which had incomplete data are also not connected
        for(SpecimenTypeDesignationDTO stdDTO : dto.getSpecimenTypeDesignationDTOs()){
            try {
                SpecimenOrObservationBase<?> original = findEarliestOriginal(stdDTO.asSpecimenTypeDesignation().getTypeSpecimen());
                if(original instanceof DerivedUnit){
                    DerivedUnit du = (DerivedUnit)original;
                    du.getDerivedFrom().addOriginal(dto.getFieldUnit());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        // add newly added typeDesignations
        Set<SpecimenTypeDesignation> addCandidates = new HashSet<>();
        for(SpecimenTypeDesignationDTO stdDTO : dto.getSpecimenTypeDesignationDTOs()){
            SpecimenTypeDesignation std = stdDTO.asSpecimenTypeDesignation();
            if(reg.getTypeDesignations().isEmpty() || !reg.getTypeDesignations().stream().filter(td -> td.equals(std)).findFirst().isPresent()){
                std.setCitation(citation);
                typifiedName.addTypeDesignation(std, false);
                addCandidates.add(std);
            }
        }
        addCandidates.forEach(std -> reg.addTypeDesignation(std));

        getStore().saveBean(reg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(SpecimenTypeDesignationWorkingSetDTO bean) {
        // TODO Auto-generated method stub

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


}
