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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.event.EventListener;

import com.vaadin.ui.AbstractField;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.cache.CdmEntityCache;
import eu.etaxonomy.cdm.debug.PersistentContextAnalyzer;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.name.NameRelationship;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public class TaxonNameEditorPresenter extends AbstractCdmEditorPresenter<TaxonName, TaxonNamePopupEditorView> {

    /**
     *
     */
    private static final List<String> BASIONYM_INIT_STRATEGY = Arrays.asList("$", "relationsFromThisName", "homotypicalGroup.typifiedNames");

    private static final long serialVersionUID = -3538980627079389221L;

    private static final Logger logger = Logger.getLogger(TaxonNameEditorPresenter.class);

    private ReferencePopupEditor referenceEditorPopup = null;

    private TaxonNamePopupEditor basionymNamePopup = null;

    private CdmFilterablePagingProvider<Reference, Reference> referencePagingProvider;

    private Reference publishedUnit;

    private BeanInstantiator<Reference> newReferenceInstantiator;

    private BeanInstantiator<TaxonName> newBasionymNameInstantiator;

    private AbstractField<TaxonName> basionymSourceField;

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
        referencePagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(getRepo().getReferenceService());
        getView().getNomReferenceCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
        getView().getNomReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getNomReferenceCombobox()));

        getView().getBasionymComboboxSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());

        CdmFilterablePagingProvider<TaxonName, TaxonName> basionymPagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());
        basionymPagingProvider.setInitStrategy(BASIONYM_INIT_STRATEGY);
        getView().getBasionymComboboxSelect().setPagingProviders(basionymPagingProvider, basionymPagingProvider, basionymPagingProvider.getPageSize(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaxonName loadCdmEntityById(Integer identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",
                "rank.vocabulary", // needed for comparing ranks

                "nomenclaturalReference.authorship",
                "nomenclaturalReference.inReference",

                "status.type",

                "combinationAuthorship",
                "exCombinationAuthorship",
                "basionymAuthorship",
                "exBasionymAuthorship",

                // basionyms: relationsToThisName.fromName
                "relationsToThisName.type",
                "relationsToThisName.fromName.rank",
                "relationsToThisName.fromName.nomenclaturalReference.authorship",
                "relationsToThisName.fromName.nomenclaturalReference.inReference",
                "relationsToThisName.fromName.relationsToThisName",
                "relationsToThisName.fromName.relationsFromThisName",

                "relationsFromThisName",
                //"relationsToThisName",
                "homotypicalGroup.typifiedNames"

                }
        );

        TaxonName taxonName;
        if(identifier != null){
            taxonName = getRepo().getNameService().load(identifier, initStrategy);
        } else {
            taxonName = TaxonNameFactory.NewNameInstance(RegistrationUIDefaults.NOMENCLATURAL_CODE, Rank.SPECIES());
        }

        if(getView().isModeEnabled(TaxonNamePopupEditorMode.nomenclaturalReferenceSectionEditingOnly)){
            if(taxonName.getNomenclaturalReference() != null){
                Reference nomRef = (Reference)taxonName.getNomenclaturalReference();
                //getView().getNomReferenceCombobox().setEnabled(nomRef.isOfType(ReferenceType.Section));
                publishedUnit = nomRef;
                while(publishedUnit.isOfType(ReferenceType.Section) && publishedUnit.getInReference() != null){
                    publishedUnit = nomRef.getInReference();
                }
                // reduce available references to those which are sections of the publishedUnit and the publishedUnit itself
                // referencePagingProvider
                referencePagingProvider.getCriteria().add(Restrictions.or(
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
        throw new RuntimeException("Error handler test");

        // return taxonName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
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

    @Override
    protected TaxonName handleTransientProperties(TaxonName bean) {

        logger.trace(this._toString() + ".onEditorSaveEvent - handling transient properties");


        List<TaxonName> newBasionymNames = getView().getBasionymComboboxSelect().getValueFromNestedFields();
        Set<TaxonName> oldBasionyms = bean.getBasionyms();
        Set<TaxonName> updateBasionyms = new HashSet<>();
        Set<TaxonName> removeBasionyms = new HashSet<>();

        for(TaxonName newB : newBasionymNames){
            if(!oldBasionyms.contains(newB)){
                updateBasionyms.add(newB);
            }
        }

        for(TaxonName oldB : oldBasionyms){
            if(!newBasionymNames.contains(oldB)){
                removeBasionyms.add(oldB);
            }
        }
        for(TaxonName removeBasionym :removeBasionyms){
            Set<NameRelationship> removeRelations = new HashSet<NameRelationship>();
            for (NameRelationship nameRelation : bean.getRelationsToThisName()){
                if (nameRelation.getType().isBasionymRelation() && nameRelation.getFromName().equals(removeBasionym)){
                    removeRelations.add(nameRelation);
                }
            }
            for (NameRelationship relation : removeRelations){
                bean.removeNameRelationship(relation);
            }
        }
        // updateBasionyms.clear(); // DEBUGGING #########################
        getRepo().getSession().clear();
        for(TaxonName addBasionymName :updateBasionyms){
            if(addBasionymName != null){
                // if(addBasionymName.getUuid() != null){
                    // reload

                    System.err.println("====== Cache ======");
                    addBasionymName = getRepo().getNameService().load(addBasionymName.getUuid(), BASIONYM_INIT_STRATEGY);
                    PersistentContextAnalyzer pca = new PersistentContextAnalyzer((CdmEntityCache)getCache(), getRepo().getSession());
                    pca.setShowHashCodes(true);
                    pca.printEntityGraph(System.err);
                    pca.printCopyEntities(System.err);

                    System.err.println("====== Basionym ======");
                    PersistentContextAnalyzer basiopca = new PersistentContextAnalyzer(addBasionymName, getRepo().getSession());
                    basiopca.setShowHashCodes(true);
                    basiopca.printEntityGraph(System.err);

                    TaxonName cachedName = getCache().find(addBasionymName);
                    if(cachedName != null){
                        System.err.println("====== Cached Basionym ======");
                        PersistentContextAnalyzer cahedbasiopca = new PersistentContextAnalyzer(addBasionymName, getRepo().getSession());
                        cahedbasiopca.setShowHashCodes(true);
                        cahedbasiopca.printEntityGraph(System.err);
                        addBasionymName = cachedName;
                    }
                // }
                bean.addBasionym(addBasionymName);
            }
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected INameService getService() {
        return getRepo().getNameService();
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).ADD")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        referenceEditorPopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);

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

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).EDIT")
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        referenceEditorPopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);

        referenceEditorPopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        referenceEditorPopup.withDeleteButton(true);
        referenceEditorPopup.setBeanInstantiator(newReferenceInstantiator);
        referenceEditorPopup.loadInEditor(event.getEntityId());
        if(newReferenceInstantiator != null){
            // this is a bit clumsy, we actually need to inject something like a view configurer
            // which can enable, disable fields
            referenceEditorPopup.getInReferenceCombobox().setEnabled(false);
            referenceEditorPopup.getTypeSelect().setEnabled(false);
        }
    }

    @EventListener
    public void onDoneWithPopupEvent(DoneWithPopupEvent event){

        if(event.getPopup() == referenceEditorPopup){
            if(event.getReason() == Reason.SAVE){

                Reference modifiedReference = referenceEditorPopup.getBean();

                // TODO the bean contained in the popup editor is not yet updated at this point.
                //      so re reload it using the uuid since new beans will not have an Id at this point.
                modifiedReference = getRepo().getReferenceService().load(modifiedReference.getUuid(), Arrays.asList("inReference"));
                getView().getNomReferenceCombobox().setValue(modifiedReference);
            }

            referenceEditorPopup = null;
        }
        if(event.getPopup() == basionymNamePopup){
            if(event.getReason() == Reason.SAVE){
                TaxonName modifiedTaxonName = basionymNamePopup.getBean();

                // TODO the bean contained in the popup editor is not yet updated at this point.
                //      so re reload it using the uuid since new beans will not have an Id at this point.
                modifiedTaxonName = getRepo().getNameService().load(modifiedTaxonName.getUuid()); //, BASIONYM_INIT_STRATEGY);
                basionymSourceField.setValue(modifiedTaxonName);

                // TODO create blocking registration
            }
            if(event.getReason() == Reason.DELETE){
                basionymSourceField.setValue(null);
            }

            basionymNamePopup = null;
            basionymSourceField = null;
        }
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).EDIT")
    public void onTaxonNameEditorActionEdit(TaxonNameEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        basionymSourceField = (AbstractField<TaxonName>)event.getSourceComponent();

        basionymNamePopup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        basionymNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        basionymNamePopup.withDeleteButton(true);
        basionymNamePopup.loadInEditor(event.getEntityId());
        basionymNamePopup.getBasionymToggle().setVisible(false);

    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).ADD")
    public void onReferenceEditorActionAdd(TaxonNameEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        basionymSourceField = (AbstractField<TaxonName>)event.getSourceComponent();

        basionymNamePopup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        basionymNamePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        basionymNamePopup.withDeleteButton(true);
        basionymNamePopup.loadInEditor(null);
        basionymNamePopup.getBasionymToggle().setVisible(false);
    }


}
