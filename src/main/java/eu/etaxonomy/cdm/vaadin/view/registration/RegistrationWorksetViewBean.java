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
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemEditButtonGroup;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemEditButtonGroup.TypeDesignationWorkingSetButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStateLabel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Action;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.security.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorksetViewBean.NAME)
public class RegistrationWorksetViewBean extends AbstractPageView<RegistrationWorkingsetPresenter>
    implements RegistrationWorkingsetView, View, AccessRestrictedView {

    public static final String DOM_ID_WORKINGSET = "workingset";

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workingset";

    public RegistrationType regType = null;

    private List<CssLayout> registrations = new ArrayList<>();

    private String headerText = "Registration Workingset Editor";
    private String subheaderText = "";

    private Integer citationID;

    private Button addNewNameRegistrationButton;

    private LazyComboBox<TaxonName> existingNameCombobox;

    private GridLayout registrationsGrid;

    private Button addExistingNameButton;

    private RegistrationItem workingsetHeader;

    private Panel registrationListPanel;

    public RegistrationWorksetViewBean() {
        super();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void initContent() {
        getLayout().setId(NAME);
        updateHeader();
        // all content is added in createRegistrationsList()
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        if(event.getParameters() != null){
            this.citationID = Integer.valueOf(event.getParameters());

            getPresenter().handleViewEntered();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWorkingset(RegistrationWorkingSet workingset) {

        if(workingsetHeader != null){
            getLayout().removeComponent(workingsetHeader);
            getLayout().removeComponent(registrationListPanel);
        }
        workingsetHeader = new RegistrationItem(workingset, this);
        if(UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin()){
            workingsetHeader.getSubmitterLabel().setVisible(true);
        }
        addContentComponent(workingsetHeader, null);

        registrationListPanel = createRegistrationsList(workingset);
        registrationListPanel.setHeight("100%");
        registrationListPanel.setStyleName("registration-list");
        registrationListPanel.setCaption("Registrations");
        addContentComponent(registrationListPanel, 1.0f);

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
     * @param workingset
     * @return
     */
    public Panel createRegistrationsList(RegistrationWorkingSet workingset) {

        registrationsGrid = new GridLayout(3, 1);
        registrationsGrid.setWidth("100%");
        // allow vertical scrolling:
        registrationsGrid.setHeightUndefined();

        //registrationsGrid.setColumnExpandRatio(0, 0.1f);
        registrationsGrid.setColumnExpandRatio(1, 1f);

        int row = 0;
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()) {
            putRegistrationListComponent(registrationsGrid, row++, dto);
        }

        Label addRegistrationLabel_1 = new Label("Add a new registration for a");
        Label addRegistrationLabel_2 = new Label("or an");

        addNewNameRegistrationButton = new Button("new name");
        addNewNameRegistrationButton.setDescription("A name which is newly published in this publication.");
        addNewNameRegistrationButton.addClickListener(
                e -> eventBus.publishEvent(new TaxonNameEditorAction(Action.ADD, addNewNameRegistrationButton))
                );

        addExistingNameButton = new Button("existing name:");
        addExistingNameButton.setDescription("A name which was previously published in a earlier publication.");
        addExistingNameButton.setEnabled(false);
        addExistingNameButton.addClickListener(
                e -> eventBus.publishEvent(new RegistrationWorkingsetAction(citationID, RegistrationWorkingsetAction.Action.start))
                );

        existingNameCombobox = new LazyComboBox<TaxonName>(TaxonName.class);
        existingNameCombobox.addValueChangeListener(
                e -> addExistingNameButton.setEnabled(e.getProperty().getValue() != null)
                );

        HorizontalLayout buttonContainer = new HorizontalLayout(addRegistrationLabel_1, addNewNameRegistrationButton, addRegistrationLabel_2, addExistingNameButton, existingNameCombobox);
        buttonContainer.setSpacing(true);
//        buttonContainer.setWidth(100, Unit.PERCENTAGE);
        buttonContainer.setComponentAlignment(addRegistrationLabel_1, Alignment.MIDDLE_LEFT);
        buttonContainer.setComponentAlignment(addRegistrationLabel_2, Alignment.MIDDLE_LEFT);
        registrationsGrid.addComponent(buttonContainer, 0, row, 2, row);
        registrationsGrid.setComponentAlignment(buttonContainer, Alignment.MIDDLE_RIGHT);

        Panel namesTypesPanel = new Panel(registrationsGrid);
        return namesTypesPanel;
    }


    /**
     * @param grid
     * @param row If null, the new row will be inserted as last registration item, that is before the button row.
     * @param dto
     */
    protected void putRegistrationListComponent(GridLayout grid, int row, RegistrationDTO dto) {

        grid.setRows(grid.getRows() + 1);

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

        RegistrationStateLabel stateLabel = new RegistrationStateLabel().update(dto.getStatus());


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

        PermissionDebugUtils.addGainPerEntityPermissionButton(buttonGroup, Registration.class, dto.getId(),
                EnumSet.of(CRUD.UPDATE), RegistrationStatus.PREPARATION.name());

        Component regItem;

        RegistrationItemEditButtonGroup editButtonGroup = new RegistrationItemEditButtonGroup(dto);

        if(editButtonGroup.getNameButton() != null){
            editButtonGroup.getNameButton().getButton().addClickListener(e -> {
                Integer nameId = editButtonGroup.getNameButton().getId();
                getEventBus().publishEvent(new TaxonNameEditorAction(
                    AbstractEditorAction.Action.EDIT,
                    nameId,
                    null, //e.getButton(), the listener method expects this to be null
                    this
                    )
                );
            });
        }

        for(TypeDesignationWorkingSetButton workingsetButton : editButtonGroup.getTypeDesignationButtons()){
            workingsetButton.getButton().addClickListener(e -> {
                Integer typeDesignationWorkingsetId = workingsetButton.getId();
                TypeDesignationWorkingSetType workingsetType = workingsetButton.getType();
                Integer registrationEntityID = dto.getId();
                getEventBus().publishEvent(new TypeDesignationWorkingsetEditorAction(
                        AbstractEditorAction.Action.EDIT,
                        typeDesignationWorkingsetId,
                        workingsetType,
                        registrationEntityID,
                        null, //e.getButton(), the listener method expects this to be null
                        this
                        )
                    );
            });
        }

        editButtonGroup.getAddTypeDesignationButton().addClickListener(
                e -> chooseNewTypeRegistrationWorkingset(dto.getId())
                );
        regItem = editButtonGroup;


        grid.addComponent(stateLabel, 0, row);
        grid.setComponentAlignment(stateLabel, Alignment.TOP_LEFT);
        grid.addComponent(regItem, 1, row);
        grid.addComponent(buttonGroup, 2, row);
        grid.setComponentAlignment(buttonGroup, Alignment.TOP_LEFT);
    }

    /**
     * @param button
     * @param registrationEntityId
     *
     */
    @Override
    public void chooseNewTypeRegistrationWorkingset(Integer registrationEntityId) {

        Window typeDesignationTypeCooser = new Window();
        typeDesignationTypeCooser.setModal(true);
        typeDesignationTypeCooser.setResizable(false);
        typeDesignationTypeCooser.setCaption("Add new type designation");
        Label label = new Label("Please select kind of type designation to be created.");
        Button newSpecimenTypeDesignationButton = new Button("Specimen type designation",
                e -> addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET, registrationEntityId, typeDesignationTypeCooser));
        Button newNameTypeDesignationButton = new Button("Name type designation",
                e -> addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType.NAME_TYPE_DESIGNATION_WORKINGSET, registrationEntityId, typeDesignationTypeCooser));
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
    protected void addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType newWorkingsetType, Integer registrationEntityId, Window typeDesignationTypeCooser) {
        UI.getCurrent().removeWindow(typeDesignationTypeCooser);
        getEventBus().publishEvent(new TypeDesignationWorkingsetEditorAction(
                AbstractEditorAction.Action.ADD,
                newWorkingsetType,
                registrationEntityId,
                null,
                this
                ));
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
        updateHeader();

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
        updateHeader();
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

    /**
     * @return the addNewNameRegistrationButton
     */
    @Override
    public Button getAddNewNameRegistrationButton() {
        return addNewNameRegistrationButton;
    }

    @Override
    public Button getAddExistingNameRegistrationButton() {
        return addExistingNameButton;
    }

    @Override
    public LazyComboBox<TaxonName> getAddExistingNameCombobox() {
        return existingNameCombobox;
    }

    /**
     * @return the citationID
     */
    @Override
    public Integer getCitationID() {
        return citationID;
    }


}
