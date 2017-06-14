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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.GenericFontIcon;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemEditButtonGroup;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemEditButtonGroup.IdSetButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;
import eu.etaxonomy.cdm.vaadin.component.registration.TypeStateLabel;
import eu.etaxonomy.cdm.vaadin.component.registration.WorkflowSteps;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkflowEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.model.registration.WorkflowStep;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorkflowViewBean.NAME)
public class RegistrationWorkflowViewBean extends AbstractPageView<RegistrationWorkflowPresenter>
    implements RegistrationWorkflowView, View, AccessRestrictedView {


    public static final String DOM_ID_WORKFLOW = "workflow-container";

    public static final String DOM_ID_WORKINGSET = "workingset";

    public static final String SUBHEADER_DEEFAULT = "Advance step by step through the registration workflow.";

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workflow";

    public static final String ACTION_NEW = "new";

    public static final String ACTION_EDIT = "edit";

    private static final boolean REG_ITEM_AS_BUTTON_GROUP = true;

    public RegistrationType regType = null;

    private CssLayout workflow;

    private List<CssLayout> registrations = new ArrayList<>();

    private String headerText = "-- empty --";
    private String subheaderText = SUBHEADER_DEEFAULT;

    private boolean addNameAndTypeEditButtons = false;


    public RegistrationWorkflowViewBean() {
        super();
    }

    @Override
    protected void initContent() {
        workflow = new CssLayout();
        workflow.setSizeFull();
        workflow.setId(DOM_ID_WORKFLOW);
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
        registration.setId(DOM_ID_WORKINGSET);
        registration.setWidth(100, Unit.PERCENTAGE);

        Panel registrationListPanel = createRegistrationsList(workingset);
        registrationListPanel.setStyleName("registration-list");
        registrationListPanel.setCaption("Registrations");


        // registration.addComponent(createWorkflowTabSheet(workingset, null));
        RegistrationItem registrationItem = new RegistrationItem(workingset, this);
        if(UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin()){
            registrationItem.getSubmitterLabel().setVisible(true);
        };
        registration.addComponent(registrationItem);
        registration.addComponent(registrationListPanel);

        registrations.add(registration);
        workflow.removeAllComponents();
        workflow.addComponent(registration);
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

    private Component createWorkflowTabSheet(RegistrationWorkingSet workingset, Component namesTypesPanel){

        if(namesTypesPanel == null){
            namesTypesPanel = new CssLayout();
        }
        Component citationComponent = new CssLayout(); // new Label(workingset.getCitation());
        Component curationComponent = new CssLayout(); // new Label("Curation in progress ...")
        Component releaseComponent = new CssLayout(); // new Label("Not yet published")

        GenericFontIcon tabIcon = new GenericFontIcon("IcoMoon", 0xe900);
        TabSheet tabsheet = new TabSheet();
        // tabsheet.addStyleName(ValoTheme.TABSHEET_FRAMED);
        //tabsheet.addStyleName(ValoTheme.TABSHEET_PADDED_TABBAR);
        tabsheet.addStyleName("workflow-tabsheet");

        Tab pubDetailsTab = tabsheet.addTab(citationComponent, WorkflowStep.PUBLICATION_DETAILS.getRepresentation(), tabIcon);
        Tab namesTypesTab = tabsheet.addTab(namesTypesPanel, WorkflowStep.NAMES_N_TYPES.getRepresentation(), tabIcon);
        Tab curationTab = tabsheet.addTab(curationComponent, WorkflowStep.CURATION.getRepresentation(), tabIcon);
        Tab awaitingPubTab = tabsheet.addTab(releaseComponent, WorkflowStep.AWAITING_PUBLICATION.getRepresentation(), tabIcon);

        pubDetailsTab.setStyleName("bg-status-" + WorkflowStep.PUBLICATION_DETAILS.name());
        namesTypesTab.setStyleName("bg-status-" + WorkflowStep.NAMES_N_TYPES.name());
        curationTab.setStyleName("bg-status-" + WorkflowStep.CURATION.name());
        awaitingPubTab.setStyleName("bg-status-" + WorkflowStep.AWAITING_PUBLICATION.name());

        return tabsheet;
    }

    /**
     * @param workingset
     * @return
     */
    public Panel createRegistrationsList(RegistrationWorkingSet workingset) {
        // prepare name and type list
        GridLayout namesTypesList = new GridLayout(3, workingset.getRegistrationDTOs().size());
        int row = 0;
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()) {
            registrationListComponent(namesTypesList, row++, dto);
        }

        namesTypesList.setSizeUndefined();
        namesTypesList.setWidth(100, Unit.PERCENTAGE);
        namesTypesList.setColumnExpandRatio(0, 0.1f);
        namesTypesList.setColumnExpandRatio(1, 0.9f);
        Panel namesTypesPanel = new Panel(namesTypesList);
        namesTypesPanel.setHeight("300px");
        return namesTypesPanel;
    }

    /**
     * @param namesTypesList
     * @param row
     * @param dto
     */
    protected void registrationListComponent(GridLayout namesTypesList, int row, RegistrationDTO dto) {
        CssLayout buttonGroup = new CssLayout();
        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        Button messageButton = new Button(FontAwesome.COMMENT);
        messageButton.setStyleName(ValoTheme.BUTTON_TINY); //  + " " + RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);
        if(dto.getMessages().isEmpty()){
            messageButton.setEnabled(false);
        } else {
            messageButton.addClickListener(e -> eventBus.publishEvent(
                    new ShowDetailsEvent<RegistrationDTO, Integer>(
                        e,
                        RegistrationDTO.class,
                        dto.getId(),
                        "messages"
                        )
                    )
                );
        }
        messageButton.setCaption("<span class=\"" + RegistrationStyles.BUTTON_BADGE +"\"> " + dto.getMessages().size() + "</span>");
        messageButton.setCaptionAsHtml(true);
        buttonGroup.addComponent(messageButton);

        TypeStateLabel typeStateLabel = new TypeStateLabel().update(dto.getRegistrationType(), dto.getStatus());


        if(UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin()) {
            Button editRegistrationButton = new Button(FontAwesome.COG);
            editRegistrationButton.setStyleName(ValoTheme.BUTTON_TINY);
            editRegistrationButton.setDescription("Edit registration");
            editRegistrationButton.addClickListener(e -> getEventBus().publishEvent(new RegistrationEditorAction(
                AbstractEditorAction.Action.EDIT,
                dto.getId(),
                null,
                this
                )));
            buttonGroup.addComponent(editRegistrationButton);
        }

        if(addNameAndTypeEditButtons ){
            Button editNameButton = new Button(FontAwesome.TAG);
            editNameButton.setStyleName(ValoTheme.BUTTON_TINY);
            editNameButton.setDescription("Edit name");
            if(dto.getName() != null){
                editNameButton.addClickListener(e -> {
                        Integer nameId = dto.getName().getId();
                        getEventBus().publishEvent(new TaxonNameEditorAction(
                            AbstractEditorAction.Action.EDIT,
                            nameId
                            )
                        );
                    });
            } else {
                editNameButton.setEnabled(false);
            }

            Button editTypesButton = new Button(FontAwesome.LEAF);
            editTypesButton.setStyleName(ValoTheme.BUTTON_TINY);
            editTypesButton.setDescription("Edit type designations");
            if(dto.getOrderdTypeDesignationWorkingSets() != null && !dto.getOrderdTypeDesignationWorkingSets().isEmpty()){
//                editTypesButton.addClickListener(e -> {
//                    int regId = dto.getId();
//                        getEventBus().publishEvent(new TypeDesignationSetEditorAction(
//                            AbstractEditorAction.Action.EDIT,
//                            regId
//                            )
//                        );
//                    });
            } else {
                editTypesButton.setEnabled(false);
            }
            buttonGroup.addComponents(editNameButton, editTypesButton);
        }

        Component regItem;
        if(REG_ITEM_AS_BUTTON_GROUP){
            RegistrationItemEditButtonGroup editButtonGroup = new RegistrationItemEditButtonGroup(dto);
            if(editButtonGroup.getNameButton() != null){

                editButtonGroup.getNameButton().getButton().addClickListener(e -> {
                    Integer nameId = editButtonGroup.getNameButton().getId();
                    getEventBus().publishEvent(new TaxonNameEditorAction(
                        AbstractEditorAction.Action.EDIT,
                        nameId,
                        null, //e.getButton(),
                        this
                        )
                    );
                });

                for(IdSetButton idSetButton : editButtonGroup.getTypeDesignationButtons()){
                    idSetButton.getButton().addClickListener(e -> {
                        Set<Integer> ids = idSetButton.getIds();
                        getEventBus().publishEvent(new TypeDesignationWorkingsetEditorAction(
                                AbstractEditorAction.Action.EDIT,
                                ids,
                                null, //e.getButton(),
                                this
                                )
                            );
                    });
                }

                editButtonGroup.getAddTypeDesignationButton().addClickListener(
                        e -> chooseNewTypeRegistrationWorkingset(e.getButton())
                        );
            }
            regItem = editButtonGroup;
        } else {
            regItem = new Label(dto.getSummary());
        }

        namesTypesList.addComponent(typeStateLabel, 0, row);
        namesTypesList.setComponentAlignment(typeStateLabel, Alignment.TOP_LEFT);
        namesTypesList.addComponent(regItem, 1, row);
        namesTypesList.addComponent(buttonGroup, 2, row);
        namesTypesList.setComponentAlignment(buttonGroup, Alignment.TOP_LEFT);
    }

    /**
     * @param button
     *
     */
    protected void chooseNewTypeRegistrationWorkingset(Button button) {

        Window typeDesignationTypeCooser = new Window();
        typeDesignationTypeCooser.setModal(true);
        typeDesignationTypeCooser.setResizable(false);
        typeDesignationTypeCooser.setCaption("Add new type designation");
        Label label = new Label("Please select kind of type designation to be created.");
        Button newSpecimenTypeDesignationButton = new Button("Specimen type designation",
                e -> addNewTypeRegistrationWorkingset(SpecimenTypeDesignation.class, typeDesignationTypeCooser));
        Button newNameTypeDesignationButton = new Button("Name type designation",
                e -> addNewTypeRegistrationWorkingset(NameTypeDesignation.class, typeDesignationTypeCooser));
        newNameTypeDesignationButton.setEnabled(false);

        VerticalLayout layout = new VerticalLayout(label, newSpecimenTypeDesignationButton, newNameTypeDesignationButton);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setComponentAlignment(newSpecimenTypeDesignationButton, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(newNameTypeDesignationButton, Alignment.MIDDLE_CENTER);
        typeDesignationTypeCooser.setContent(layout);
        UI.getCurrent().addWindow(typeDesignationTypeCooser);

    }

    /**
     * @param button
     *
     */
    protected void addNewTypeRegistrationWorkingset(Class<? extends TypeDesignationBase<?>> newEntityType, Window typeDesignationTypeCooser) {
        UI.getCurrent().removeWindow(typeDesignationTypeCooser);
        getEventBus().publishEvent(new TypeDesignationWorkingsetEditorAction(
                AbstractEditorAction.Action.ADD,
                newEntityType,
                null,
                this
                ));
    }


    /**
    *
    */
   private void addBulletWorkflowName() {
       WorkflowSteps steps = new WorkflowSteps();
       steps.appendNewWorkflowItem(1, "Publication details including the publisher.",
               e -> eventBus.publishEvent(new ReferenceEditorAction(Action.EDIT)));
       steps.appendNewWorkflowItem(2, "One or multiple published scientific new names.",
               e -> eventBus.publishEvent(new TaxonNameEditorAction(Action.EDIT)));
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
              e -> eventBus.publishEvent(new ReferenceEditorAction(Action.EDIT)));
      steps.appendNewWorkflowItem(2, "One or multiple published typifications.",
              e -> eventBus.publishEvent(new TaxonNameEditorAction(Action.EDIT)));
      steps.appendNewWorkflowItem(3, "Request for data curation and await approval.", null);
      steps.appendNewWorkflowItem(4, "Awaiting publication", null);
      getWorkflow().addComponent(steps);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void openDetailsPopup(String caption, List<String> messages) {
        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"details-popup-content\">");
        messages.forEach(s -> sb.append(s).append("</br>"));
        sb.append("</div>");
        new Notification(caption, sb.toString(), Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }

}
