/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationConverter.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.name.SpecimenTypeDesignationWorkingsetPopupEditor;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.reference.ReferencePopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkflowPresenter extends AbstractPresenter<RegistrationWorkflowView> implements Serializable{

    private static final long serialVersionUID = 1L;

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    private RegistrationWorkingSet workingset;

    /**
     *
     */
    public RegistrationWorkflowPresenter() {
    }

    @EventListener
    protected void onRegistrationStartEvent(RegistrationWorkflowEvent event){

        boolean HACK = true;
        if(workingset != null && !HACK){
            Logger.getLogger(RegistrationWorkflowPresenter.class).warn("Can't start a new workflow over an existing one.");
            return;
        }

        if(event.isStart()) {
            workingset = new RegistrationWorkingSet();
            Registration reg = Registration.NewInstance();
            reg.setName(TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES()));
            getView().setHeaderText("New Registration");
            try {
                workingset.add(reg);
            } catch (RegistrationValidationException error) {
                getView().getWorkflow().setComponentError(new SystemError(error));
            }
            getView().setWorkingset(workingset);
        } else {
            Integer citationID = event.getCitationID();
            presentWorkingSetByRegID(citationID);
        }

    }

    /**
     * @param registrationID
     * @deprecated use other method working sets should only be addressed by the referenceID
     */
    @Deprecated
    private void presentWorkingSetByRegID(Integer citationID) {
        try {
            workingset = workingSetService.loadWorkingSetByCitationID(citationID);
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
            workingset = workingSetService.loadWorkingSetByReferenceID(referenceID);
        } catch (RegistrationValidationException error) {
            getView().getWorkflow().setComponentError(new SystemError(error));
        }
        getView().setHeaderText("Registration for " + workingset.getCitation());
        getView().setWorkingset(workingset);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onReferenceEditorActionAdd(ReferenceEditorAction event) {
        Reference reference = ReferenceFactory.newGeneric();
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onReferenceEditorActionEdit(ReferenceEditorAction event) {
        TransactionStatus tx = getRepo().startTransaction(false);
        Reference reference = getRepo().getReferenceService().find(event.getEntityId());
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
        popup.withDeleteButton(true);
        getRepo().commitTransaction(tx);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onRegistrationEditorAction(RegistrationEditorAction event) {
        TransactionStatus tx = getRepo().startTransaction(false);
        Registration registration = getRepo().getRegistrationService().find(event.getEntityId());
        RegistrationPopupEditor popup = getNavigationManager().showInPopup(RegistrationPopupEditor.class);
        popup.showInEditor(registration);
        getRepo().commitTransaction(tx);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onTaxonNameEditorAction(TaxonNameEditorAction event) {
        TransactionStatus tx = getRepo().startTransaction(false);
        // FIXME optional:
        // A) allow full initialization of the entity here, the Presenter of the Editor should
        //    provide the intiStrategy via a static method so that it can be used
        // B) only pass the identifier (Object) to the View and the Presenter is responsible for
        //    loading and initializing  the entity
        TaxonName taxonName = getRepo().getNameService().find(event.getEntityId());
        TaxonNamePopupEditor popup = getNavigationManager().showInPopup(TaxonNamePopupEditor.class);
        popup.showInEditor(taxonName);
        popup.withDeleteButton(true);
        // in the registration application inReferences should only edited centrally
        popup.getNomReferenceCombobox().setEnabled(false);
        getRepo().commitTransaction(tx);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).EDIT && #event.sourceComponent == null")
    public void onTypeDesignationsEditorActionEdit(TypeDesignationWorkingsetEditorAction event) {

            TransactionStatus tx = getRepo().startTransaction(false);
            Registration reg = getRepo().getRegistrationService().find(event.getRegistrationId());
            RegistrationDTO regDTO = new RegistrationDTO(reg);
            TypeDesignationWorkingSet typeDesignationWorkingSet = regDTO.getTypeDesignationWorkingSet(event.getEntityId());
            if(typeDesignationWorkingSet.isSpecimenTypeDesigationWorkingSet()){
                // check SpecimenTypeDesignation
                SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
                popup.showInEditor(regDTO.getSpecimenTypeDesignationWorkingSetDTO(typeDesignationWorkingSet.getBaseEntityReference()));
            } else {
                // FIXME implement NameTypeDesignationWorkingsetPopupEditor
            }
            getRepo().commitTransaction(tx);
    }

    @EventListener(condition = "#event.type == T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action).ADD && #event.sourceComponent == null")
    public void onTypeDesignationsEditorActionAdd(TypeDesignationWorkingsetEditorAction event) {

        if(event.getNewEntityType().equals(SpecimenTypeDesignation.class)){
            TransactionStatus tx = getRepo().startTransaction(false);
            Registration reg = getRepo().getRegistrationService().find(event.getRegistrationId());
            RegistrationDTO regDTO = new RegistrationDTO(reg);
            SpecimenTypeDesignationWorkingsetPopupEditor popup = getNavigationManager().showInPopup(SpecimenTypeDesignationWorkingsetPopupEditor.class);
            popup.showInEditor(null);
            popup.withDeleteButton(true);
            getRepo().commitTransaction(tx);
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
        presentWorkingSet(workingset.getCitationId());
    }

}
