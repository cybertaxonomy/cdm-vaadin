/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.phycobank;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;

import eu.etaxonomy.cdm.vaadin.design.phycobank.RegistrationWorkflowDesign;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorkflowView.NAME)
public class RegistrationWorkflowView extends RegistrationWorkflowDesign implements View {

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workflow";

    RegistrationType regType = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        if(event.getParameters() != null){
           String[] params = event.getParameters().split("/");
           if(params.length > 0){
               regType = RegistrationType.valueOf(params[0]);
               title.setValue(title.getValue() + "  " + regType.name() + " ...");
           }
        }

    }

}
