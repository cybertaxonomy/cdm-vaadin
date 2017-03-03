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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.vaadin.component.phycobank.RegistrationWorkflowComponent;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationWorkflowPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorkflowViewBean.NAME)
public class RegistrationWorkflowViewBean extends AbstractView<RegistrationWorkflowPresenter>
    implements RegistrationWorkflowView, View {

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workflow";

    public RegistrationType regType = null;

    RegistrationWorkflowComponent design;

    public RegistrationWorkflowViewBean() {
        design = new RegistrationWorkflowComponent();
        setCompositionRoot(design);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        if(event.getParameters() != null){
           String[] params = event.getParameters().split("/");
           if(params.length > 0){
               regType = RegistrationType.valueOf(params[0]);
               design.getTitle().setValue(design.getTitle().getValue() + "  " + regType.name() + " ...");
           }
        }

    }

    /**
     * {@inheritDoc}
     */
    @Autowired
    @Override
    protected void injectPresenter(RegistrationWorkflowPresenter presenter) {
        setPresenter(presenter);
    }

    /* ------- RegistrationWorkflowView implementation ------- */

    /**
     * @return the title
     */
    @Override
    public Label getTitle() {
        return design.getTitle();
    }

    /**
     * @return the workflow
     */
    @Override
    public CssLayout getWorkflow() {
        return design.getWorkflow();
    }

    /**
     * @return the stepIndex
     */
    @Override
    public Button getStepIndex() {
        return design.getStepIndex();
    }

    /**
     * @return the caption
     */

    @Override
    public Label getCaptionLabel() {
        return design.getCaptionLabel();
    }
}
