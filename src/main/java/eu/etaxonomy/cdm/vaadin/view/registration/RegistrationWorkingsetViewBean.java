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

import eu.etaxonomy.cdm.api.dto.RegistrationDTO.RankedNameReference;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationGroup.TypeDesignationSetType;
import eu.etaxonomy.cdm.api.util.RoleProberImpl;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.ref.EntityReference;
import eu.etaxonomy.cdm.ref.TypedEntityReferenceFactory;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.BadgeButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons.TypeDesignationSetButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemsPanel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusFieldInstantiator;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusLabel;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationSetEditorAction;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;
import eu.etaxonomy.vaadin.event.EditorActionType;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 */
@SpringView(name=RegistrationWorkingsetViewBean.NAME)
public class RegistrationWorkingsetViewBean
        extends AbstractPageView<RegistrationWorkingsetView,RegistrationWorkingsetPresenter>
        implements RegistrationWorkingsetView, View, AccessRestrictedView {

    private static final long serialVersionUID = -213040114015958970L;

    private static final int COL_INDEX_STATE_LABEL = 0;

    private static final int COL_INDEX_REG_ITEM = 1;

    private static final int COL_INDEX_BUTTON_GROUP = 2;

    public static final String DOM_ID_WORKINGSET = "workingset";

    public static final String TEXT_NAME_TYPIFICATION = "covering the name and typifications";
    public static final String TEXT_TYPIFICATION_ONLY = "covering typifications only";

    public static final String NAME = "workingset";

    private String headerText = "Registration Workingset Editor";
    private String subheaderText = "";

    private UUID citationUuid;

    private GridLayout registrationsGrid;

    private RegistrationItem workingsetHeader;

    private Panel registrationListPanel;

    private Button addNewNameRegistrationButton;

    private Button addExistingNameButton;

    private LazyComboBox<TaxonName> existingNameCombobox;

    private Label existingNameRegistrationTypeLabel;

    /**
     * uses the registrationId as key
     */
    private Map<UUID, RegistrationDetailsItem> registrationItemMap = new HashMap<>();

    /**
     * uses the registrationId as key
     */
    private Map<UUID, RankedNameReference> typifiedNamesMap = new HashMap<>();

    private RegistrationStatusFieldInstantiator<RegistrationWrapperDTO> statusFieldInstantiator;

    private String accessDeniedMessage;

    public RegistrationWorkingsetViewBean() {
        super();
    }

    @Override
    protected void initContent() {
        getLayout().setId(NAME);
        updateHeader();
        // all content is added in createRegistrationsList()
    }

    @Override
    public void enter(ViewChangeEvent event) {
        if(event.getParameters() != null){
            this.citationUuid = UUID.fromString(event.getParameters());

            getPresenter().handleViewEntered();
        }
    }

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
    public void setBlockingRegistrations(UUID registrationUuid, Set<RegistrationWrapperDTO> blockingRegDTOs) {

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

    public Panel createRegistrationsList(RegistrationWorkingSet workingset) {

        registrationsGrid = new GridLayout(3, 1);
        registrationsGrid.setWidth("100%");
        // allow vertical scrolling:
        registrationsGrid.setHeightUndefined();

        //registrationsGrid.setColumnExpandRatio(0, 0.1f);
        registrationsGrid.setColumnExpandRatio(1, 1f);

        registrationItemMap.clear();
        registrationsGrid.setRows(workingset.getRegistrationWrapperDTOs().size() * 2  + 3);
        int row = 0;
        for(RegistrationWrapperDTO dto : workingset.getRegistrationWrapperDTOs()) {
            row = putRegistrationListComponent(row, dto);
        }

        // --- Footer with UI to create new registrations ----
        Label addRegistrationLabel_1 = new Label("Add a new registration for a");
        Label addRegistrationLabel_2 = new Label("or an");

        addNewNameRegistrationButton = new Button("new name");
        addNewNameRegistrationButton.setDescription("A name which is newly published in this publication.");
        Stack<EditorActionContext> context = new Stack<EditorActionContext>();
        context.push(new EditorActionContext(
                    TypedEntityReferenceFactory.fromTypeAndId(Registration.class, null),
                    this)
                    );
        addNewNameRegistrationButton.addClickListener(
                e -> {
                    getViewEventBus().publish(this, new TaxonNameEditorAction(EditorActionType.ADD, null, addNewNameRegistrationButton, null, this, context));
                }
        );

        existingNameRegistrationTypeLabel = new Label();
        addExistingNameButton = new Button("existing name:");
        addExistingNameButton.setEnabled(false);
        addExistingNameButton.addClickListener(e -> reviewExistingName());

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
                            if(!getPresenter().checkWokingsetContainsProtolog(name)){
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

    private void reviewExistingName() {
        // call commit to make the selection available
        existingNameCombobox.commit();
        TaxonName taxonName = existingNameCombobox.getValue();
        Stack<EditorActionContext> context = new Stack<>();
        context.push(new EditorActionContext(
                    TypedEntityReferenceFactory.fromEntity(taxonName),
                    this)
                    );
        getViewEventBus().publish(
                this,
                new TaxonNameEditorAction(
                        EditorActionType.EDIT,
                        taxonName.getUuid(),
                        addExistingNameButton,
                        existingNameCombobox,
                        this,
                        context)
        );
    }

    protected int putRegistrationListComponent(int row, RegistrationWrapperDTO dto) {

        RankedNameReference typifiedNameReference = dto.getTypifiedNameRef();
        if(typifiedNameReference == null){
            typifiedNameReference = dto.getNameRef();
        }
        typifiedNamesMap.put(dto.getUuid(), typifiedNameReference);
        final boolean isSupraSpecific = typifiedNameReference.isSupraGeneric();

        RegistrationItemNameAndTypeButtons regItemNameAndTypeButtons = new RegistrationItemNameAndTypeButtons(dto, getPresenter().getCache());
        UUID registrationEntityUuid = dto.getUuid();

        RegistrationItemButtons regItemButtons = new RegistrationItemButtons();

        CssLayout footer = new CssLayout();
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setStyleName("item-footer");

        RegistrationDetailsItem regDetailsItem = new RegistrationDetailsItem(regItemNameAndTypeButtons, regItemButtons, footer);
        registrationItemMap.put(registrationEntityUuid, regDetailsItem);

        Stack<EditorActionContext> context = new Stack<>();
        context.push(new EditorActionContext(
                    TypedEntityReferenceFactory.fromTypeAndId(Registration.class, registrationEntityUuid),
                    this)
                    );

        if(regItemNameAndTypeButtons.getNameButton() != null){
            regItemNameAndTypeButtons.getNameButton().getButton().addClickListener(e -> {
                UUID nameuUuid = regItemNameAndTypeButtons.getNameButton().getUuid();
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

        for(TypeDesignationSetButton workingsetButton : regItemNameAndTypeButtons.getTypeDesignationButtons()){
            workingsetButton.getButton().addClickListener(e -> {
                VersionableEntity baseEntity = workingsetButton.getBaseEntity();
                RankedNameReference typifiedNameRef = typifiedNamesMap.get(registrationEntityUuid);
                TypeDesignationSetType workingsetType = workingsetButton.getType();
                getViewEventBus().publish(this, new TypeDesignationSetEditorAction(
                        baseEntity,
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

        regItemNameAndTypeButtons.getAddTypeDesignationButton().addClickListener(e -> {
                    TypeDesignationSetType type = isSupraSpecific ?
                            TypeDesignationSetType.NAME_TYPE_DESIGNATION_SET : TypeDesignationSetType.SPECIMEN_TYPE_DESIGNATION_SET;
                    addNewTypeDesignationSet(type, registrationEntityUuid, null, e.getButton());
//                  chooseNewTypeRegistrationWorkingset(dto.getUuid(), null);
                }
            );

        Button blockingRegistrationButton = regItemButtons.getBlockingRegistrationButton();
        blockingRegistrationButton.setStyleName(ValoTheme.BUTTON_TINY);
        blockingRegistrationButton.setDescription("No blocking registrations");
        if(dto.isBlocked()){
            blockingRegistrationButton.setEnabled(true);
            blockingRegistrationButton.setDescription("This registration is currently blocked by other registrations");
            blockingRegistrationButton.addStyleName(EditValoTheme.BUTTON_HIGHLIGHT);
            blockingRegistrationButton.addClickListener(e -> getViewEventBus().publish(
                    this,
                    new ShowDetailsEvent<RegistrationWrapperDTO, UUID>(
                            e,
                            RegistrationWrapperDTO.class,
                            dto.getUuid(),
                            RegistrationItem.BLOCKED_BY
                            )
                    ));
        }

        BadgeButton validationProblemsButton = regItemButtons.getValidationProblemsButton();
        validationProblemsButton.setStyleName(ValoTheme.BUTTON_TINY); //  + " " + RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);

        if(!dto.getValidationProblems().isEmpty()){
            validationProblemsButton.setEnabled(true);
            validationProblemsButton.addClickListener(e -> getViewEventBus().publish(this,
                    new ShowDetailsEvent<RegistrationWrapperDTO, UUID>(
                        e,
                        RegistrationWrapperDTO.class,
                        dto.getUuid(),
                        RegistrationItem.VALIDATION_PROBLEMS
                        )
                    )
                );
        }
        int problemCount = dto.getValidationProblems().size();
        validationProblemsButton.setCaption(problemCount > 0 ? Integer.toString(problemCount) : null);

        Component statusComponent;
        if(statusFieldInstantiator != null){
            statusComponent = statusFieldInstantiator.create(dto);
        } else {
            statusComponent = new RegistrationStatusLabel().update(dto.getStatus());
        }
        Label submitterLabel = new Label(dto.getSubmitterUserName());
        submitterLabel.setStyleName(LABEL_NOWRAP + " submitter");
        submitterLabel.setIcon(FontAwesome.USER);
        submitterLabel.setContentMode(ContentMode.HTML);
        CssLayout stateAndSubmitter = new CssLayout(statusComponent, submitterLabel);


        if(UserHelperAccess.userHelper().userIs(new RoleProberImpl(RolesAndPermissions.ROLE_CURATION)) || UserHelperAccess.userHelper().userIsAdmin()) {

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

            Button unlockButton = new Button(FontAwesome.LOCK);
            unlockButton.setStyleName(ValoTheme.BUTTON_TINY);
            unlockButton.setDescription("Unlock");
            unlockButton.addClickListener(e -> {
                regItemNameAndTypeButtons.setLockOverride(!regItemNameAndTypeButtons.isLockOverride());
                if(regItemNameAndTypeButtons.isRegistrationLocked()){
                    unlockButton.setIcon(regItemNameAndTypeButtons.isLockOverride() ? FontAwesome.UNLOCK_ALT : FontAwesome.LOCK);
                    unlockButton.setDescription(regItemNameAndTypeButtons.isLockOverride() ? "Click to unlock editing" : "Click to lock editing");
                }
            });
            unlockButton.setEnabled(regItemNameAndTypeButtons.isRegistrationLocked());
            regItemButtons.addComponents(unlockButton, editRegistrationButton);
        }

        PermissionDebugUtils.addGainPerEntityPermissionButton(regItemButtons, Registration.class, dto.getUuid(),
                EnumSet.of(CRUD.UPDATE), RegistrationStatus.PREPARATION.name());

        row++;
        registrationsGrid.addComponent(stateAndSubmitter, COL_INDEX_STATE_LABEL, row);
        // registrationsGrid.setComponentAlignment(stateLabel, Alignment.TOP_LEFT);
        registrationsGrid.addComponent(regItemNameAndTypeButtons, COL_INDEX_REG_ITEM, row);
        registrationsGrid.addComponent(regItemButtons, COL_INDEX_BUTTON_GROUP, row);
        registrationsGrid.setComponentAlignment(regItemButtons, Alignment.TOP_LEFT);

        row++;
        registrationsGrid.addComponent(footer, 0, row, COL_INDEX_BUTTON_GROUP, row);

        return row;
    }

    //originally used in regItemButtonGroup.getAddTypeDesignationButton().addClickListener()
    //to allow selecting the type designation type
    //now selection is automated by selecting from rank class
    //TODO can be deleted if the above works as expected #10261
    // also remove typeDesignationTypeChooser parameter in addNewTypeDesignationSet
    @Deprecated
    private void chooseNewTypeRegistrationWorkingset(UUID registrationEntityUuid, UUID nameUuid){
        Window typeDesignationTypeCooser = new Window();
        typeDesignationTypeCooser.setModal(true);
        typeDesignationTypeCooser.setResizable(false);
        typeDesignationTypeCooser.setCaption("Add new type designation");
        Label label = new Label("Please select kind of type designation to be created.");
        Button newSpecimenTypeDesignationButton = new Button("Specimen type designation",
                e -> addNewTypeDesignationSet(TypeDesignationSetType.SPECIMEN_TYPE_DESIGNATION_SET, registrationEntityUuid, typeDesignationTypeCooser, e.getButton()));
        Button newNameTypeDesignationButton = new Button("Name type designation",
                e -> addNewTypeDesignationSet(TypeDesignationSetType.NAME_TYPE_DESIGNATION_SET, registrationEntityUuid, typeDesignationTypeCooser, e.getButton()));

        VerticalLayout layout = new VerticalLayout(label, newSpecimenTypeDesignationButton, newNameTypeDesignationButton);
        layout.setMargin(true);
        layout.setSpacing(true);
        layout.setComponentAlignment(newSpecimenTypeDesignationButton, Alignment.MIDDLE_CENTER);
        layout.setComponentAlignment(newNameTypeDesignationButton, Alignment.MIDDLE_CENTER);
        typeDesignationTypeCooser.setContent(layout);
        UI.getCurrent().addWindow(typeDesignationTypeCooser);
    }

    protected void addNewTypeDesignationSet(TypeDesignationSetType newWorkingsetType, UUID registrationEntityUuid,
            Window typeDesignationTypeChooser, Button sourceButton) {
        //TODO typeDesignationTypeCooser parameter can be removed once we remove
        //chooseNewTypeRegistrationWorkingset()
        if (typeDesignationTypeChooser != null) {
            UI.getCurrent().removeWindow(typeDesignationTypeChooser);
        }
        EntityReference typifiedNameRef = typifiedNamesMap.get(registrationEntityUuid);
        getViewEventBus().publish(this, new TypeDesignationSetEditorAction(
                newWorkingsetType,
                registrationEntityUuid,
                typifiedNameRef.getUuid(),
                sourceButton,
                null,
                this
                ));
    }

    @Override
    protected String getHeaderText() {
        return headerText;
    }

    @Override
    public void setHeaderText(String text) {
        this.headerText = text;
        updateHeader();
    }

    public String getSubheaderText() {
        return subheaderText;
    }

    @Override
    public void setSubheaderText(String text) {
        subheaderText = text;
        updateHeader();
    }

    @Override
    protected String getSubHeaderText() {
        return subheaderText;
    }

    @Override
    public void openDetailsPopup(String caption, List<String> messages) {
        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"details-popup-content\">");
        messages.forEach(s -> sb.append(s).append("</br>"));
        sb.append("</div>");
        new Notification(caption, sb.toString(), Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
    }

    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }

    @Override
    public String getAccessDeniedMessage() {
        return accessDeniedMessage;
    }

    @Override
    public void setAccessDeniedMessage(String accessDeniedMessage) {
        this.accessDeniedMessage = accessDeniedMessage;
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
    public LazyComboBox<TaxonName> getExistingNameCombobox() {
        return existingNameCombobox;
    }

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