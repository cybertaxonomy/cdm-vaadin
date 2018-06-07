/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;
import org.vaadin.viritin.fields.CaptionGenerator;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.permission.UserHelper;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class ReferenceEditorPresenter extends AbstractCdmEditorPresenter<Reference, ReferencePopupEditorView> {

    private static final long serialVersionUID = -7926116447719010837L;

    private static final Logger logger = Logger.getLogger(ReferenceEditorPresenter.class);

    ReferencePopupEditor inReferencePopup = null;

    public ReferenceEditorPresenter() {

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getInReferenceCombobox().getSelect().setCaptionGenerator(new CaptionGenerator<Reference>(){

            @Override
            public String getCaption(Reference option) {
                return option.getTitleCache();
            }

        });

        CdmFilterablePagingProvider<Reference, Reference> collectionPagingProvider = pagingProviderFactory.referencePagingProvider();
        getView().getInReferenceCombobox().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());
        getView().getInReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getInReferenceCombobox()));
        getView().getInReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<Reference>(getView().getInReferenceCombobox(),this));

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> teamOrPersonPagingProvider = new CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase>(getRepo().getAgentService(), TeamOrPersonBase.class);
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = new CdmFilterablePagingProvider<AgentBase, Person>(getRepo().getAgentService(), Person.class);
        getView().getAuthorshipField().setFilterableTeamPagingProvider(teamOrPersonPagingProvider, this);
        getView().getAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Reference loadCdmEntity(UUID identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$"
                }
        );

        Reference reference;
        if(identifier != null){
            reference = getRepo().getReferenceService().load(identifier, initStrategy);
        } else {
            reference = createNewReference();
        }
        return reference;
    }

    /**
     * TODO this should better go into {@link AbstractCdmEditorPresenter}
     *
     * @return
     */
    protected Reference createNewReference() {
        if(this.beanInstantiator != null){
            return beanInstantiator.createNewBean();
        }
        return ReferenceFactory.newGeneric();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(Reference.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Reference bean) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    /**
    *
    * @param editorAction
     * @throws EditorEntityBeanException
    */
   @EventBusListenerMethod
   public void onReferenceEditorAction(ReferenceEditorAction editorAction) {

       if(!isFromOwnView(editorAction) || editorAction.getTarget() == null){
           return;
       }

       if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getTarget().getClass())){
           if(editorAction.isAddAction()){
               inReferencePopup = openPopupEditor(ReferencePopupEditor.class, editorAction);
               inReferencePopup.loadInEditor(null);
           }
           if(editorAction.isEditAction()){
               inReferencePopup = openPopupEditor(ReferencePopupEditor.class, editorAction);
               inReferencePopup.withDeleteButton(true);
               inReferencePopup.loadInEditor(editorAction.getEntityUuid());
           }
       }
   }

   @EventBusListenerMethod
   public void onEntityChangeEvent(EntityChangeEvent<?> event){

       if(event.getSourceView() == inReferencePopup){
           if(event.isCreateOrModifiedType()){
               getCache().load(event.getEntity());
               getView().getInReferenceCombobox().reload();
           }
           if(event.isRemovedType()){
               getView().getInReferenceCombobox().selectNewItem(null);
           }
           inReferencePopup = null;
       }

   }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<Reference> getService() {
        return getRepo().getReferenceService();
    }



}
