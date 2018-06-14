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

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.TaxonNameStringFilterablePagingProvider;
import eu.etaxonomy.cdm.service.initstrategies.AgentBaseInit;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.name.TaxonNameDTO;
import eu.etaxonomy.cdm.vaadin.permission.UserHelper;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.component.ReloadableLazyComboBox;
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

    private static final List<String> REFERENCE_INIT_STRATEGY = Arrays.asList("authorship", "inReference.authorship", "inReference.inReference.authorship", "inReference.inReference.inReference.authorship");

    private static final long serialVersionUID = -3538980627079389221L;

    private static final Logger logger = Logger.getLogger(TaxonNameEditorPresenter.class);

    private CdmFilterablePagingProvider<Reference, Reference> nomReferencePagingProvider;

    private Reference publishedUnit;

    private BeanInstantiator<Reference> newReferenceInstantiator;

    private TaxonNameStringFilterablePagingProvider genusOrUninomialPartPagingProvider;

    private TaxonNameStringFilterablePagingProvider specificEpithetPartPagingProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        super.handleViewEntered();

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getRankSelect().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(TermType.Rank));
        getView().getRankSelect().setItemCaptionPropertyId("label");

        // genusOrUninomialField
        if(getView().getGenusOrUninomialField() instanceof LazyComboBox){
            genusOrUninomialPartPagingProvider = new TaxonNameStringFilterablePagingProvider(getRepo().getNameService());
            genusOrUninomialPartPagingProvider.listenToFields(
                    null,
                    getView().getInfraGenericEpithetField(),
                    getView().getSpecificEpithetField(),
                    getView().getInfraSpecificEpithetField()
                   );
            ((LazyComboBox)getView().getGenusOrUninomialField()).loadFrom(genusOrUninomialPartPagingProvider, genusOrUninomialPartPagingProvider, genusOrUninomialPartPagingProvider.getPageSize());
        }

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
        getView().getNomReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getNomReferenceCombobox()));
        getView().getNomReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getNomReferenceCombobox(), this));

        getView().getBasionymComboboxSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());

        CdmFilterablePagingProvider<TaxonName, TaxonName> basionymPagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());
        basionymPagingProvider.setInitStrategy(BASIONYM_INIT_STRATEGY);
        getView().getBasionymComboboxSelect().setPagingProviders(basionymPagingProvider, basionymPagingProvider, basionymPagingProvider.getPageSize(), this);

        getView().getReplacedSynonymsComboboxSelect().setCaptionGenerator( new CdmTitleCacheCaptionGenerator<TaxonName>());
        // reusing the basionymPagingProvider for the replaced synonyms to benefit from caching
        getView().getReplacedSynonymsComboboxSelect().setPagingProviders(basionymPagingProvider, basionymPagingProvider, basionymPagingProvider.getPageSize(), this);

        getView().getValidationField().getValidatedNameComboBox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());
        // reusing the basionymPagingProvider for the replaced synonyms to benefit from caching
        getView().getValidationField().getValidatedNameComboBox().loadFrom(basionymPagingProvider, basionymPagingProvider, basionymPagingProvider.getPageSize());
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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaxonName loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",
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

                }
        );

        TaxonName taxonName;
        if(identifier != null){
            taxonName = getRepo().getNameService().load(identifier, initStrategy);
        } else {
            taxonName = TaxonNameFactory.NewNameInstance(RegistrationUIDefaults.NOMENCLATURAL_CODE, Rank.SPECIES());
        }

        if(getView().isModeEnabled(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY)){
            if(taxonName.getNomenclaturalReference() != null){
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
        }

        return taxonName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(TaxonName.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(TaxonName bean) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(bean, crud, null);
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


        if(getView() == null || event.getSourceView() != getView() ){
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
        if(boundPropertyIdPath.matches("specificEpithet")){
            if(event.getNewField() instanceof LazyComboBox){

                if(specificEpithetPartPagingProvider  == null){
                    specificEpithetPartPagingProvider = new TaxonNameStringFilterablePagingProvider(getRepo().getNameService(), Rank.SPECIES());
                }
                specificEpithetPartPagingProvider.listenToFields(
                        getView().getGenusOrUninomialField(),
                        null, null, null);
                specificEpithetPartPagingProvider.updateFromFields();
                ((LazyComboBox)event.getNewField()).loadFrom(specificEpithetPartPagingProvider, specificEpithetPartPagingProvider, specificEpithetPartPagingProvider.getPageSize());
            } else {
                if(specificEpithetPartPagingProvider != null){
                    specificEpithetPartPagingProvider.unlistenAllFields();
                }
            }

        }

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent<?> event){

        if(event.getSourceView() instanceof AbstractPopupEditor) {

            BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

            if(boundTargetField != null){
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
                }
                if(boundTargetField.matchesPropertyIdPath("validationFor.otherName")){
                    ReloadableLazyComboBox<TaxonName> otherNameField = (ReloadableLazyComboBox<TaxonName>)boundTargetField.getField(TaxonName.class);
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
                    ReloadableLazyComboBox<TaxonName> basionymSourceField = (ReloadableLazyComboBox<TaxonName>)boundTargetField.getField(TaxonName.class);
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
                }

            }
        }
    }



    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }

        PropertyIdPath boundPropertyId = boundPropertyIdPath(event.getTarget());

        if(boundPropertyId != null){
            if(boundPropertyId.matches("validationFor.otherName") || boundPropertyId.matches("basionyms") || boundPropertyId.matches("replacedSynonyms")){
                TaxonNamePopupEditor validatedNamePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                validatedNamePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                    .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                    .forEach(m -> validatedNamePopup.enableMode(m));
                validatedNamePopup.loadInEditor(event.getEntityUuid());
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
                TaxonNamePopupEditor validatedNamePopup = openPopupEditor(TaxonNamePopupEditor.class, event);
                validatedNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
                validatedNamePopup.withDeleteButton(true);
                getView().getModesActive().stream()
                        .filter(m -> !TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY.equals(m))
                        .forEach(m -> validatedNamePopup.enableMode(m));
                validatedNamePopup.loadInEditor(null);
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




}
