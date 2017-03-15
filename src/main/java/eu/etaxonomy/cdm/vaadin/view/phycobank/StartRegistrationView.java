/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.phycobank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import eu.etaxonomy.cdm.vaadin.design.phycobank.StartRegistrationDesign;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=StartRegistrationView.NAME)
public class StartRegistrationView extends StartRegistrationDesign implements View {

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "regStart";

    @Autowired
    ApplicationEventPublisher eventBus;

    public StartRegistrationView() {
        buttonName.addClickListener(e -> eventBus.publishEvent(new NavigationEvent(
                RegistrationWorkflowViewBean.NAME,
                RegistrationWorkflowViewBean.ACTION_NEW,
                RegistrationType.NAME.name()
                )));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {

    }

}
