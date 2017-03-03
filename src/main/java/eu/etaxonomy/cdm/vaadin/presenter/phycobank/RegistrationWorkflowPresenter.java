/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import org.springframework.context.event.EventListener;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.vaadin.event.phycobank.RegistrationStartEvent;
import eu.etaxonomy.cdm.vaadin.view.phycobank.RegistrationWorkflowView;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class RegistrationWorkflowPresenter extends AbstractPresenter<RegistrationWorkflowView> {

    private RegistrationType registrationType = null;

    /**
     *
     */
    public RegistrationWorkflowPresenter() {
    }

    @EventListener
    protected void onRegistrationStartEvent(RegistrationStartEvent e){
        this.registrationType = e.getType();
        getView().getTitle().setValue(getView().getTitle().getValue() + " " + registrationType.name());

    }

}
