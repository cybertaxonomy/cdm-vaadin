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
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.CollectionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class CollectionEditorPresenter extends AbstractCdmEditorPresenter<Collection, CollectionPopupEditorView> {

    private static final long serialVersionUID = -1996365248431425021L;
    private CollectionPopupEditor collectionPopuEditor;


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
        getView().getSuperCollectionCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Collection>(getView().getSuperCollectionCombobox(),this));
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onCollectionEditorActionAdd(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        collectionPopuEditor = getNavigationManager().showInPopup(CollectionPopupEditor.class, getView());

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(null);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onCollectionEditorActionEdit(CollectionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        collectionPopuEditor = getNavigationManager().showInPopup(CollectionPopupEditor.class, getView());

        collectionPopuEditor.grantToCurrentUser(this.crud);
        collectionPopuEditor.withDeleteButton(true);
        collectionPopuEditor.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod()
    public void onEntityChangeEvent(EntityChangeEvent<?> event){
        if(event.getSourceView() == collectionPopuEditor){
            if(event.isCreateOrModifiedType()){

                Collection newCollection = (Collection) event.getEntity();
                getCache().load(newCollection);
                if(event.isCreatedType()){
                    getView().getSuperCollectionCombobox().setValue(newCollection);
                } else {
                    getView().getSuperCollectionCombobox().reload();
                }
            }

            collectionPopuEditor = null;
        }
    }



}
