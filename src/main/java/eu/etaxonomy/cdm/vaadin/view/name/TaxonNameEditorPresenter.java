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
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.data.Property;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.TaxonNameStringFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.service.initstrategies.AgentBaseInit;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorActionStrRep;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.name.TaxonNameDTO;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.component.CompositeCustomField;
import eu.etaxonomy.vaadin.component.ReloadableLazyComboBox;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.component.WeaklyRelatedEntityCombobox;
import eu.etaxonomy.vaadin.component.WeaklyRelatedEntityField;
import eu.etaxonomy.vaadin.event.FieldReplaceEvent;
import eu.etaxonomy.vaadin.mvp.AbstractCdmDTOEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.mvp.BoundField;
import eu.etaxonomy.vaadin.ui.view.PopupView;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class TaxonNameEditorPresenter extends AbstractCdmDTOEditorPresenter<TaxonNameDTO, TaxonName, TaxonNamePopupEditorView> {


    private static final List<String> BASIONYM_INIT_STRATEGY = Arrays.asList(
            "$",
            "relationsFromThisName",
            "relationsToThisName.type",
            "homotypicalGroup.typifiedNames"
            );

    public static final List<String> REFERENCE_INIT_STRATEGY = Arrays.asList("authorship", "inReference.authorship", "inReference.inReference.authorship", "inReference.inReference.inReference.authorship");

    private static final long serialVersionUID = -3538980627079389221L;

    private static final Logger logger = Logger.getLogger(TaxonNameEditorPresenter.class);

    private CdmFilterablePagingProvider<Reference, Reference> nomReferencePagingProvider;

    private Reference publishedUnit;

    private BeanInstantiator<Reference> newReferenceInstantiator;

    private TaxonNameStringFilterablePagingProvider genusOrUninomialPartPagingProvider;

    private TaxonNameStringFilterablePagingProvider specificEpithetPartPagingProvider;

    private Property.ValueChangeListener refreshSpecificEpithetComboBoxListener;

    private CdmFilterablePagingProvider<TaxonName, TaxonName> relatedNamePagingProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        super.handleViewEntered();

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getRankSelect().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(TermType.Rank));
        getView().getRankSelect().setItemCaptionPropertyId("label");

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> termOrPersonPagingProvider = new CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase>(getRepo().getAgentService(), TeamOrPersonBase.class);
        termOrPersonPagingProvider.setInitStrategy(AgentBaseInit.TEAM_OR_PERSON_INIT_STRATEGY);
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = new CdmFilterablePagingProvider<AgentBase, Person>(getRepo().getAgentService(), Person.class);

        getView().getCombinationAuthorshipField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);
        getView().getCombinationAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        getView().getExCombinationAuthorshipField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);
        getView().getExCombinationAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        getView().getBasionymAuthorshipField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);
        getView().getBasionymAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        getView().getExBasionymAuthorshipField().setFilterableTeamPagingProvider(termOrPersonPagingProvider, this);
        getView().getExBasionymAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);

        getView().getNomReferenceCombobox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        nomReferencePagingProvider = pagingProviderFactory.referencePagingProvider();
        nomReferencePagingProvider.setInitStrategy(REFERENCE_INIT_STRATEGY);
        getView().getNomReferenceCombobox().loadFrom(nomReferencePagingProvider, nomReferencePagingProvider, nomReferencePagingProvider.getPageSize());
        getView().getNomReferenceCombobox().setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getNomReferenceCombobox()));
        getView().getNomReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getNomReferenceCombobox(), this));

        getView().getBasionymComboboxSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());

        relatedNamePagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());
        relatedNamePagingProvider.setInitStrategy(BASIONYM_INIT_STRATEGY);
        getView().getBasionymComboboxSelect().setPagingProviders(relatedNamePagingProvider, relatedNamePagingProvider, relatedNamePagingProvider.getPageSize(), this);

        getView().getReplacedSynonymsComboboxSelect().setCaptionGenerator( new CdmTitleCacheCaptionGenerator<TaxonName>());
        // reusing the basionymPagingProvider for the replaced synonyms to benefit from caching
        getView().getReplacedSynonymsComboboxSelect().setPagingProviders(relatedNamePagingProvider, relatedNamePagingProvider, relatedNamePagingProvider.getPageSize(), this);

        getView().getValidationField().getValidatedNameComboBox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());
        // reusing the basionymPagingProvider for the replaced synonyms to benefit from caching
        getView().getValidationField().getValidatedNameComboBox().loadFrom(relatedNamePagingProvider, relatedNamePagingProvider, relatedNamePagingProvider.getPageSize());
        getView().getValidationField().getValidatedNameComboBox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getValidationField().getValidatedNameComboBox(), this));

        getView().getNomReferenceCombobox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        CdmFilterablePagingProvider<Reference, Reference> icbnCodesPagingProvider = pagingProviderFactory.referencePagingProvider();
        icbnCodesPagingProvider.setInitStrategy(REFERENCE_INIT_STRATEGY);
        // @formatter:off
        // TODO use markers on references instead of isbn. The marker type MarkerType.NOMENCLATURAL_RELEVANT() has already prepared (#7466)
        icbnCodesPagingProvider.getCriteria().add(Restrictions.in("isbn", new String[]{
                "3-904144-22-7",     // Saint Louis Code
                "3-906166-48-1",     // Vienna Code
                "978-3-87429-425-6", // Melbourne Code
                "978-3-946583-16-5", // Shenzhen Code
                "0-85301-006-4"      // ICZN 1999
                                     // ICNB
                }));
        // @formatter:on
        getView().getValidationField().getCitatonComboBox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        getView().getValidationField().getCitatonComboBox().loadFrom(icbnCodesPagingProvider, icbnCodesPagingProvider, icbnCodesPagingProvider.getPageSize());
        getView().getValidationField().getCitatonComboBox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getValidationField().getCitatonComboBox(), this));

        getView().getAnnotationsField().setAnnotationTypeItemContainer(selectFieldFactory.buildTermItemContainer(
                AnnotationType.EDITORIAL().getUuid(), AnnotationType.TECHNICAL().getUuid()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaxonName loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(
                "$",
                "annotations.type",
                "annotations.*", // needed as log as we are using a table in FilterableAnnotationsField
                "rank.vocabulary", // needed for comparing ranks

                "nomenclaturalReference.authorship",
                "nomenclaturalReference.inReference.authorship",
                "nomenclaturalReference.inReference.inReference.authorship",
                "nomenclaturalReference.inReference.inReference.inReference.authorship",

                "status.type",

                "combinationAuthorship",
                "exCombinationAuthorship",
                "basionymAuthorship",
                "exBasionymAuthorship",

                // basionyms: relationsToThisName.fromName
                "relationsToThisName.type",
                "relationsToThisName.fromName.rank",
                "relationsToThisName.fromName.combinationAuthorship",
                "relationsToThisName.fromName.exCombinationAuthorship",
                "relationsToThisName.fromName.nomenclaturalReference.authorship",
                "relationsToThisName.fromName.nomenclaturalReference.inReference.authorship",
                "relationsToThisName.fromName.nomenclaturalReference.inReference.inReference.inReference.authorship",
                "relationsToThisName.fromName.relationsToThisName",
                "relationsToThisName.fromName.relationsFromThisName",
                "relationsToThisName.citation",

                "relationsFromThisName",
                "homotypicalGroup.typifiedNames"
        );

        TaxonName taxonName;
        if(identifier != null){
            taxonName = getRepo().getNameService().load(identifier, initStrategy);
        } else {
            taxonName = createCdmEntity();
        }

        if(getView().isModeEnabled(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY)){
            Reference nomRef = taxonName.getNomenclaturalReference();

            //getView().getNomReferenceCombobox().setEnabled(nomRef.isOfType(ReferenceType.Section));
            publishedUnit = nomRef;
            while(publishedUnit.isOfType(ReferenceType.Section) && publishedUnit.getInReference() != null){
                publishedUnit = nomRef.getInReference();
            }
            // reduce available references to those which are sections of the publishedUnit and the publishedUnit itself
            // nomReferencePagingProvider
            nomReferencePagingProvider.getCriteria().add(Restrictions.or(
                    Restrictions.and(Restrictions.eq("inReference", publishedUnit), Restrictions.eq("type", ReferenceType.Section)),
                    Restrictions.idEq(publishedUnit.getId())
                    )
            );
            // and remove the empty option
            getView().getNomReferenceCombobox().getSelect().setNullSelectionAllowed(false);

            // new Reference only a sub sections of the publishedUnit
            newReferenceInstantiator = new BeanInstantiator<Reference>() {
                @Override
                public Reference createNewBean() {
                    Reference newRef = ReferenceFactory.newSection();
                    newRef.setInReference(publishedUnit);
                    return newRef;
                }
            };

        }

        relatedNamePagingProvider.addRestriction(new Restriction<>("id", Operator.AND_NOT, null, Integer.valueOf(taxonName.getId())));

        return taxonName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(TaxonName.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(TaxonName bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected INameService getService() {
        return getRepo().getNameService();
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        ReferencePopupEditor referenceEditorPopup = openPopupEditor(ReferencePopupEditor.class, event);

        referenceEditorPopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        referenceEditorPopup.withDeleteButton(true);
        referenceEditorPopup.setBeanInstantiator(newReferenceInstantiator);
        referenceEditorPopup.loadInEditor(null);
        if(newReferenceInstantiator != null){
            // this is a bit clumsy, we actually need to inject something like a view configurer
            // which can enable, disable fields
            referenceEditorPopup.getInReferenceCombobox().setEnabled(false);
            referenceEditorPopup.getTypeSelect().setEnabled(false);
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {


        if(!isFromOwnView(event)){
            return;
        }
        ReferencePopupEditor referenceEditorPopup = openPopupEditor(ReferencePopupEditor.class, event);

        referenceEditorPopup.withDeleteButton(true);
        referenceEditorPopup.setBeanInstantiator(newReferenceInstantiator);
        referenceEditorPopup.loadInEditor(event.getEntityUuid());
        if(newReferenceInstantiator != null){
            // this is a bit clumsy, we actually need to inject something like a view configurator
            // which can enable, disable fields
            referenceEditorPopup.getInReferenceCombobox().setEnabled(false);
            referenceEditorPopup.getTypeSelect().setEnabled(false);
        }
    }

    @EventBusListenerMethod
    public void onFieldReplaceEvent(FieldReplaceEvent<String> event){

        PropertyIdPath boundPropertyIdPath = boundPropertyIdPath(event.getNewField());
        if(boundPropertyIdPath != null){
            TaxonNameDTO taxonNamedto = ((AbstractPopupEditor<TaxonNameDTO, AbstractCdmDTOEditorPresenter<TaxonNameDTO, TaxonName,?>>)getView()).getBean();
            if(boundPropertyIdPath.matches("specificEpithet")){
                AbstractField<String> genusOrUninomialField = getView().getGenusOrUninomialField();
                if(event.getNewField() instanceof CompositeCustomField){
                    if(specificEpithetPartPagingProvider == null){
                        specificEpithetPartPagingProvider = new TaxonNameStringFilterablePagingProvider(getRepo().getNameService(), Rank.SPECIES());
                    }
                    specificEpithetPartPagingProvider.listenToFields(
                            genusOrUninomialField,
                            null, null, null);
                    specificEpithetPartPagingProvider.excludeNames(taxonNamedto.cdmEntity());
                    specificEpithetPartPagingProvider.updateFromFields();
                    WeaklyRelatedEntityCombobox<TaxonName> specificEpithetField = (WeaklyRelatedEntityCombobox<TaxonName>)event.getNewField();
                    refreshSpecificEpithetComboBoxListener = e -> { specificEpithetField.getSelect().refresh(); specificEpithetField.setValue(null);};
                    specificEpithetField.loadFrom(specificEpithetPartPagingProvider, specificEpithetPartPagingProvider, specificEpithetPartPagingProvider.getPageSize());
                    specificEpithetField.setValue(event.getOldField().getValue());
                    specificEpithetField.reload();
                    genusOrUninomialField.addValueChangeListener(refreshSpecificEpithetComboBoxListener);
                } else {
                    if(specificEpithetPartPagingProvider != null){
                        specificEpithetPartPagingProvider.unlistenAllFields();
                    }
                    if(refreshSpecificEpithetComboBoxListener != null){
                        genusOrUninomialField.removeValueChangeListener(refreshSpecificEpithetComboBoxListener);
                        refreshSpecificEpithetComboBoxListener = null;
                    }
                }
            } else if(boundPropertyIdPath.matches("genusOrUninomial")) {
                if(event.getNewField() instanceof CompositeCustomField){
                    if(genusOrUninomialPartPagingProvider  == null){
                        genusOrUninomialPartPagingProvider = new TaxonNameStringFilterablePagingProvider(getRepo().getNameService());
                    }
                    genusOrUninomialPartPagingProvider.listenToFields(
                                null,
                                getView().getInfraGenericEpithetField(),
                                getView().getSpecificEpithetField(),
                                getView().getInfraSpecificEpithetField()
                               );
                    genusOrUninomialPartPagingProvider.excludeNames(taxonNamedto.cdmEntity());
                    WeaklyRelatedEntityCombobox<TaxonName> genusOrUninomialField = (WeaklyRelatedEntityCombobox<TaxonName>)event.getNewField();
                    genusOrUninomialField.loadFrom(genusOrUninomialPartPagingProvider, genusOrUninomialPartPagingProvider, genusOrUninomialPartPagingProvider.getPageSize());
                    genusOrUninomialField.setValue(event.getOldField().getValue());
                    genusOrUninomialField.reload();
                }else {
                    if(genusOrUninomialPartPagingProvider != null){
                        genusOrUninomialPartPagingProvider.unlistenAllFields();
                    }
                }

            }
        }

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent<?> event){

        if(event.getSourceView() instanceof AbstractPopupEditor) {

            BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

            if(boundTargetField != null){
                if(boundTargetField.matchesPropertyIdPath("genusOrUninomial")){
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());
                        if(getView().getGenusOrUninomialField() instanceof WeaklyRelatedEntityCombobox){
                            WeaklyRelatedEntityCombobox<TaxonName> weaklyRelatedEntityCombobox = (WeaklyRelatedEntityCombobox<TaxonName>)getView().getGenusOrUninomialField();
                            if(event.isCreatedType()){
                                weaklyRelatedEntityCombobox.setValue(((TaxonName)event.getEntity()).getGenusOrUninomial());
                                weaklyRelatedEntityCombobox.reload();
                            } else {
                                weaklyRelatedEntityCombobox.reload();
                            }
                            // NOTE: in constrast to the ToOneRelatedEntityCombobox the .discard() does not
                            // work here since no datasource is bound to the field, see weaklyRelatedEntityCombobox.reload()
                            weaklyRelatedEntityCombobox.updateButtons();
                        }
                    }
                } else
                if(boundTargetField.matchesPropertyIdPath("specificEpithet")){
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());

                        if(getView().getSpecificEpithetField() instanceof WeaklyRelatedEntityCombobox){
                            WeaklyRelatedEntityCombobox weaklyRelatedEntityCombobox = (WeaklyRelatedEntityCombobox)getView().getSpecificEpithetField();
                            if(event.isCreatedType()){
                                getView().getSpecificEpithetField().setValue(((TaxonName)event.getEntity()).getSpecificEpithet());
                                weaklyRelatedEntityCombobox.reload();
                            } else {
                                weaklyRelatedEntityCombobox.reload();
                            }
                            // NOTE: in constrast to the ToOneRelatedEntityCombobox the .discard() does not
                            // work here since no datasource is bound to the field, see weaklyRelatedEntityCombobox.reload()
                            weaklyRelatedEntityCombobox.updateButtons();
                        }
                    }
                } else
                if(boundTargetField.matchesPropertyIdPath("nomenclaturalReference")){
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());
                        if(event.isCreatedType()){
                            getView().getNomReferenceCombobox().setValue((Reference) event.getEntity());
                        } else {
                            getView().getNomReferenceCombobox().reload(); // refreshSelectedValue(modifiedReference);
                        }
                        getView().getCombinationAuthorshipField().discard(); //refresh from the datasource
                        getView().updateAuthorshipFields();
                    }
                } else
                if(boundTargetField.matchesPropertyIdPath("validationFor.otherName")){
                    ReloadableLazyComboBox<TaxonName> otherNameField = asReloadableLazyComboBox(boundTargetField.getField(TaxonName.class));
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());
                        if(event.isCreatedType()){
                            otherNameField.setValue((TaxonName) event.getEntity());
                        } else {
                            otherNameField.reload();
                        }
                    } else
                    if(event.isRemovedType()){
                        otherNameField.setValue(null);
                    }
                } else
                if(boundTargetField.matchesPropertyIdPath("basionyms")){
                    ReloadableLazyComboBox<TaxonName> basionymSourceField = asReloadableLazyComboBox(boundTargetField.getField(TaxonName.class));
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());
                        if(event.isCreatedType()){
                            basionymSourceField .setValue((TaxonName) event.getEntity());
                        } else {
                            basionymSourceField.reload();
                        }
                        getView().getBasionymAuthorshipField().discard(); //refresh from the datasource
                        getView().getExBasionymAuthorshipField().discard(); //refresh from the datasource
                        getView().updateAuthorshipFields();
                    } else
                    if(event.isRemovedType()){
                        basionymSourceField.setValue(null);
                        getView().updateAuthorshipFields();
                    }
                } else
                if(boundTargetField.matchesPropertyIdPath("replacedSynonyms")){
                    ReloadableLazyComboBox<TaxonName> replacedSynonyms = asReloadableLazyComboBox(boundTargetField.getField(TaxonName.class));
                    if(event.isCreateOrModifiedType()){
                        getCache().load(event.getEntity());
                        if(event.isCreatedType()){
                            replacedSynonyms .setValue((TaxonName) event.getEntity());
                        } else {
                            replacedSynonyms.reload();
                        }
                        getView().getExCombinationAuthorshipField().discard(); //refresh from the datasource
                        getView().updateAuthorshipFields();
                    } else
                    if(event.isRemovedType()){
                        replacedSynonyms.setValue(null);
                        getView().updateAuthorshipFields();
                    }
                }

            }
        }
    }

    protected <CDM extends CdmBase> ReloadableLazyComboBox<CDM> asReloadableLazyComboBox(Field<CDM> field){

        if(field instanceof ToOneRelatedEntityCombobox){
            field = ((ToOneRelatedEntityCombobox<CDM>)field).getSelect();
        }
        return (ReloadableLazyComboBox<CDM>)field;
    }



    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        PropertyIdPath boundPropertyId = boundPropertyIdPath(event.getTarget());

        if(boundPropertyId != null){
            if(boundPropertyId.matches("validationFor.otherName") || boundPropertyId.matches("basionyms") || boundPropertyId.matches("replacedSynonyms")){
                TaxonNamePopupEditor namePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                namePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                    .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                    .forEach(m -> namePopup.enableMode(m));
                namePopup.loadInEditor(event.getEntityUuid());
            }
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionStrRepEdit(TaxonNameEditorActionStrRep event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        PropertyIdPath boundPropertyId = boundPropertyIdPath(event.getTarget());

        if(boundPropertyId != null){
            if(boundPropertyId.matches("genusOrUninomial") || boundPropertyId.matches("specificEpithet")){
                TaxonNamePopupEditor namePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                namePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                    .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                    .forEach(m -> namePopup.enableMode(m));
                namePopup.loadInEditor(event.getEntityUuid());
            }
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionAdd(TaxonNameEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        PropertyIdPath boundPropertyId = boundPropertyIdPath(event.getTarget());

        if(boundPropertyId != null){
            if(boundPropertyId.matches("validationFor.otherName") || boundPropertyId.matches("basionyms") || boundPropertyId.matches("replacedSynonyms")){
                TaxonNamePopupEditor namePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                namePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
                namePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                        .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                        .forEach(m -> namePopup.enableMode(m));
                namePopup.loadInEditor(null);
            }
        }
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onTaxonNameEditorActionStrRepAdd(TaxonNameEditorActionStrRep event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        PropertyIdPath boundPropertyId = boundPropertyIdPath(event.getTarget());

        if(boundPropertyId != null){
            if(boundPropertyId.matches("genusOrUninomial") || boundPropertyId.matches("specificEpithet")){
                TaxonNamePopupEditor namePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                namePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
                namePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                .forEach(m -> namePopup.enableMode(m));
                namePopup.loadInEditor(null);
                if(boundPropertyId.matches("genusOrUninomial")){
                    namePopup.getRankSelect().setValue(Rank.GENUS());
                }
                if(boundPropertyId.matches("specificEpithet")){
                    namePopup.getGenusOrUninomialField().setValue(getView().getGenusOrUninomialField().getValue());
                    namePopup.getRankSelect().setValue(Rank.SPECIES());
                }
                if(WeaklyRelatedEntityField.class.isAssignableFrom(event.getTarget().getClass())){
                    WeaklyRelatedEntityField<TaxonName> taxoNameField = (WeaklyRelatedEntityField<TaxonName>)event.getTarget();
                    if(!taxoNameField.isValueInOptions()){
                        String nameString = event.getTarget().getValue();
                        if(StringUtils.isNotEmpty(nameString)){
                            if(boundPropertyId.matches("genusOrUninomial")){
                                namePopup.getGenusOrUninomialField().setValue(nameString);
                            }
                            if(boundPropertyId.matches("specificEpithet")){
                                namePopup.getSpecificEpithetField().setValue(nameString);
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaxonNameDTO createDTODecorator(TaxonName cdmEntitiy) {
        return new TaxonNameDTO(cdmEntitiy);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BeanInstantiator<TaxonName> defaultCdmEntityInstantiator() {
        return new BeanInstantiator<TaxonName>() {

            @Override
            public TaxonName createNewBean() {
                return  TaxonNameFactory.NewNameInstance(RegistrationUIDefaults.NOMENCLATURAL_CODE, Rank.SPECIES());
            }
        };
    }




}
