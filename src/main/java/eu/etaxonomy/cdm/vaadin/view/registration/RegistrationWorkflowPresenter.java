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

import com.vaadin.server.SystemError;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceFactory;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
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
    private RegistrationService serviceMock;

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
            Registration reg = new Registration();
            reg.setName(TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES()));
            getView().setHeaderText("New " + event.getType().name().toString()+ " Registration");
            try {
                workingset.add(reg);
            } catch (RegistrationValidationException error) {
                getView().getWorkflow().setComponentError(new SystemError(error));
            }
        } else {
            try {
                workingset = serviceMock.loadWorkingSetByRegistrationID(event.getRegistrationID());
            } catch (RegistrationValidationException error) {
                getView().getWorkflow().setComponentError(new SystemError(error));
            }
            getView().setHeaderText("Registration for " + workingset.getCitation());
        }
        if(workingset != null){
            getView().setWorkingset(workingset);
            //TODO add Blocking registrations to view
        }
    }

    @EventListener(condition = "#event.eventType ==T(eu.etaxonomy.cdm.vaadin.event.EntityEventType).ADD")
    public void onReferenceAddEvent(ReferenceEvent event) {
        Reference reference = ReferenceFactory.newGeneric();
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
    }

    @EventListener(condition = "#event.eventType ==T(eu.etaxonomy.cdm.vaadin.event.EntityEventType).EDIT")
    public void onReferenceEditEvent(ReferenceEvent event) {
        Reference reference = getRepo().getReferenceService().find(event.getEntityId());
        ReferencePopupEditor popup = getNavigationManager().showInPopup(ReferencePopupEditor.class);
        popup.showInEditor(reference);
    }


    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.entityType == T(eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet)")
    public void onShowRegistrationWorkingSetMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        List<String> messages = new ArrayList<>();
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()){
            dto.getMessages().forEach(m -> messages.add(dto.getSummary() + ": " + m));
        }
        if(event.getProperty().equals("messages")){
            getView().openDetailsPopup("Messages", messages);
        }
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.entityType == T(eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO)")
    public void onShowRegistrationMessages(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        RegistrationDTO regDto = serviceMock.loadDtoById((Integer)event.getIdentifier());
        if(event.getProperty().equals("messages")){
            getView().openDetailsPopup("Messages", regDto.getMessages());
        }
    }

}
