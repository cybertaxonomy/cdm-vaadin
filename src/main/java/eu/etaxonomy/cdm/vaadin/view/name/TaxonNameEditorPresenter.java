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
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Restrictions;
import org.springframework.context.event.EventListener;

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
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
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

    private static final long serialVersionUID = -3538980627079389221L;

    private static final Logger logger = Logger.getLogger(TaxonNameEditorPresenter.class);

    ReferencePopupEditor newReferencePopup = null;

    private CdmFilterablePagingProvider<Reference, Reference> referencePagingProvider;

    private Reference publishedUnit;

    private BeanInstantiator<Reference> newReferenceInstantiator;

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

        getView().getBasionymCombobox().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());
        CdmFilterablePagingProvider<TaxonName, TaxonName> namePagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());

        getView().getBasionymCombobox().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
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
            taxonName = TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES());
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

        return taxonName;
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
        List<TaxonName> newBasionymNames = getView().getBasionymCombobox().getValueFromNestedFields();
        Set<TaxonName> oldBasionyms = bean.getBasionyms();
        boolean updateBasionyms = false;

        for(TaxonName newB : newBasionymNames){
            updateBasionyms = updateBasionyms || !oldBasionyms.contains(newB);
        }

        for(TaxonName oldB : oldBasionyms){
            updateBasionyms = updateBasionyms || !newBasionymNames.contains(oldB);
        }

        if(updateBasionyms){
            bean.removeBasionyms();
            for(TaxonName basionymName :newBasionymNames){
                if(basionymName != null){
                    if(basionymName .getUuid() != null){
                        // reload
                        basionymName = getRepo().getNameService().load(basionymName.getUuid(), Arrays.asList("$", "relationsFromThisName", "homotypicalGroup.typifiedNames"));
                    }
                    bean.addBasionym(basionymName);
                }
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

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        newReferencePopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);

        newReferencePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        newReferencePopup.withDeleteButton(true);
        newReferencePopup.setBeanInstantiator(newReferenceInstantiator);
        newReferencePopup.loadInEditor(null);
        if(newReferenceInstantiator != null){
            // this is a bit clumsy, we actually need to inject something like a view configurer
            // which can enable, disable fields
            newReferencePopup.getInReferenceCombobox().setEnabled(false);
            newReferencePopup.getTypeSelect().setEnabled(false);
        }
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT")
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {

        if(getView() == null || event.getSourceView() != getView() ){
            return;
        }
        newReferencePopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);

        newReferencePopup.grantToCurrentUser(EnumSet.of(CRUD.UPDATE, CRUD.DELETE));
        newReferencePopup.withDeleteButton(true);
        newReferencePopup.setBeanInstantiator(newReferenceInstantiator);
        newReferencePopup.loadInEditor(event.getEntityId());
        if(newReferenceInstantiator != null){
            // this is a bit clumsy, we actually need to inject something like a view configurer
            // which can enable, disable fields
            newReferencePopup.getInReferenceCombobox().setEnabled(false);
            newReferencePopup.getTypeSelect().setEnabled(false);
        }
    }

    @EventListener
    public void onDoneWithPopupEvent(DoneWithPopupEvent event){
        if(event.getPopup() == newReferencePopup){
            if(event.getReason() == Reason.SAVE){

                Reference newReference = newReferencePopup.getBean();

                // TODO the bean contained in the popup editor is not yet updated at this point.
                //      so re reload it using the uuid since new beans will not have an Id at this point.
                newReference = getRepo().getReferenceService().load(newReference.getUuid(), Arrays.asList("inReference"));
                getView().getNomReferenceCombobox().setValue(newReference);
            }

            newReferencePopup = null;
        }
    }



}
