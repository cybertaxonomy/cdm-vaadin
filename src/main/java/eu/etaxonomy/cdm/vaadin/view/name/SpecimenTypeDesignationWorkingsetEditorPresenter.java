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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.AbstractElementCollection;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.cache.CdmTransientEntityCacher;
import eu.etaxonomy.cdm.model.ICdmCacher;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.location.Country;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.ISpecimenTypeDesignationWorkingSetService;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.service.initstrategies.AgentBaseInit;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowItemCollection;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEventFilter;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.CollectionCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.occurrence.CollectionPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
/**
 * SpecimenTypeDesignationWorkingsetPopupEditorView implementation must override the showInEditor() method,
 * see {@link #prepareAsFieldGroupDataSource()} for details.
 *
 * @author a.kohlbecker
 * @since Jun 13, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class SpecimenTypeDesignationWorkingsetEditorPresenter
    extends AbstractEditorPresenter<SpecimenTypeDesignationWorkingSetDTO , SpecimenTypeDesignationWorkingsetPopupEditorView>
    implements CachingPresenter, DisposableBean {

    private static final long serialVersionUID = 4255636253714476918L;

    private static final EnumSet<CRUD> COLLECTION_EDITOR_CRUD = EnumSet.of(CRUD.UPDATE, CRUD.DELETE);

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

    private ICdmCacher cache;

    @Autowired
    protected CdmFilterablePagingProviderFactory pagingProviderFactory;

    SpecimenTypeDesignationWorkingSetDTO<Registration> workingSetDto;

    private Map<CollectionPopupEditor, SpecimenTypeDesignationDTORow> collectionPopupEditorsRowMap = new HashMap<>();

    private Map<ReferencePopupEditor, SpecimenTypeDesignationDTORow> referencePopupEditorsRowMap = new HashMap<>();

    private Set<CollectionRowItemCollection> popuEditorTypeDesignationSourceRows = new HashSet<>();

    private java.util.Collection<CdmBase> rootEntities = new HashSet<>();

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

        cache = new CdmTransientEntityCacher(this);
        if(identifier != null){

            TypeDesignationWorkingsetEditorIdSet idset = (TypeDesignationWorkingsetEditorIdSet)identifier;

            if(idset.baseEntityRef != null){
                workingSetDto = specimenTypeDesignationWorkingSetService.load(idset.registrationUuid, idset.baseEntityRef);
                if(workingSetDto.getFieldUnit() == null){
                    workingSetDto = specimenTypeDesignationWorkingSetService.fixMissingFieldUnit(workingSetDto);
                        // FIXME open Dialog to warn user about adding an empty fieldUnit to the typeDesignations
                        //       This method must go again into the presenter !!!!
                        logger.info("Basing all typeDesignations on a new fieldUnit");
                }
                // need to use load but put see #7214
                cache.load(workingSetDto.getOwner());
                rootEntities.add(workingSetDto.getOwner());
            } else {
                // create a new workingset, for a new fieldunit which is the base for the workingset
                workingSetDto = specimenTypeDesignationWorkingSetService.create(idset.registrationUuid, idset.publicationUuid, idset.typifiedNameUuid);
                // need to use load but put see #7214
                cache.load(workingSetDto.getOwner());
                cache.load(workingSetDto.getTypifiedName());
                rootEntities.add(workingSetDto.getOwner());
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

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> termOrPersonPagingProvider = new CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase>(getRepo().getAgentService(), TeamOrPersonBase.class);
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = new CdmFilterablePagingProvider<AgentBase, Person>(getRepo().getAgentService(), Person.class);
        termOrPersonPagingProvider.setInitStrategy(AgentBaseInit.TEAM_OR_PERSON_INIT_STRATEGY);
        getView().getCollectorField().setFilterablePersonPagingProvider(personPagingProvider, this);
        getView().getCollectorField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);

        getView().getTypeDesignationsCollectionField().addElementRemovedListener(e -> deleteTypeDesignation(e.getElement()));
        getView().getTypeDesignationsCollectionField().addElementAddedListener(e -> addTypeDesignation(e.getElement()));


        popuEditorTypeDesignationSourceRows.clear();
        CdmFilterablePagingProvider<Collection, Collection> collectionPagingProvider = new CdmFilterablePagingProvider<Collection, Collection>(getRepo().getCollectionService());
        collectionPagingProvider.getRestrictions().add(new Restriction<>("institute.titleCache", Operator.OR, MatchMode.ANYWHERE, CdmFilterablePagingProvider.QUERY_STRING_PLACEHOLDER));

        CdmFilterablePagingProvider<Reference, Reference> referencePagingProvider = pagingProviderFactory.referencePagingProvider();

        getView().getTypeDesignationsCollectionField().setEditorInstantiator(new AbstractElementCollection.Instantiator<SpecimenTypeDesignationDTORow>() {

            @Override
            public SpecimenTypeDesignationDTORow create() {

                SpecimenTypeDesignationDTORow row = new SpecimenTypeDesignationDTORow();

                row.kindOfUnit.setContainerDataSource(selectFactory.buildTermItemContainer(
                        RegistrationTermLists.KIND_OF_UNIT_TERM_UUIDS())
                        );
                row.kindOfUnit.setNullSelectionAllowed(false);

                row.typeStatus.setContainerDataSource(selectFactory.buildTermItemContainer(
                        RegistrationTermLists.SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS())
                        );
                row.typeStatus.setNullSelectionAllowed(false);


                row.collection.loadFrom(
                        collectionPagingProvider,
                        collectionPagingProvider,
                        collectionPagingProvider.getPageSize()
                        );
                row.collection.getSelect().setCaptionGenerator(new CollectionCaptionGenerator());
                row.collection.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Collection>(row.collection));
                row.collection.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Collection>(row.collection.getSelect(),
                        SpecimenTypeDesignationWorkingsetEditorPresenter.this));
                row.collection.addClickListenerAddEntity(e -> doCollectionEditorAdd(row));
                row.collection.addClickListenerEditEntity(e -> {
                        if(row.collection.getValue() != null){
                            doCollectionEditorEdit(row);
                        }
                    });

                row.mediaSpecimenReference.loadFrom(
                        referencePagingProvider,
                        referencePagingProvider,
                        collectionPagingProvider.getPageSize()
                        );

                row.mediaSpecimenReference.getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
                row.mediaSpecimenReference.getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(row.mediaSpecimenReference));
                row.mediaSpecimenReference.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Reference>(row.mediaSpecimenReference.getSelect(),
                        SpecimenTypeDesignationWorkingsetEditorPresenter.this));
                row.mediaSpecimenReference.addClickListenerAddEntity(e -> doReferenceEditorAdd(row));
                row.mediaSpecimenReference.addClickListenerEditEntity(e -> {
                    if(row.mediaSpecimenReference.getValue() != null){
                        doReferenceEditorEdit(row);
                    }
                });

                getView().applyDefaultComponentStyle(row.components());

                popuEditorTypeDesignationSourceRows.add(row);

                return row;
            }

        });

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(SpecimenTypeDesignationWorkingSetDTO dto) {

        if(crud != null){
            UserHelperAccess.userHelper().createAuthorityForCurrentUser(dto.getFieldUnit(), crud, null);
        }

        specimenTypeDesignationWorkingSetService.save(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deleteBean(SpecimenTypeDesignationWorkingSetDTO bean) {
        specimenTypeDesignationWorkingSetService.delete(bean, true);
    }

    /**
     * @param element
     * @return
     */
    private void addTypeDesignation(SpecimenTypeDesignationDTO element) {
        getView().updateAllowDelete();
    }


    /**
     * In this method the SpecimenTypeDesignation is dissociated from the Registration.
     * The actual deletion of the SpecimenTypeDesignation and DerivedUnit will take place in {@link #saveBean(SpecimenTypeDesignationWorkingSetDTO)}
     *
     * TODO once https://dev.e-taxonomy.eu/redmine/issues/7077 is fixed dissociating from the Registration could be removed here
     *
     * @param e
     * @return
     */
    private void deleteTypeDesignation(SpecimenTypeDesignationDTO element) {

        Registration reg = workingSetDto.getOwner();
        SpecimenTypeDesignation std = element.asSpecimenTypeDesignation();

        reg.getTypeDesignations().remove(std);

        getView().updateAllowDelete();
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
    public ICdmCacher getCache() {
        return cache;
    }

    @Override
    public void disposeCache() {
        cache.dispose();
    }

    public void doCollectionEditorAdd(SpecimenTypeDesignationDTORow row) {

        CollectionPopupEditor collectionPopupEditor = openPopupEditor(CollectionPopupEditor.class, null);

        collectionPopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
        collectionPopupEditor.withDeleteButton(true);
        collectionPopupEditor.loadInEditor(null);

        collectionPopupEditorsRowMap.put(collectionPopupEditor, row);
    }

    public void doCollectionEditorEdit(SpecimenTypeDesignationDTORow row) {

        CollectionPopupEditor collectionPopupEditor = openPopupEditor(CollectionPopupEditor.class, null);

        collectionPopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
        collectionPopupEditor.withDeleteButton(true);
        collectionPopupEditor.loadInEditor(row.collection.getValue().getUuid());

        collectionPopupEditorsRowMap.put(collectionPopupEditor, row);
    }


    @EventBusListenerMethod(filter = EntityChangeEventFilter.OccurrenceCollectionFilter.class)
    public void onCollectionEvent(EntityChangeEvent event){

        if(!isFromOwnView(event)){
            return;
        }

        Collection newCollection = getRepo().getCollectionService().load(
                event.getEntityUuid(), Arrays.asList(new String[]{"$.institute"})
                );
        cache.load(newCollection);

        if(event.isCreatedType()){
            SpecimenTypeDesignationDTORow row = collectionPopupEditorsRowMap.get(event.getSourceView());
            ToOneRelatedEntityCombobox<Collection> combobox = row.getComponent(ToOneRelatedEntityCombobox.class, 3);
            combobox.setValue((Collection) event.getEntity());
        }
        for( CollectionRowItemCollection row : popuEditorTypeDesignationSourceRows) {
            ToOneRelatedEntityCombobox<Collection> combobox = row.getComponent(ToOneRelatedEntityCombobox.class, 3);
            combobox.reload();
        }
    }

    public void doReferenceEditorAdd(SpecimenTypeDesignationDTORow row) {

        ReferencePopupEditor referencePopupEditor = openPopupEditor(ReferencePopupEditor.class, null);

        referencePopupEditor.withReferenceTypes(RegistrationUIDefaults.MEDIA_REFERENCE_TYPES);
        referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
        referencePopupEditor.withDeleteButton(true);
        referencePopupEditor.loadInEditor(null);

        referencePopupEditorsRowMap.put(referencePopupEditor, row);
    }

    public void doReferenceEditorEdit(SpecimenTypeDesignationDTORow row) {

        ReferencePopupEditor referencePopupEditor = openPopupEditor(ReferencePopupEditor.class, null);
        referencePopupEditor.withReferenceTypes(RegistrationUIDefaults.MEDIA_REFERENCE_TYPES);
        referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
        referencePopupEditor.withDeleteButton(true);
        referencePopupEditor.loadInEditor(row.mediaSpecimenReference.getValue().getUuid());

        referencePopupEditorsRowMap.put(referencePopupEditor, row);
    }

    @EventBusListenerMethod(filter = EntityChangeEventFilter.ReferenceFilter.class)
    public void onReferenceEvent(EntityChangeEvent event){

        Reference newRef = getRepo().getReferenceService().load(event.getEntityUuid(), Arrays.asList(new String[]{"$"}));
        cache.load(newRef);

        if(event.isCreatedType()){
            SpecimenTypeDesignationDTORow row = referencePopupEditorsRowMap.get(event.getSourceView());
            ToOneRelatedEntityCombobox<Reference> combobox = row.getComponent(ToOneRelatedEntityCombobox.class, 7);
            combobox.setValue((Reference) event.getEntity());

        } else {
            for( CollectionRowItemCollection row : popuEditorTypeDesignationSourceRows) {
                ToOneRelatedEntityCombobox<Reference> combobox = row.getComponent(ToOneRelatedEntityCombobox.class, 7);
                combobox.reload();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRootEntity(CdmBase entity) {
        rootEntities.add(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public java.util.Collection<CdmBase> getRootEntities() {
        return rootEntities ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        super.destroy();
        cache.dispose();
    }

}
