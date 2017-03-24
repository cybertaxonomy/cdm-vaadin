/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.registration;

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
import eu.etaxonomy.cdm.vaadin.event.ReferenceEvent;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationWorkflowView;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkflowPresenter extends AbstractPresenter<RegistrationWorkflowView> {


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

        if(workingset != null){
            Logger.getLogger(RegistrationWorkflowPresenter.class).warn("Cant start a new workflow over an existing one.");
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
            // getView().getTitle().setValue("Workflow for a " + registrationType().name());
            for(Registration reg : workingset.getRegistrations()){
                getView().makeWorflow(RegistrationType.from(reg));
            }
        }
    }

//    @EventListener(condition = "#event.eventType ==T(eu.etaxonomy.cdm.vaadin.event.EventType).ADD")
//    public void onReferenceAddEvent(ReferenceEvent event) {
//        getView().openReferenceEditor(null);
//    }

    @EventListener(condition = "#event.eventType ==T(eu.etaxonomy.cdm.vaadin.event.EventType).EDIT")
    public void onReferenceEditEvent(ReferenceEvent event) {
        getView().openReferenceEditor(null);
    }

}