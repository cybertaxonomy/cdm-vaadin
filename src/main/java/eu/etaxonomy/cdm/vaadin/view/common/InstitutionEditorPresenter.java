/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.common;

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
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.EditorActionTypeFilter;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.InstitutionEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.model.common.InstitutionDTO;
import eu.etaxonomy.vaadin.mvp.AbstractCdmDTOEditorPresenter;
import eu.etaxonomy.vaadin.mvp.BoundField;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class InstitutionEditorPresenter extends AbstractCdmDTOEditorPresenter<InstitutionDTO, Institution, InstitutionPopupEditorView> {

    private static final long serialVersionUID = -1996365248431425021L;


    /**
     * {@inheritDoc}
     */
    @Override
    protected Institution loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",
                "contact.$",
                "isPartOf.$",
                }
        );

        Institution bean;
        if(identifier != null){
            bean = (Institution) getRepo().getAgentService().load(identifier, initStrategy);
        } else {
            bean = Institution.NewInstance();
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
    protected void guaranteePerEntityCRUDPermissions(Institution bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<Institution> getService() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        CdmFilterablePagingProvider<AgentBase, Institution> collectionPagingProvider = new CdmFilterablePagingProvider<AgentBase, Institution>(getRepo().getAgentService(), Institution.class);
        getView().getPartOfCombobox().getSelect().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());
        getView().getPartOfCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Institution>(getView().getPartOfCombobox(), this));

    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Add.class)
    public void onInstitutionEditorActionAdd(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor intitutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        intitutionPopuEditor.grantToCurrentUser(this.crud);
        intitutionPopuEditor.withDeleteButton(true);
        intitutionPopuEditor.loadInEditor(null);
    }

    @EventBusListenerMethod(filter = EditorActionTypeFilter.Edit.class)
    public void onCollectionEditorActionEdit(InstitutionEditorAction event) {

        if(!checkFromOwnView(event)){
            return;
        }

        InstitutionPopupEditor intitutionPopuEditor = openPopupEditor(InstitutionPopupEditor.class, event);

        intitutionPopuEditor.grantToCurrentUser(this.crud);
        intitutionPopuEditor.withDeleteButton(true);
        intitutionPopuEditor.loadInEditor(event.getEntityUuid());
    }

    @EventBusListenerMethod()
    public void onEntityChangeEvent(EntityChangeEvent<?> event){

        BoundField boundTargetField = boundTargetField((PopupView) event.getSourceView());

        if(boundTargetField != null){
            if(boundTargetField.matchesPropertyIdPath("isPartOf")){
                if(event.isCreateOrModifiedType()){

                    Institution newInstitution = (Institution) event.getEntity();
                    getCache().load(newInstitution);
                    if(event.isCreatedType()){
                        getView().getPartOfCombobox().setValue(newInstitution);
                    } else {
                        getView().getPartOfCombobox().reload();
                    }
                }

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected InstitutionDTO createDTODecorator(Institution cdmEntitiy) {
        return new InstitutionDTO(cdmEntitiy);
    }



}
