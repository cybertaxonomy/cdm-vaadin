/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
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

        if(e.isStart()) {
            registration = new Registration();
            registration.setName(TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES()));
        } else {
            registration = serviceMock.loadByRegistrationID(e.getRegistrationID());
        }
        if(registration != null){
            getView().getTitle().setValue("Workflow for a " + registrationType().name());
            getView().makeWorflow(registrationType());
        }
    }

    /**
     * @return
     */
    private RegistrationType registrationType() {
        return RegistrationType.from(registration);
    }



}
