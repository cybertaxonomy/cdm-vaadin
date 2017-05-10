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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
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
    @Qualifier(IRegistrationWorkingSetService.ACTIVE_IMPL)
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
            getView().setHeaderText("New " + event.getType().name().toString()+ " Registration");
            try {
                workingset.add(reg);
            } catch (RegistrationValidationException error) {
                getView().getWorkflow().setComponentError(new SystemError(error));
            }
            getView().setWorkingset(workingset);
        } else {
            Integer registrationID = event.getRegistrationID();
            presentWorkingSetByRegID(registrationID);
        }

    }

    /**
     * @param registrationID
     * @deprecated use other method working sets should only be addressed by the referenceID
     */
    @Deprecated
    private void presentWorkingSetByRegID(Integer registrationID) {
        try {
            workingset = workingSetService.loadWorkingSetByRegistrationID(registrationID);
        } catch (RegistrationValidationException error) {
            getView().getWorkflow().setComponentError(new SystemError(error));
        }
        getView().setHeaderText("Registration for " + workingset.getCitation());
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

    @EventListener(condition = "#event.type ==T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Type).ADD")
    public void onReferenceAddEvent(ReferenceEditorAction event) {
        Reference reference = ReferenceFactory.newGeneric();
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
    }

    @EventListener(condition = "#event.type ==T(eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Type).EDIT")
    public void onReferenceEditEvent(ReferenceEditorAction event) {
        Reference reference = getRepo().getReferenceService().find(event.getEntityId());
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
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
        if(event.getEntityType().isAssignableFrom(Reference.class)){
            if(workingset.getCitationId().equals(event.getEntityId())){
                presentWorkingSet(event.getEntityId());
            }
        }

    }

}
