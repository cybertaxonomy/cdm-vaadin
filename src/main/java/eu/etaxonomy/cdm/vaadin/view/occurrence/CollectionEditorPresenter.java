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

import org.springframework.context.event.EventListener;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.CollectionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
public class CollectionEditorPresenter extends AbstractCdmEditorPresenter<Collection, CollectionPopupEditorView> {

    private static final long serialVersionUID = -1996365248431425021L;
    private CollectionPopupEditor collectionPopuEditor;

    /**
     * {@inheritDoc}
     */
    @Override
    protected Collection loadCdmEntityById(Integer identifier) {

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
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(Collection.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Collection bean) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(bean, crud, null);
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
        getView().getSuperCollectionCombobox().getSelect().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());

        getView().getSuperCollectionCombobox().getSelect().addValueChangeListener(
                new ToOneRelatedEntityReloader<Collection>(getView().getSuperCollectionCombobox(),
                this)
                );
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).ADD")
    public void onCollectionEditorActionAdd(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        collectionPopuEditor = getNavigationManager().showInPopup(CollectionPopupEditor.class);

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(null);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.vaadin.event.EditorActionType).EDIT")
    public void onCollectionEditorActionEdit(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        collectionPopuEditor = getNavigationManager().showInPopup(CollectionPopupEditor.class);

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(event.getEntityId());
    }

    public void onDoneWithPopupEvent(DoneWithPopupEvent event){
        if(event.getPopup() == collectionPopuEditor){
            if(event.getReason() == Reason.SAVE){

                Collection newCollection = collectionPopuEditor.getBean();

                // TODO the bean contained in the popup editor is not yet updated at this point.
                //      so re reload it using the uuid since new beans will not have an Id at this point.
                newCollection = getRepo().getCollectionService().find(newCollection.getUuid());
                getView().getSuperCollectionCombobox().getSelect().setValue(newCollection);
            }

            collectionPopuEditor = null;
        }
    }



}
