/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import static eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles.LABEL_NOWRAP;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractField;
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

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.dto.RegistrationType;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.ref.EntityReference;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons.TypeDesignationWorkingSetButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemsPanel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusFieldInstantiator;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusLabel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;
import eu.etaxonomy.vaadin.event.EditorActionType;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=RegistrationWorksetViewBean.NAME)
public class RegistrationWorksetViewBean extends AbstractPageView<RegistrationWorkingsetPresenter>
    implements RegistrationWorkingsetView, View, AccessRestrictedView {


    private static final int COL_INDEX_STATE_LABEL = 0;

    private static final int COL_INDEX_REG_ITEM = 1;

    private static final int COL_INDEX_BUTTON_GROUP = 2;

    public static final String DOM_ID_WORKINGSET = "workingset";

    public static final String TEXT_NAME_TYPIFICATION = "covering the name and typifications";
    public static final String TEXT_TYPIFICATION_ONLY = "covering typifications only";

    private static final long serialVersionUID = -213040114015958970L;

    public static final String NAME = "workingset";

    public RegistrationType regType = null;

    private List<CssLayout> registrations = new ArrayList<>();

    private String headerText = "Registration Workingset Editor";
    private String subheaderText = "";

    private UUID citationUuid;

    private Button addNewNameRegistrationButton;

    private LazyComboBox<TaxonName> existingNameCombobox;

    private GridLayout registrationsGrid;

    private Button addExistingNameButton;

    private Label existingNameRegistrationTypeLabel;

    private RegistrationItem workingsetHeader;

    private Panel registrationListPanel;

    /**
     * uses the registrationId as key
     */
    private Map<UUID, RegistrationDetailsItem> registrationItemMap = new HashMap<>();

    /**
     * uses the registrationId as key
     */
    private Map<UUID, EntityReference> typifiedNamesMap = new HashMap<>();

    private RegistrationStatusFieldInstantiator statusFieldInstantiator;

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
            this.citationUuid = UUID.fromString(event.getParameters());

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
        workingsetHeader = new RegistrationItem(workingset, this, getPresenter().getCache());
        addContentComponent(workingsetHeader, null);

        registrationListPanel = createRegistrationsList(workingset);
        registrationListPanel.setHeight("100%");
        registrationListPanel.setStyleName("registration-list");
        registrationListPanel.setCaption("Registrations");
        addContentComponent(registrationListPanel, 1.0f);

    }

    @Override
    public void setBlockingRegistrations(UUID registrationUuid, Set<RegistrationDTO> blockingRegDTOs) {

        RegistrationDetailsItem regItem = registrationItemMap.get(registrationUuid);

        boolean blockingRegAdded = false;
        for(Iterator it = regItem.itemFooter.iterator(); it.hasNext(); ){
            if(it.next() instanceof RegistrationItemsPanel){
                blockingRegAdded = true;
                break;
            }
        }
        if(!blockingRegAdded){
            regItem.itemFooter.addComponent(new RegistrationItemsPanel(this, "Blocked by", blockingRegDTOs, getPresenter().getCache()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Deprecated // no longer needed
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

        registrationItemMap.clear();
        registrationsGrid.setRows(workingset.getRegistrationDTOs().size() * 2  + 3);
        int row = 0;
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()) {
            row = putRegistrationListComponent(row, dto);
        }

        // --- Footer with UI to create new registrations ----
        Label addRegistrationLabel_1 = new Label("Add a new registration for a");
        Label addRegistrationLabel_2 = new Label("or an");

        addNewNameRegistrationButton = new Button("new name");
        addNewNameRegistrationButton.setDescription("A name which is newly published in this publication.");
        addNewNameRegistrationButton.addClickListener(
                e -> {
                    getViewEventBus().publish(this, new TaxonNameEditorAction(EditorActionType.ADD, null, addNewNameRegistrationButton, null, this));

                }
        );

        existingNameRegistrationTypeLabel = new Label();
        addExistingNameButton = new Button("existing name:");
        addExistingNameButton.setEnabled(false);
        addExistingNameButton.addClickListener(
                e -> getViewEventBus().publish(this, new RegistrationWorkingsetAction(
                        citationUuid,
                        RegistrationWorkingsetAction.Action.start
                )
             )
        );

        existingNameCombobox = new LazyComboBox<TaxonName>(TaxonName.class);
        existingNameCombobox.addValueChangeListener(
                e -> {
                    boolean selectionNotEmpty = e.getProperty().getValue() != null;
                    addExistingNameButton.setEnabled(false);
                    existingNameRegistrationTypeLabel.setValue(null);
                    if(selectionNotEmpty){
                        TaxonName name = (TaxonName)e.getProperty().getValue();
                        if(getPresenter().canCreateNameRegistrationFor(name)){
                            existingNameRegistrationTypeLabel.setValue(TEXT_NAME_TYPIFICATION);
                            addExistingNameButton.setEnabled(true);
                        } else {
                            if(!getPresenter().checkWokingsetContainsProtologe(name)){
                                existingNameRegistrationTypeLabel.setValue(TEXT_TYPIFICATION_ONLY);
                                addExistingNameButton.setEnabled(true);
                            }
                        }
                    } else {
                        existingNameRegistrationTypeLabel.setValue(null);
                    }
                }
                );

        HorizontalLayout buttonContainer = new HorizontalLayout(
                addRegistrationLabel_1,
                addNewNameRegistrationButton,
                addRegistrationLabel_2,
                addExistingNameButton,
                existingNameCombobox,
                existingNameRegistrationTypeLabel
                );
        buttonContainer.setSpacing(true);
//        buttonContainer.setWidth(100, Unit.PERCENTAGE);
        buttonContainer.setComponentAlignment(addRegistrationLabel_1, Alignment.MIDDLE_LEFT);
        buttonContainer.setComponentAlignment(addRegistrationLabel_2, Alignment.MIDDLE_LEFT);

        row++;
        registrationsGrid.addComponent(buttonContainer, 0, row, COL_INDEX_BUTTON_GROUP, row);
        registrationsGrid.setComponentAlignment(buttonContainer, Alignment.MIDDLE_RIGHT);

        row++;
        Label hint = new Label(
                "For most names that already exist in the system, it is only possible to create a registration covering type designations. "
                + "In all other cases please choose <a href=\"registration#!regStart\">\"New\"</a> from the main menu and start a registration for the nomenclatural reference of the name to be registered.",
                ContentMode.HTML);
        registrationsGrid.addComponent(hint, 0, row, COL_INDEX_BUTTON_GROUP, row);
        registrationsGrid.setComponentAlignment(hint, Alignment.MIDDLE_RIGHT);

        Panel namesTypesPanel = new Panel(registrationsGrid);
        namesTypesPanel.setStyleName(EditValoTheme.PANEL_CONTENT_PADDING_LEFT);
        return namesTypesPanel;
    }


    protected int putRegistrationListComponent(int row, RegistrationDTO dto) {

        EntityReference typifiedNameReference = dto.getTypifiedNameRef();
        if(typifiedNameReference == null){
            typifiedNameReference = dto.getNameRef();
        }
        typifiedNamesMap.put(dto.getUuid(), typifiedNameReference);

        RegistrationItemNameAndTypeButtons regItemButtonGroup = new RegistrationItemNameAndTypeButtons(dto);
        UUID registrationEntityUuid = dto.getUuid();

        RegistrationItemButtons regItemButtons = new RegistrationItemButtons();

        CssLayout footer = new CssLayout();
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setStyleName("item-footer");

        RegistrationDetailsItem regDetailsItem = new RegistrationDetailsItem(regItemButtonGroup, regItemButtons, footer);
        registrationItemMap.put(registrationEntityUuid, regDetailsItem);

        Stack<EditorActionContext> context = new Stack<EditorActionContext>();
        context.push(new EditorActionContext(
                    new TypedEntityReference<>(Registration.class, registrationEntityUuid),
                    this)
                    );

        if(regItemButtonGroup.getNameButton() != null){
            regItemButtonGroup.getNameButton().getButton().addClickListener(e -> {
                UUID nameuUuid = regItemButtonGroup.getNameButton().getUuid();
                getViewEventBus().publish(this, new TaxonNameEditorAction(
                    EditorActionType.EDIT,
                    nameuUuid,
                    e.getButton(),
                    null,
                    this,
                    context
                    )
                );
            });
        }

        for(TypeDesignationWorkingSetButton workingsetButton : regItemButtonGroup.getTypeDesignationButtons()){
            workingsetButton.getButton().addClickListener(e -> {
                TypedEntityReference baseEntityRef = workingsetButton.getBaseEntity();
                EntityReference typifiedNameRef = typifiedNamesMap.get(registrationEntityUuid);
                TypeDesignationWorkingSetType workingsetType = workingsetButton.getType();
                getViewEventBus().publish(this, new TypeDesignationWorkingsetEditorAction(
                        baseEntityRef,
                        workingsetType,
                        registrationEntityUuid,
                        typifiedNameRef.getUuid(),
                        e.getButton(),
                        null,
                        this,
                        context
                        )
                    );
            });
        }

        regItemButtonGroup.getAddTypeDesignationButton().addClickListener(
                e -> chooseNewTypeRegistrationWorkingset(dto.getUuid())
                );


        Button blockingRegistrationButton = regItemButtons.getBlockingRegistrationButton();
        blockingRegistrationButton.setStyleName(ValoTheme.BUTTON_TINY);
        blockingRegistrationButton.setDescription("No blocking registrations");
        if(dto.isBlocked()){
            blockingRegistrationButton.setEnabled(true);
            blockingRegistrationButton.setDescription("This registration is currently blocked by other registrations");
            blockingRegistrationButton.addStyleName(EditValoTheme.BUTTON_HIGHLITE);
            blockingRegistrationButton.addClickListener(e -> getViewEventBus().publish(
                    this,
                    new ShowDetailsEvent<RegistrationDTO, UUID>(
                            e,
                            RegistrationDTO.class,
                            dto.getUuid(),
                            RegistrationItem.BLOCKED_BY
                            )
                    ));
        }

        Button validationProblemsButton = regItemButtons.getValidationProblemsButton();
        validationProblemsButton.setStyleName(ValoTheme.BUTTON_TINY); //  + " " + RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);

        if(!dto.getValidationProblems().isEmpty()){
            validationProblemsButton.setEnabled(true);
            validationProblemsButton.addClickListener(e -> getViewEventBus().publish(this,
                    new ShowDetailsEvent<RegistrationDTO, UUID>(
                        e,
                        RegistrationDTO.class,
                        dto.getUuid(),
                        RegistrationItem.VALIDATION_PROBLEMS
                        )
                    )
                );
        }
        validationProblemsButton.setCaption("<span class=\"" + RegistrationStyles.BUTTON_BADGE +"\"> " + dto.getValidationProblems().size() + "</span>");
        validationProblemsButton.setCaptionAsHtml(true);

        Button messageButton = regItemButtons.getMessagesButton();
        messageButton.addClickListener(e -> getViewEventBus().publish(this,
                    new ShowDetailsEvent<RegistrationDTO, UUID>(
                        e,
                        RegistrationDTO.class,
                        dto.getUuid(),
                        RegistrationItem.MESSAGES
                        )
                    )
                );
        messageButton.setStyleName(ValoTheme.BUTTON_TINY);

        Component statusComponent;
        if(statusFieldInstantiator != null){
            AbstractField<Object> statusField = statusFieldInstantiator.create(dto);
            statusField.setValue(dto.getStatus());
            statusComponent = statusField;
        } else {
            statusComponent = new RegistrationStatusLabel().update(dto.getStatus());
        }
        Label submitterLabel = new Label(dto.getSubmitterUserName());
        submitterLabel.setStyleName(LABEL_NOWRAP + " submitter");
        submitterLabel.setIcon(FontAwesome.USER);
        submitterLabel.setContentMode(ContentMode.HTML);
        CssLayout stateAndSubmitter = new CssLayout(statusComponent, submitterLabel);


        if(UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION)) || UserHelperAccess.userHelper().userIsAdmin()) {
            Button editRegistrationButton = new Button(FontAwesome.COG);
            editRegistrationButton.setStyleName(ValoTheme.BUTTON_TINY);
            editRegistrationButton.setDescription("Edit registration");
            editRegistrationButton.addClickListener(e -> getViewEventBus().publish(this, new RegistrationEditorAction(
                EditorActionType.EDIT,
                dto.getUuid(),
                e.getButton(),
                null,
                this
                )));
            regItemButtons.addComponent(editRegistrationButton);
        }

        PermissionDebugUtils.addGainPerEntityPermissionButton(regItemButtons, Registration.class, dto.getUuid(),
                EnumSet.of(CRUD.UPDATE), RegistrationStatus.PREPARATION.name());

        row++;
        registrationsGrid.addComponent(stateAndSubmitter, COL_INDEX_STATE_LABEL, row);
        // registrationsGrid.setComponentAlignment(stateLabel, Alignment.TOP_LEFT);
        registrationsGrid.addComponent(regItemButtonGroup, COL_INDEX_REG_ITEM, row);
        registrationsGrid.addComponent(regItemButtons, COL_INDEX_BUTTON_GROUP, row);
        registrationsGrid.setComponentAlignment(regItemButtons, Alignment.TOP_LEFT);

        row++;
        registrationsGrid.addComponent(footer, 0, row, COL_INDEX_BUTTON_GROUP, row);

        return row;
    }

    /**
     * @param button
     * @param registrationEntityId
     *
     */
    @Override
    public void chooseNewTypeRegistrationWorkingset(UUID registrationEntityUuid){
        Window typeDesignationTypeCooser = new Window();
        typeDesignationTypeCooser.setModal(true);
        typeDesignationTypeCooser.setResizable(false);
        typeDesignationTypeCooser.setCaption("Add new type designation");
        Label label = new Label("Please select kind of type designation to be created.");
        Button newSpecimenTypeDesignationButton = new Button("Specimen type designation",
                e -> addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType.SPECIMEN_TYPE_DESIGNATION_WORKINGSET, registrationEntityUuid, typeDesignationTypeCooser, e.getButton()));
        Button newNameTypeDesignationButton = new Button("Name type designation",
                e -> addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType.NAME_TYPE_DESIGNATION_WORKINGSET, registrationEntityUuid, typeDesignationTypeCooser, e.getButton()));

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
    protected void addNewTypeDesignationWorkingset(TypeDesignationWorkingSetType newWorkingsetType, UUID registrationEntityUuid, Window typeDesignationTypeCooser, Button sourceButton) {
        UI.getCurrent().removeWindow(typeDesignationTypeCooser);
        EntityReference typifiedNameRef = typifiedNamesMap.get(registrationEntityUuid);
        getViewEventBus().publish(this, new TypeDesignationWorkingsetEditorAction(
                newWorkingsetType,
                registrationEntityUuid,
                typifiedNameRef.getUuid(),
                sourceButton,
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
    public UUID getCitationUuid() {
        return citationUuid;
    }

    @Override
    public Map<UUID, RegistrationDetailsItem> getRegistrationItemMap(){
        return Collections.unmodifiableMap(registrationItemMap);
    }


    /**
     * @param statusFieldInstantiator the statusFieldInstantiator to set
     */
    @Override
    public void setStatusComponentInstantiator(RegistrationStatusFieldInstantiator statusComponentInstantiator) {
        this.statusFieldInstantiator = statusComponentInstantiator;
    }


}
