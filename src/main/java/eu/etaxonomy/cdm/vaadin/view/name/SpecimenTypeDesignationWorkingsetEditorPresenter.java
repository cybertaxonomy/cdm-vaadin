/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.viritin.fields.AbstractElementCollection;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.cache.CdmEntityCache;
import eu.etaxonomy.cdm.cache.EntityCache;
import eu.etaxonomy.cdm.model.common.DefinedTerm;
import eu.etaxonomy.cdm.model.location.Country;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.ISpecimenTypeDesignationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
/**
 * SpecimenTypeDesignationWorkingsetPopupEditorView implementation must override the showInEditor() method,
 * see {@link #prepareAsFieldGroupDataSource()} for details.
 *
 * @author a.kohlbecker
 * @since Jun 13, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetEditorPresenter
    extends AbstractEditorPresenter<SpecimenTypeDesignationWorkingSetDTO , SpecimenTypeDesignationWorkingsetPopupEditorView>
    implements CachingPresenter {

    private static final long serialVersionUID = 4255636253714476918L;

    CdmStore<Registration, IRegistrationService> store;


    /**
     * This object for this field will either be injected by the {@link PopupEditorFactory} or by a Spring
     * {@link BeanFactory}
     */
    @Autowired
    private ISpecimenTypeDesignationWorkingSetService specimenTypeDesignationWorkingSetService;

    /**
     * if not null, this CRUD set is to be used to create a CdmAuthoritiy for the base entitiy which will be
     * granted to the current use as long this grant is not assigned yet.
     */
    private EnumSet<CRUD> crud = null;

    private CdmEntityCache cache = null;

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
    protected SpecimenTypeDesignationWorkingSetDTO<Registration> loadBeanById(Object identifier) {

        SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto;
        if(identifier != null){

            TypeDesignationWorkingsetEditorIdSet idset = (TypeDesignationWorkingsetEditorIdSet)identifier;

            if(idset.workingsetId != null){
                workingSetDto = specimenTypeDesignationWorkingSetService.loadDtoByIds(idset.registrationId, idset.workingsetId);
                if(workingSetDto.getFieldUnit() == null){
                    workingSetDto = specimenTypeDesignationWorkingSetService.fixMissingFieldUnit(workingSetDto);
                        // FIXME open Dialog to warn user about adding an empty fieldUnit to the typeDesignations
                        //       This method must go again into the presenter !!!!
                        logger.info("Basing all typeDesignations on a new fieldUnit");
                }
                cache = new CdmEntityCache(workingSetDto.getOwner());
            } else {
                // create a new workingset, for a new fieldunit which is the base for the workingset
                workingSetDto = specimenTypeDesignationWorkingSetService.create(idset.registrationId, idset.publicationId, idset.typifiedNameId);
                cache = new CdmEntityCache(workingSetDto.getOwner());
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

            CdmFilterablePagingProvider<Collection> collectionPagingProvider = new CdmFilterablePagingProvider<Collection>(getRepo().getCollectionService());

            CdmFilterablePagingProvider<Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference>(getRepo().getReferenceService());

            @Override
            public SpecimenTypeDesignationDTORow create() {

                SpecimenTypeDesignationDTORow row = new SpecimenTypeDesignationDTORow();

                row.kindOfUnit.setContainerDataSource(selectFactory.buildTermItemContainer(
                        RegistrationTermLists.KIND_OF_UNIT_TERM_UUIDS())
                        );
                row.kindOfUnit.setNullSelectionAllowed(false);

                row.kindOfUnit.addValueChangeListener(e -> {
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
                row.collection.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Collection>(row.collection.getSelect(), SpecimenTypeDesignationWorkingsetEditorPresenter.this));

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

                DefinedTerm kindOfUnit = (DefinedTerm)row.kindOfUnit.getValue();

                boolean publishedImageType = kindOfUnit != null && kindOfUnit.equals(KindOfUnitTerms.PUBLISHED_IMAGE());
                boolean unPublishedImageType = kindOfUnit != null && kindOfUnit.equals(KindOfUnitTerms.UNPUBLISHED_IMAGE());

                row.mediaSpecimenReference.setEnabled(publishedImageType);
                row.mediaSpecimenReferenceDetail.setEnabled(publishedImageType);
                row.mediaUri.setEnabled(unPublishedImageType);

            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(SpecimenTypeDesignationWorkingSetDTO dto) {
        if(crud != null){
            UserHelper.fromSession().createAuthorityForCurrentUser(dto.getFieldUnit(), crud, null);
        }

        specimenTypeDesignationWorkingSetService.save(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(SpecimenTypeDesignationWorkingSetDTO bean) {
        // TODO Auto-generated method stub

    }

    /**
     * @param crud
     */
    public void setGrantsForCurrentUser(EnumSet<CRUD> crud) {
        this.crud = crud;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntityCache getCache() {
        if(((AbstractPopupEditor)getView()).isBeanLoaded()){
            return cache;
        } else {
            return null;
        }
    }


}
