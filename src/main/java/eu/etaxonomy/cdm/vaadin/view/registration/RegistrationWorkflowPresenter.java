/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.vaadin.view.name.RegistrationAndWorkingsetId;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationWorkflowViewBean.ViewParameters;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkflowPresenter extends AbstractPresenter<RegistrationWorkflowView> {

    private static final long serialVersionUID = 1L;

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    /**
     * @return the workingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        ensureBoundConversation();
        return workingSetService;
    }

    private RegistrationWorkingSet workingset;

    private Reference citation;


    /**
     *
     */
    public RegistrationWorkflowPresenter() {
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        super.handleViewEntered();
        ViewParameters viewParams = getView().getViewParameters();
        if(viewParams.action.equals(RegistrationWorkflowView.ACTION_NEW)){
            citation = getRepo().getReferenceService().find(viewParams.referenceId);
            workingset = new RegistrationWorkingSet(citation);
            getView().setHeaderText("Registration for " + workingset.getCitation());
            getView().setWorkingset(workingset);
        }
        if(viewParams.action.equals(RegistrationWorkflowView.ACTION_EDIT)){
            presentWorkingSetByRegID(viewParams.referenceId);
        }
    }

    /**
     * @param registrationID
     * @deprecated use other method working sets should only be addressed by the referenceID
     */
    @Deprecated
    private void presentWorkingSetByRegID(Integer citationID) {
        try {
            workingset = getWorkingSetService().loadWorkingSetByCitationID(citationID);
        } catch (RegistrationValidationException error) {
            getView().getWorkflow().setComponentError(new SystemError(error));
        }
        getView().setHeaderText("Registrations in " + workingset.getCitation());
        getView().setWorkingset(workingset);
    }

    /**
     * @param registrationID
     */
    private void presentWorkingSet(Integer referenceID) {
        try {
            workingset = getWorkingSetService().loadWorkingSetByReferenceID(referenceID);
        } catch (RegistrationValidationException error) {
            getView().getWorkflow().setComponentError(new SystemError(error));
        }
        getView().setHeaderText("Registration for " + workingset.getCitation());
        getView().setWorkingset(workingset);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.loadInEditor(null);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.withDeleteButton(true);
        popup.loadInEditor(event.getEntityId());
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onRegistrationEditorAction(RegistrationEditorAction event) {
        RegistrationPopupEditor popup = getNavigationManager().showInPopup(RegistrationPopupEditor.class);
        popup.loadInEditor(event.getEntityId());
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onTaxonNameEditorAction(TaxonNameEditorAction event) {

        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        popup.withDeleteButton(true);
        // disable NomReferenceCombobox:
        // the in the registration application inReferences should only edited centrally
        popup.getNomReferenceCombobox().setEnabled(false);
        popup.loadInEditor(event.getEntityId());


    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onTypeDesignationsEditorActionEdit(TypeDesignationWorkingsetEditorAction event) {

            if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET ){
                SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
                popup.loadInEditor(new RegistrationAndWorkingsetId(event.getRegistrationId(), event.getEntityId()));
            } else {
                // TypeDesignationWorkingSetType.NAME_TYPE_DESIGNATION_WORKINGSET
                // FIXME implement NameTypeDesignationWorkingsetPopupEditor
            }

    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onTypeDesignationsEditorActionAdd(TypeDesignationWorkingsetEditorAction event) {

        if(event.getWorkingSetType() == TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET){
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
            popup.loadInEditor(null);
            popup.withDeleteButton(true);
        }
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet)")
    public void onShowRegistrationWorkingSetMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        List<String> messages = new ArrayList<>();
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()){
            dto.getMessages().forEach(m -> messages.add(dto.getSummary() + ": " + m));
        }
        if(event.getProperty().equals("messages")){
            getView().openDetailsPopup("Messages", messages);
        }
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO)")
    public void onShowRegistrationMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        RegistrationDTO regDto = workingSetService.loadDtoById((Integer)event.getIdentifier());
        if(event.getProperty().equals("messages")){
            if(getView() != null){
                getView().openDetailsPopup("Messages", regDto.getMessages());
            }
        }
    }

    @EventListener
    public void onEntityChangeEvent(EntityChangeEvent event){
        if(workingset == null){
            return;
        }
        if(Reference.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getCitationId().equals(event.getEntityId())){
                refreshView();
            }
        } else
        if(Registration.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrations().stream().anyMatch(reg -> reg.getId() == event.getEntityId())){
                refreshView();
            }
        } else
        if(TaxonName.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(reg ->
                reg.getTypifiedName() != null
                && reg.getTypifiedName().getId() == event.getEntityId())){
                    refreshView();
            }
        } else
        if(TypeDesignationBase.class.isAssignableFrom(event.getEntityType())){
            if(workingset.getRegistrationDTOs().stream().anyMatch(
                    reg -> reg.getTypeDesignations().stream().anyMatch(
                            td -> td.getId() == event.getEntityId()
                            )
                        )
                    ){
                refreshView();
            }
        }

    }

    /**
     *
     */
    protected void refreshView() {
        getConversationHolder().getSession().clear();
        presentWorkingSet(workingset.getCitationId());
    }

}
