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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.AbstractElementCollection;
import org.vaadin.viritin.fields.ElementCollectionField;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.cache.CdmTransientEntityWithUuidCacher;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.location.Country;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.occurrence.FieldUnit;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.NamedSource;
import eu.etaxonomy.cdm.model.reference.NamedSourceBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.model.term.DefinedTermBase;
import eu.etaxonomy.cdm.model.term.TermType;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.service.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.ISpecimenTypeDesignationSetService;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.service.initstrategies.AgentBaseInit;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowItemCollection;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEventFilter;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationSetDTO;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CollectionCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.ReferenceEllypsisCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.fields.ElementCollectionHelper;
import eu.etaxonomy.cdm.vaadin.view.occurrence.CollectionPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.reference.RegistrationUiReferenceEditorFormConfigurator;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
/**
 * SpecimenTypeDesignationSetPopupEditorView implementation must override the showInEditor() method,
 * see {@link #prepareAsFieldGroupDataSource()} for details.
 *
 * @author a.kohlbecker
 * @since Jun 13, 2017
 */
@SpringComponent
@Scope("prototype")
public class SpecimenTypeDesignationSetEditorPresenter
        extends AbstractEditorPresenter<SpecimenTypeDesignationSetDTO,SpecimenTypeDesignationSetEditorPresenter,SpecimenTypeDesignationSetPopupEditorView>
        implements CachingPresenter, NomenclaturalActContext {

    private static final long serialVersionUID = 4255636253714476918L;

    private static final Logger logger = LogManager.getLogger();

    private static final EnumSet<CRUD> COLLECTION_EDITOR_CRUD = EnumSet.of(CRUD.UPDATE, CRUD.DELETE);

    /**
     * This object for this field will either be injected by the {@link PopupEditorFactory} or by a Spring
     * {@link BeanFactory}
     */
    @Autowired
    private ISpecimenTypeDesignationSetService specimenTypeDesignationSetService;

    @Autowired
    private CdmFilterablePagingProviderFactory pagingProviderFactory;

    @Autowired
    private CdmBeanItemContainerFactory cdmBeanItemContainerFactory;

    /**
     * if not null, this CRUD set is to be used to create a CdmAuthoritiy for the base entitiy which will be
     * granted to the current use as long this grant is not assigned yet.
     */
    private EnumSet<CRUD> crud = null;

    private ICdmEntityUuidCacher cache;

    private SpecimenTypeDesignationSetDTO<Registration> workingSetDto;

    private CdmFilterablePagingProvider<Reference, Reference> designationReferencePagingProvider;

    private CdmFilterablePagingProvider<Reference, Reference> mediaReferencePagingProvider;

    /**
     * The unit of publication in which the type designation has been published.
     * This may be any type listed in {@link RegistrationUIDefaults#NOMECLATURAL_PUBLICATION_UNIT_TYPES}
     * but never a {@link ReferenceType#Section}
     */
    private NamedSourceBase publishedUnit;

    private Map<CollectionPopupEditor, SpecimenTypeDesignationDTORow> collectionPopupEditorsRowMap = new HashMap<>();

    private Map<ReferencePopupEditor, ToOneRelatedEntityCombobox<Reference>> referencePopupEditorsCombobox = new HashMap<>();

    private ElementCollectionHelper<ElementCollectionField<?>> typeDesignationsCollectionFieldHelper;

    private Set<CollectionRowItemCollection> typeDesignationEditorRows = new HashSet<>();

    private java.util.Collection<CdmBase> rootEntities = new HashSet<>();

    private BeanInstantiator<Reference> newReferenceInstantiator;

    private Optional<Boolean> isInTypedesignationOnlyAct = Optional.empty();

    /**
     * Loads an existing working set from the database. This process actually involves
     * loading the Registration specified by the <code>RegistrationAndWorkingsetId.registrationId</code> and in
     * a second step to find the workingset by the <code>registrationAndWorkingsetId.workingsetId</code>.
     * <p>
     * The <code>identifier</code> must be of the type {@link TypeDesignationSetIds} whereas the
     * field <code>registrationId</code> must be present.
     * The field <code>workingsetId</code> however can be null.
     * I this case a new workingset with a new {@link FieldUnit} as
     * base entity is being created.
     *
     * @param identifier a {@link TypeDesignationSetIds}
     */
    @Override
    protected SpecimenTypeDesignationSetDTO<Registration> loadBeanById(Object identifier) {

        cache = new CdmTransientEntityWithUuidCacher(this);
        if(identifier != null){

            SpecimenTypeDesignationSetIds idset = (SpecimenTypeDesignationSetIds)identifier;

            if(idset.baseEntity != null){
                // load existing workingset
                workingSetDto = specimenTypeDesignationSetService.load(idset.registrationUuid, idset.baseEntity);
                if(workingSetDto.getFieldUnit() == null){
                    workingSetDto = specimenTypeDesignationSetService.fixMissingFieldUnit(workingSetDto);
                        // FIXME open Dialog to warn user about adding an empty fieldUnit to the typeDesignations
                        //       This method must go again into the presenter !!!!
                        logger.info("Basing all typeDesignations on a new fieldUnit");
                }
            } else {
                // create a new workingset, for a new fieldunit which is the base for the workingset
                workingSetDto = specimenTypeDesignationSetService.create(idset.getRegistrationUUID(), idset.getTypifiedNameUuid());
                cache.load(workingSetDto.getTypifiedName());
                rootEntities.add(workingSetDto.getTypifiedName());
            }
            Registration registration = workingSetDto.getOwner();
            // need to use load() but put() see #7214
            cache.load(registration);
            rootEntities.add(registration);
            setInTypedesignationOnlyAct(Optional.of(Boolean.valueOf(registration.getName() == null)));
            try {
                NamedSourceBase citedSource = registration.findCitedSource();
                if(citedSource == null) {
                    Reference reference = getRepo().getReferenceService().load(idset.getPublishedUnitUuid());
                    citedSource = NamedSource.NewPrimarySourceInstance(reference, null);
                }
                setPublishedUnit(citedSource);
            } catch (Exception e) {
                // FIXME report error state instead
                logger.error("Error on finding published unit in " + registration.toString(), e);
            }
        } else {
            workingSetDto = null;
        }

        if (getPublishedUnit() != null) {
            // reduce available references to those which are sections of
            // the publicationUnit and the publishedUnit itself
            designationReferencePagingProvider.getCriteria()
                    .add(Restrictions.or(
                            Restrictions.and(
                                    Restrictions.eq("inReference", publishedUnit.getCitation()),
                                    Restrictions.eq("type", ReferenceType.Section)),
                            Restrictions.idEq(publishedUnit.getCitation().getId()))
                         );
            // new Reference only a sub sections of the publishedUnit
            newReferenceInstantiator = new BeanInstantiator<Reference>() {
                @Override
                public Reference createNewBean() {
                    Reference newRef = ReferenceFactory.newSection();
                    newRef.setInReference(publishedUnit.getCitation());
                    return newRef;
                }
            };
        }

        // new Reference only a sub sections of the publishedUnit
        newReferenceInstantiator = new BeanInstantiator<Reference>() {
            @Override
            public Reference createNewBean() {
                Reference newRef = ReferenceFactory.newSection();
                newRef.setInReference(publishedUnit.getCitation());
                return newRef;
            }
        };

        return workingSetDto;
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("serial")
    @Override
    public void handleViewEntered() {

        getView().getCountrySelectField().setContainerDataSource(cdmBeanItemContainerFactory.buildVocabularyTermsItemContainer(Country.uuidCountryVocabulary));

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> termOrPersonPagingProvider = pagingProviderFactory.teamOrPersonPagingProvider();
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = pagingProviderFactory.personPagingProvider();
        termOrPersonPagingProvider.setInitStrategy(AgentBaseInit.TEAM_OR_PERSON_INIT_STRATEGY);
        // the ToOneRelatedEntityReloader is added internally in the TeamOrPersonField:
        getView().getCollectorField().setFilterablePersonPagingProvider(personPagingProvider, this);
        getView().getCollectorField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);

        getView().getExactLocationField().getReferenceSystemSelect().setContainerDataSource(cdmBeanItemContainerFactory.buildTermItemContainer(TermType.ReferenceSystem));
        getView().getExactLocationField().getReferenceSystemSelect().setItemCaptionPropertyId("label");

        getView().getTypeDesignationsCollectionField().addElementRemovedListener(e -> deleteTypeDesignation(e.getElement()));
        getView().getTypeDesignationsCollectionField().addElementAddedListener(e -> addTypeDesignation(e.getElement()));

        getView().getAnnotationsField().setAnnotationTypeItemContainer(cdmBeanItemContainerFactory.buildVocabularyTermsItemContainer(
                AnnotationType.EDITORIAL().getVocabulary().getUuid()));

        typeDesignationEditorRows.clear();
        CdmFilterablePagingProvider<Collection, Collection> collectionPagingProvider = new CdmFilterablePagingProvider<Collection, Collection>(getRepo().getCollectionService());
        collectionPagingProvider.getRestrictions().add(new Restriction<>("institute.titleCache", Operator.OR, MatchMode.ANYWHERE, CdmFilterablePagingProvider.QUERY_STRING_PLACEHOLDER));

        designationReferencePagingProvider = pagingProviderFactory.referencePagingProvider();
        mediaReferencePagingProvider = pagingProviderFactory.referencePagingProvider();

        typeDesignationsCollectionFieldHelper = new ElementCollectionHelper(getView().getTypeDesignationsCollectionField());
        getView().getTypeDesignationsCollectionField().setEditorInstantiator(new AbstractElementCollection.Instantiator<SpecimenTypeDesignationDTORow>() {

            @Override
            public SpecimenTypeDesignationDTORow create() {

                SpecimenTypeDesignationDTORow row = new SpecimenTypeDesignationDTORow();

                row.kindOfUnit.setContainerDataSource(cdmBeanItemContainerFactory.buildTermItemContainer(
                        RegistrationTermLists.KIND_OF_UNIT_TERM_UUIDS())
                        );
                row.kindOfUnit.setNullSelectionAllowed(false);

                row.typeStatus.setContainerDataSource(provideTypeStatusTermItemContainer());
                row.typeStatus.setNullSelectionAllowed(false);


                row.collection.loadFrom(
                        collectionPagingProvider,
                        collectionPagingProvider,
                        collectionPagingProvider.getPageSize()
                        );
                row.collection.getSelect().setCaptionGenerator(new CollectionCaptionGenerator());
                row.collection.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Collection>(row.collection.getSelect(),
                        SpecimenTypeDesignationSetEditorPresenter.this));
                row.collection.addClickListenerAddEntity(e -> doCollectionEditorAdd(row));
                row.collection.addClickListenerEditEntity(e -> {
                        if(row.collection.getValue() != null){
                            doCollectionEditorEdit(row);
                        }
                    });

                row.designationReference.loadFrom(
                        designationReferencePagingProvider,
                        designationReferencePagingProvider,
                        designationReferencePagingProvider.getPageSize()
                        );
                row.designationReference.getSelect().setCaptionGenerator(new ReferenceEllypsisCaptionGenerator(LabelType.BIBLIOGRAPHIC, row.designationReference.getSelect()));
                row.designationReference.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Reference>(row.designationReference.getSelect(),
                        SpecimenTypeDesignationSetEditorPresenter.this));
                row.designationReference.addClickListenerAddEntity(e -> doReferenceEditorAdd(row.designationReference));
                row.designationReference.addClickListenerEditEntity(e -> {
                    if(row.designationReference.getValue() != null){
                        doReferenceEditorEdit(row.designationReference);
                    }
                });
                row.designationReference.setRequired(checkInTypeDesignationOnlyAct());
                row.designationReference.getSelect().setNullSelectionAllowed(!checkInTypeDesignationOnlyAct());

                row.mediaSpecimenReference.loadFrom(
                        mediaReferencePagingProvider,
                        mediaReferencePagingProvider,
                        mediaReferencePagingProvider.getPageSize()
                        );
                row.mediaSpecimenReference.getSelect().setCaptionGenerator(new ReferenceEllypsisCaptionGenerator(LabelType.BIBLIOGRAPHIC, row.mediaSpecimenReference.getSelect()));
                row.mediaSpecimenReference.getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Reference>(row.mediaSpecimenReference.getSelect(),
                        SpecimenTypeDesignationSetEditorPresenter.this));
                row.mediaSpecimenReference.addClickListenerAddEntity(e -> doReferenceEditorAdd(row.mediaSpecimenReference));
                row.mediaSpecimenReference.addClickListenerEditEntity(e -> {
                    if(row.mediaSpecimenReference.getValue() != null){
                        doReferenceEditorEdit(row.mediaSpecimenReference);
                    }
                });

                getView().applyDefaultComponentStyle(row.components());

                typeDesignationEditorRows.add(row);

                return row;
            }
        });
    }

    protected BeanItemContainer<DefinedTermBase> provideTypeStatusTermItemContainer() {
        BeanItemContainer<DefinedTermBase> container = cdmBeanItemContainerFactory.buildTermItemContainer(
                RegistrationTermLists.SPECIMEN_TYPE_DESIGNATION_STATUS_UUIDS());
        List<TypeDesignationStatusBase> filteredItems = container.getItemIds().stream()
                .filter(t -> t instanceof SpecimenTypeDesignationStatus)
                .map(t -> (SpecimenTypeDesignationStatus)t)
                .filter(tsb ->
                    !checkInTypeDesignationOnlyAct()
                    || tsb.hasDesignationSource() == true
                )
                .collect(Collectors.toList());
        container.removeAllItems();
        container.addAll(filteredItems);
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(SpecimenTypeDesignationSetDTO dto) {

        if(crud != null){
            UserHelperAccess.userHelper().createAuthorityForCurrentUser(dto.getFieldUnit(), crud, null);
        }

        List<SpecimenTypeDesignationDTO> stdDTOs = dto.getSpecimenTypeDesignationDTOs();
        for(SpecimenTypeDesignationDTO stddto : stdDTOs) {
            // clean up
            if(!stddto.getTypeStatus().hasDesignationSource()) {
                stddto.setDesignationReference(null);
                stddto.setDesignationReferenceDetail(null);
            }
        }

        specimenTypeDesignationSetService.save(dto);
    }

    @Override
    protected void deleteBean(SpecimenTypeDesignationSetDTO bean) {
        specimenTypeDesignationSetService.delete(bean, true);
    }

    private void addTypeDesignation(SpecimenTypeDesignationDTO element) {
        getView().updateAllowDeleteTypeDesignation();
    }


    /**
     * In this method the SpecimenTypeDesignation is dissociated from the Registration.
     * The actual deletion of the SpecimenTypeDesignation and DerivedUnit will take place in {@link #saveBean(SpecimenTypeDesignationSetDTO)}
     *
     * TODO once https://dev.e-taxonomy.eu/redmine/issues/7077 is fixed dissociating from the Registration could be removed here
     */
    private void deleteTypeDesignation(SpecimenTypeDesignationDTO element) {

        Registration reg = workingSetDto.getOwner();
        SpecimenTypeDesignation std = element.asSpecimenTypeDesignation();

        reg.getTypeDesignations().remove(std);

        getView().updateAllowDeleteTypeDesignation();
    }

    public void setGrantsForCurrentUser(EnumSet<CRUD> crud) {
        this.crud = crud;
    }

    @Override
    public ICdmEntityUuidCacher getCache() {
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
    public void onCollectionEvent(EntityChangeEvent<?> event){

        if(event.getSourceView() instanceof AbstractPopupEditor) {

            Stack<EditorActionContext> context = ((AbstractPopupEditor) event.getSourceView()).getEditorActionContext();
            if(context.size() > 1){
               AbstractView<?,?> parentView = context.get(context.size() - 2).getParentView();
               if(getView().equals(parentView)){
                   Collection newCollection = getRepo().getCollectionService().load(
                           event.getEntityUuid(), Arrays.asList(new String[]{"$.institute"})
                           );
                   cache.load(newCollection);

                   if(event.isCreatedType()){
                       //TODO use typeDesignationsCollectionFieldHelper instead to get component
                       SpecimenTypeDesignationDTORow row = collectionPopupEditorsRowMap.get(event.getSourceView());
                       ToOneRelatedEntityCombobox<Collection> combobox = row.getComponent(ToOneRelatedEntityCombobox.class, SpecimenTypeDesignationDTORow.FIELD_INDEX_COLLECTION);
                       combobox.setValue((Collection) event.getEntity());
                   } else {
                       //TODO use typeDesignationsCollectionFieldHelper instead to get component
                       for( CollectionRowItemCollection row : typeDesignationEditorRows) {
                           ToOneRelatedEntityCombobox<Collection> combobox = row.getComponent(ToOneRelatedEntityCombobox.class,  SpecimenTypeDesignationDTORow.FIELD_INDEX_COLLECTION);
                           combobox.reload();
                       }
                   }
               }
            }
        }
    }

    public void doReferenceEditorAdd(ToOneRelatedEntityCombobox<Reference> referenceComobox) {

        ReferencePopupEditor referencePopupEditor = openPopupEditor(ReferencePopupEditor.class, null);

        String property = typeDesignationsCollectionFieldHelper.properyFor(referenceComobox);
        if(property.equals(SpecimenTypeDesignationDTORow.FIELD_NAME_DESIGNATION_REFERENCE)){
            referencePopupEditor.withReferenceTypes(EnumSet.of(ReferenceType.Section));
            referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
            referencePopupEditor.withDeleteButton(true);
            RegistrationUiReferenceEditorFormConfigurator
                .create(true).configure(referencePopupEditor, newReferenceInstantiator);
            referencePopupEditor.loadInEditor(null);
        } else {
            // only other option by now
            referencePopupEditor.withReferenceTypes(RegistrationUIDefaults.MEDIA_REFERENCE_TYPES);
            referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
            referencePopupEditor.withDeleteButton(true);
            referencePopupEditor.loadInEditor(null);
        }

        referencePopupEditorsCombobox.put(referencePopupEditor, referenceComobox);
    }

    public void doReferenceEditorEdit(ToOneRelatedEntityCombobox<Reference> referenceComobox) {

        ReferencePopupEditor referencePopupEditor = openPopupEditor(ReferencePopupEditor.class, null);

        String property = typeDesignationsCollectionFieldHelper.properyFor(referenceComobox);

        if(property.equals(SpecimenTypeDesignationDTORow.FIELD_NAME_DESIGNATION_REFERENCE)){
            referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
            referencePopupEditor.withDeleteButton(true);
            RegistrationUiReferenceEditorFormConfigurator
                .create(false).typeSelectReadonly(true).configure(referencePopupEditor, newReferenceInstantiator);
            referencePopupEditor.loadInEditor(referenceComobox.getValue().getUuid());
        } else {
            // only other option by now
            referencePopupEditor.withReferenceTypes(RegistrationUIDefaults.MEDIA_REFERENCE_TYPES);
            referencePopupEditor.grantToCurrentUser(COLLECTION_EDITOR_CRUD);
            referencePopupEditor.withDeleteButton(true);
            referencePopupEditor.loadInEditor(referenceComobox.getValue().getUuid());
        }

        referencePopupEditorsCombobox.put(referencePopupEditor, referenceComobox);
    }



    @EventBusListenerMethod(filter = EntityChangeEventFilter.ReferenceFilter.class)
    public void onReferenceEvent(EntityChangeEvent event){

        Reference newRef = getRepo().getReferenceService().load(event.getEntityUuid(), Arrays.asList(new String[]{"$"}));
        cache.load(newRef);

        ToOneRelatedEntityCombobox<Reference> combobox = referencePopupEditorsCombobox.get(event.getSourceView());
        if(event.isCreatedType()){
            combobox.setValue((Reference) event.getEntity());
        } else {
            for( CollectionRowItemCollection row : typeDesignationEditorRows) {
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

    @Override
    public void destroy() throws Exception {
        super.destroy();
        cache.dispose();
    }

    /**
     * @return
     *  the {@link #publishedUnit}
     */
    public NamedSourceBase getPublishedUnit() {
        return publishedUnit;
    }

    /**
     * @param publishedUnit
     *  The unit of publication in which the type designation has been published.
     *  This may be any type listed in {@link RegistrationUIDefaults#NOMECLATURAL_PUBLICATION_UNIT_TYPES}
     */
    protected void setPublishedUnit(NamedSourceBase publishedUnit) throws Exception {
        if(publishedUnit == null) {
            throw new NullPointerException();
        }
        if(publishedUnit.getCitation() == null) {
            throw new NullPointerException("The citation of the published unit must not be null.");
        }
        if(!RegistrationUIDefaults.NOMECLATURAL_PUBLICATION_UNIT_TYPES.contains(publishedUnit.getCitation().getType())) {
            throw new Exception("The referrence type '"  + publishedUnit.getType() + "'is not allowed for publishedUnit.");
        }
        this.publishedUnit = publishedUnit;
    }

    @Override
    public void setInTypedesignationOnlyAct(Optional<Boolean> isInTypedesignationOnlyAct) {
        this.isInTypedesignationOnlyAct = isInTypedesignationOnlyAct;
    }

    @Override
    public Optional<Boolean> isInTypedesignationOnlyAct() {
        return isInTypedesignationOnlyAct;
    }

}
