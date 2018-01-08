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

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.vaadin.viritin.fields.CaptionGenerator;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityField;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
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

        CdmFilterablePagingProvider<Reference, Reference> collectionPagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(getRepo().getReferenceService());
        getView().getInReferenceCombobox().loadFrom(collectionPagingProvider, collectionPagingProvider, collectionPagingProvider.getPageSize());
        getView().getInReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getInReferenceCombobox()));

        CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> teamOrPersonPagingProvider = new CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase>(getRepo().getAgentService());
        CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider = new CdmFilterablePagingProvider<AgentBase, Person>(getRepo().getAgentService(), Person.class);
        getView().getAuthorshipField().setFilterableTeamPagingProvider(teamOrPersonPagingProvider, this);
        getView().getAuthorshipField().setFilterablePersonPagingProvider(personPagingProvider, this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Reference loadCdmEntityById(Integer identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",

                }
        );

        Reference reference;
        if(identifier != null){
            reference = getRepo().getReferenceService().load(identifier, initStrategy);
        } else {
            reference = ReferenceFactory.newGeneric();
        }
        return reference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
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
   @EventListener(condition = "#editorAction.sourceComponent != null")
   public void onReferenceEditorAction(ReferenceEditorAction editorAction) {
       if(!isFromOwnView(editorAction)){
           return;
       }
       if(ToOneRelatedEntityField.class.isAssignableFrom(editorAction.getSourceComponent().getClass())){
           if(editorAction.isAddAction()){
               inReferencePopup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
               inReferencePopup.loadInEditor(null);
           }
           if(editorAction.isEditAction()){
               ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
               popup.withDeleteButton(true);
               popup.loadInEditor(editorAction.getEntityId());
           }
       }
   }

   @EventListener
   public void doDoneWithPopupEvent(DoneWithPopupEvent event){

       if(event.getPopup().equals(inReferencePopup)){
           if(event.getReason().equals(Reason.SAVE)){
               Reference bean = inReferencePopup.getBean();
               getView().getInReferenceCombobox().selectNewItem(bean);
           }
           if(event.getReason().equals(Reason.DELETE)){
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
