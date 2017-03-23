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

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEvent;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
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

    private Registration registration;

    /**
     *
     */
    public RegistrationWorkflowPresenter() {
    }

    @EventListener
    protected void onRegistrationStartEvent(RegistrationWorkflowEvent e){

        if(registration != null){
            Logger.getLogger(RegistrationWorkflowPresenter.class).warn("Foiling attempt to start another registration in existing workflow");
            return;
        }

        if(e.isStart()) {
            registration = new Registration();
            registration.setName(TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES()));
            getView().setHeaderText("New " + e.getType().name().toString()+ " Registration");
        } else {
            registration = serviceMock.loadByRegistrationID(e.getRegistrationID());
            getView().setHeaderText("Registration " + registration.getIdentifier());
        }
        if(registration != null){
            // getView().getTitle().setValue("Workflow for a " + registrationType().name());
            getView().makeWorflow(registrationType());
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


    /**
     * @return
     */
    private RegistrationType registrationType() {
        return RegistrationType.from(registration);
    }



}
