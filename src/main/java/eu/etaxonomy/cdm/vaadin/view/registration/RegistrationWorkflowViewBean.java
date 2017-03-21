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

import eu.etaxonomy.cdm.vaadin.component.registration.WorkflowSteps;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationType;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationWorkflowPresenter;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorkflowViewBean.NAME)
public class RegistrationWorkflowViewBean extends AbstractPageView<RegistrationWorkflowPresenter>
    implements RegistrationWorkflowView, View {

    /**
     *
     */
    private static final String CSS_CLASS_WORKFLOW = "workflow-container";

    private static final String SUBHEADER_PREFIX = "Advance step by step through the registration workflow for scientific names.";

    private static final String HEADER_PREFIX = "Registration of the ...";

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workflow";

    public static final String ACTION_NEW = "new";

    public static final String ACTION_EDIT = "edit";

    public RegistrationType regType = null;

    CssLayout workflow;

    String headerSuffix = "";

    public RegistrationWorkflowViewBean() {
        super();

        workflow = new CssLayout();
        workflow.setSizeFull();
        workflow.setId(CSS_CLASS_WORKFLOW);
        getLayout().addComponent(workflow);
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
               headerSuffix = regType.name() + " ...";
               eventBus.publishEvent(new RegistrationWorkflowEvent(regType));

           } else if( params[0].equals(ACTION_EDIT)) {
               headerSuffix = params[1];
               eventBus.publishEvent(new RegistrationWorkflowEvent(Integer.parseInt(params[1])));
           }
           updateHeader();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void openNameEditor(UUID nameUuid) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getHeaderText() {
        return HEADER_PREFIX + " " + headerSuffix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSubHeaderText() {
        return SUBHEADER_PREFIX;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CssLayout getWorkflow() {
        return workflow;
    }


}
