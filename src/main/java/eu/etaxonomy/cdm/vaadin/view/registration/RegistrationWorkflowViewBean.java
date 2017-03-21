/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationWorkflowComponent;
import eu.etaxonomy.cdm.vaadin.component.registration.WorkflowSteps;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationType;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationWorkflowPresenter;
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

    public static final String ACTION_NEW = "new";

    public static final String ACTION_EDIT = "edit";

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

           if(params[0].equals(ACTION_NEW)) {
               regType = RegistrationType.valueOf(params[1]);
               design.getTitle().setValue(design.getTitle().getValue() + "  " + regType.name() + " ...");
               eventBus.publishEvent(new RegistrationWorkflowEvent(regType));

           } else if( params[0].equals(ACTION_EDIT)) {
               design.getTitle().setValue(design.getTitle().getValue() + "  " + params[1]);
               eventBus.publishEvent(new RegistrationWorkflowEvent(Integer.parseInt(params[1])));
           }

        }
    }

    @Override
    public void makeWorflow(RegistrationType type){
        switch (type) {
        case NAME:
            addNameWorkflow();
            break;
        case TYPIFICATION:
            addTypificationWorkflow();
            break;
        default:
            break;
        }
    }

    /**
    *
    */
   private void addNameWorkflow() {
       WorkflowSteps steps = new WorkflowSteps();
       steps.appendNewWorkflowItem(1, "Nomenclatural reference", null);
       steps.appendNewWorkflowItem(2, "Name", null);
       steps.appendNewWorkflowItem(3, "Publisher Details", null);
       steps.appendNewWorkflowItem(4, "Data curation", null);
       steps.appendNewWorkflowItem(5, "Awaiting publication", null);
       getWorkflow().addComponent(steps);
   }

   /**
   *
   */
  private void addTypificationWorkflow() {
      WorkflowSteps steps = new WorkflowSteps();
      steps.appendNewWorkflowItem(1, "Name", null);
      steps.appendNewWorkflowItem(2, "Type information", null);
      steps.appendNewWorkflowItem(3, "Publisher Details", null);
      steps.appendNewWorkflowItem(4, "Data curation", null);
      steps.appendNewWorkflowItem(5, "Awaiting publication", null);
      getWorkflow().addComponent(steps);
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
     * {@inheritDoc}
     */
    @Override
    public void openNameEditor(UUID nameUuid) {
        // TODO Auto-generated method stub

    }

}
