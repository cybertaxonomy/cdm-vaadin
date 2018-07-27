/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.occurrence;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction.Operator;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.CollectionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.view.common.InstitutionPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.BoundField;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class CollectionEditorPresenter extends AbstractCdmEditorPresenter<Collection, CollectionPopupEditorView> {

    private static final long serialVersionUID = -1996365248431425021L;


    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",
                "institute.$",
                "superCollection.$",
                }
        );

        Collection bean;
        if(identifier != null){
            bean = getRepo().getCollectionService().load(identifier, initStrategy);
        } else {
            bean = Collection.NewInstance();
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(Collection.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Collection bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<Collection> getService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        CdmFilterablePagingProvider<Collection, Collection> collectionPagingProvider = new CdmFilterablePagingProvider<Collection, Collection>(getRepo().getCollectionService());
        collectionPagingProvider.getRestrictions().add(new Restriction<String>("institute.titleCache", Operator.OR, MatchMode.ANYWHERE, CdmFilterablePagingProvider.QUERY_STRING_PLACEHOLDER));
        getView().getSuperCollectionCombobox().getSelect().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());
        getView().getSuperCollectionCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Collection>(getView().getSuperCollectionCombobox(), this));

        CdmFilterablePagingProvider<AgentBase, Institution> institutionPagingProvider = new CdmFilterablePagingProvider<AgentBase, Institution>(getRepo().getAgentService(), Institution.class);
        getView().getInstitutionCombobox().getSelect().loadFrom(institutionPagingProvider, institutionPagingProvider, institutionPagingProvider.getPageSize());
        getView().getInstitutionCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getInstitutionCombobox(), this));
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onCollectionEditorActionAdd(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        CollectionPopupEditor collectionPopuEditor = openPopupEditor(CollectionPopupEditor.class, event);

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(null);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onCollectionEditorActionEdit(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        CollectionPopupEditor collectionPopuEditor = openPopupEditor(CollectionPopupEditor.class, event);

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onInstitutionEditorActionEdit(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor institutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        institutionPopuEditor.grantToCurrentUser(this.crud);
        institutionPopuEditor.withDeleteButton(true);
        institutionPopuEditor.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onInstitutionEditorActionAdd(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor institutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        institutionPopuEditor.grantToCurrentUser(this.crud);
        institutionPopuEditor.withDeleteButton(true);
        institutionPopuEditor.loadInEditor(null);
    }

    @EventBusListenerMethod()
    public void onEntityChangeEvent(EntityChangeEvent<?> event){

        BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

        if(boundTargetField != null){
            if(boundTargetField.matchesPropertyIdPath("superCollection")){
                if(event.isCreateOrModifiedType()){

                    Collection newCollection = (Collection) event.getEntity();
                    getCache().load(newCollection);
                    if(event.isCreatedType()){
                        getView().getSuperCollectionCombobox().setValue(newCollection);
                    } else {
                        getView().getSuperCollectionCombobox().reload();
                    }
                }

            } else if(boundTargetField.matchesPropertyIdPath("institute")){
                if(event.isCreateOrModifiedType()){

                    Institution newInstitution = (Institution) event.getEntity();
                    getCache().load(newInstitution);
                    if(event.isCreatedType()){
                        getView().getInstitutionCombobox().setValue(newInstitution);
                    } else {
                        getView().getInstitutionCombobox().reload();
                    }
                }

            }
        }
    }



}
