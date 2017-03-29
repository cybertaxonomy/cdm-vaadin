/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.GenericFontIcon;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.component.registration.WorkflowSteps;
import eu.etaxonomy.cdm.vaadin.event.EntityEventType;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEvent;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.model.registration.WorkflowStep;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO;
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


    public static final String CSS_CLASS_WORKFLOW = "workflow-container";

    public static final String SUBHEADER_DEEFAULT = "Advance step by step through the registration workflow.";

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workflow";

    public static final String ACTION_NEW = "new";

    public static final String ACTION_EDIT = "edit";

    public RegistrationType regType = null;

    private CssLayout workflow;

    private List<CssLayout> registrations = new ArrayList<>();

    private String headerText = "-- empty --";
    private String subheaderText = SUBHEADER_DEEFAULT;


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
               headerText = regType.name() + " ...";
               eventBus.publishEvent(new RegistrationWorkflowEvent(regType));

           } else if( params[0].equals(ACTION_EDIT)) {
               headerText = params[1];
               eventBus.publishEvent(new RegistrationWorkflowEvent(Integer.parseInt(params[1])));
           }
           updateHeader();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorkingset(RegistrationWorkingSet workingset) {

        CssLayout registration = new CssLayout();
        registration.setWidth(100, Unit.PERCENTAGE);
        registration.addComponent(createRegistrationCaption(workingset));
        registrations.add(registration);
        workflow.addComponent(registration);

    }

    /**
     * @param workingset
     * @return
     */
    private Component createRegistrationCaption(RegistrationWorkingSet workingset) {
        String citation = workingset.getCitation();
        int messagesCount = workingset.messagesCount();
        RegistrationStatus status = workingset.lowestStatus();

        return createRegistrationCaption(citation, status, messagesCount);
    }

    /**
     * @param workingset
     * @return
     */
    private Component createRegistrationCaption(RegistrationDTO regitrationDto) {
        String citation = regitrationDto.getCitation();
        int messagesCount = regitrationDto.getMessages().size();
        RegistrationStatus status = regitrationDto.getStatus();

        return createRegistrationCaption(citation, status, messagesCount);
    }

    /**
     * @param workingset
     * @return
     */
    private Component createRegistrationCaption(String citation, RegistrationStatus status, int messagesCount){

        GridLayout grid = new GridLayout(2, 1);
        grid.addStyleName("registration-workflow-item");
        grid.setWidth(100, Unit.PERCENTAGE);
        Label citationLabel = new Label(citation);
        grid.addComponent(citationLabel, 0, 0);
        grid.setColumnExpandRatio(0, 1.0f);

        CssLayout iconContainer = new CssLayout();
        iconContainer.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        if(messagesCount > 0){
            Button messagesButton = new Button(FontAwesome.COMMENT);
            messagesButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
            iconContainer.addComponent(messagesButton);
        }

        WorkflowStep step = WorkflowStep.from(status, citation != null && !citation.isEmpty());
        Label statusIndicator = new Label(WorkflowStep.from(status, citation != null && !citation.isEmpty()).getHtml());
        statusIndicator.setCaptionAsHtml(true);
        statusIndicator.setStyleName("workflow-step label-nowrap");
        statusIndicator.setIcon(new GenericFontIcon("IcoMoon", 0xe900)); // triangle-right
        CssLayout statusWrap = new CssLayout(statusIndicator);
        statusWrap.setStyleName("workflow-step-wrap bg-status-" + status.name());
        iconContainer.addComponent(statusWrap);

        grid.addComponent(iconContainer, 1, 0);
        grid.setComponentAlignment(iconContainer, Alignment.TOP_RIGHT);

        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addBlockingRegistration(RegistrationDTO blocking) {
        if(registrations == null) {
            throw new RuntimeException("A Workingset must be present prior adding blocking registrations.");
        }
        // add the blocking registration

    }

    /**
    *
    */
   private void addBulletWorkflowName() {
       WorkflowSteps steps = new WorkflowSteps();
       steps.appendNewWorkflowItem(1, "Publication details including the publisher.",
               e -> eventBus.publishEvent(new ReferenceEvent(EntityEventType.EDIT)));
       steps.appendNewWorkflowItem(2, "One or multiple published scientific new names.",
               e -> eventBus.publishEvent(new TaxonNameEvent(EntityEventType.EDIT)));
       steps.appendNewWorkflowItem(3, "Request for data curation and await approval.", null);
       steps.appendNewWorkflowItem(4, "Awaiting publication", null);
       getWorkflow().addComponent(steps);
   }

   /**
   *
   */
  private void addBulletWorkflowTypification() {
      WorkflowSteps steps = new WorkflowSteps();
      steps.appendNewWorkflowItem(1, "Publication details including the publisher.",
              e -> eventBus.publishEvent(new ReferenceEvent(EntityEventType.EDIT)));
      steps.appendNewWorkflowItem(2, "One or multiple published typifications.",
              e -> eventBus.publishEvent(new TaxonNameEvent(EntityEventType.EDIT)));
      steps.appendNewWorkflowItem(3, "Request for data curation and await approval.", null);
      steps.appendNewWorkflowItem(4, "Awaiting publication", null);
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
    public void openReferenceEditor(UUID referenceUuid) {
        // TODO Auto-generated method stub

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
        return headerText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setHeaderText(String text) {
        this.headerText = text;

    }

    /**
     * @return the subheaderText
     */
    public String getSubheaderText() {
        return subheaderText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSubheaderText(String text) {
        subheaderText = text;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getSubHeaderText() {
        return subheaderText;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CssLayout getWorkflow() {
        return workflow;
    }


}
