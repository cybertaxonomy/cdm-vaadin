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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
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
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons.TypeDesignationWorkingSetButton;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemsPanel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStateLabel;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.RegistrationEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TypeDesignationWorkingsetEditorAction;
import eu.etaxonomy.cdm.vaadin.event.registration.RegistrationWorkingsetAction;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.security.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
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

    /**
     *
     */
    private static final int COL_INDEX_STATE_LABEL = 0;

    /**
     *
     */
    private static final int COL_INDEX_REG_ITEM = 1;

    /**
     *
     */
    private static final int COL_INDEX_BUTTON_GROUP = 2;

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

    /**
     * uses the registrationId as key
     */
    private Map<Integer, RegistrationDetailsItem> registrationItemMap = new HashMap<>();

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
        addContentComponent(workingsetHeader, null);

        registrationListPanel = createRegistrationsList(workingset);
        registrationListPanel.setHeight("100%");
        registrationListPanel.setStyleName("registration-list");
        registrationListPanel.setCaption("Registrations");
        addContentComponent(registrationListPanel, 1.0f);

    }

    @Override
    public void setBlockingRegistrations(int registrationId, Set<RegistrationDTO> blockingRegDTOs) {

        RegistrationDetailsItem regItem = registrationItemMap.get(registrationId);

        boolean blockingRegAdded = false;
        for(Iterator it = regItem.itemFooter.iterator(); it.hasNext(); ){
            if(it.next() instanceof RegistrationItemsPanel){
                blockingRegAdded = true;
                break;
            }
        }
        if(!blockingRegAdded){
            regItem.itemFooter.addComponent(new RegistrationItemsPanel(this, "Blocked by", blockingRegDTOs));
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
        registrationsGrid.setRows(workingset.getRegistrationDTOs().size() * 2  + 2);
        int row = 0;
        for(RegistrationDTO dto : workingset.getRegistrationDTOs()) {
            row = putRegistrationListComponent(row, dto);
        }

        Label addRegistrationLabel_1 = new Label("Add a new registration for a");
        Label addRegistrationLabel_2 = new Label("or an");

        addNewNameRegistrationButton = new Button("new name");
        addNewNameRegistrationButton.setDescription("A name which is newly published in this publication.");
        addNewNameRegistrationButton.addClickListener(
                e -> getViewEventBus().publish(this, new TaxonNameEditorAction(EditorActionType.ADD, null, addNewNameRegistrationButton, this)));

        addExistingNameButton = new Button("existing name:");
        addExistingNameButton.setDescription("A name which was previously published in a earlier publication.");
        addExistingNameButton.setEnabled(false);
        addExistingNameButton.addClickListener(
                e -> getViewEventBus().publish(this, new RegistrationWorkingsetAction(citationID, RegistrationWorkingsetAction.Action.start))
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

        row++;
        registrationsGrid.addComponent(buttonContainer, 0, row, COL_INDEX_BUTTON_GROUP, row);
        registrationsGrid.setComponentAlignment(buttonContainer, Alignment.MIDDLE_RIGHT);

        Panel namesTypesPanel = new Panel(registrationsGrid);
        namesTypesPanel.setStyleName(EditValoTheme.PANEL_CONTENT_PADDING_LEFT);
        return namesTypesPanel;
    }



    protected int putRegistrationListComponent(int row, RegistrationDTO dto) {

        RegistrationItemNameAndTypeButtons regItemButtonGroup = new RegistrationItemNameAndTypeButtons(dto);
        Integer registrationEntityID = dto.getId();

        RegistrationItemButtons regItemButtons = new RegistrationItemButtons();

        CssLayout footer = new CssLayout();
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setStyleName("item-footer");

        RegistrationDetailsItem regDetailsItem = new RegistrationDetailsItem(regItemButtonGroup, regItemButtons, footer);
        registrationItemMap.put(registrationEntityID, regDetailsItem);

        Stack<EditorActionContext> context = new Stack<EditorActionContext>();
        context.push(new EditorActionContext(
                    new TypedEntityReference<>(Registration.class, registrationEntityID),
                    this)
                    );

        if(regItemButtonGroup.getNameButton() != null){
            regItemButtonGroup.getNameButton().getButton().addClickListener(e -> {
                Integer nameId = regItemButtonGroup.getNameButton().getId();
                getViewEventBus().publish(this, new TaxonNameEditorAction(
                    EditorActionType.EDIT,
                    nameId,
                    e.getButton(),
                    this,
                    context
                    )
                );
            });
        }

        for(TypeDesignationWorkingSetButton workingsetButton : regItemButtonGroup.getTypeDesignationButtons()){
            workingsetButton.getButton().addClickListener(e -> {
                TypedEntityReference baseEntityRef = workingsetButton.getBaseEntity();
                TypeDesignationWorkingSetType workingsetType = workingsetButton.getType();
                getViewEventBus().publish(this, new TypeDesignationWorkingsetEditorAction(
                        EditorActionType.EDIT,
                        baseEntityRef,
                        workingsetType,
                        registrationEntityID,
                        e.getButton(),
                        this,
                        context
                        )
                    );
            });
        }

        regItemButtonGroup.getAddTypeDesignationButton().addClickListener(
                e -> chooseNewTypeRegistrationWorkingset(dto.getId())
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
                    new ShowDetailsEvent<RegistrationDTO, Integer>(
                            e,
                            RegistrationDTO.class,
                            dto.getId(),
                            RegistrationItem.BLOCKED_BY
                            )
                    ));
        }

        Button validationProblemsButton = regItemButtons.getValidationProblemsButton();
        validationProblemsButton.setStyleName(ValoTheme.BUTTON_TINY); //  + " " + RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);

        if(!dto.getValidationProblems().isEmpty()){
            validationProblemsButton.setEnabled(true);
            validationProblemsButton.addClickListener(e -> getViewEventBus().publish(this,
                    new ShowDetailsEvent<RegistrationDTO, Integer>(
                        e,
                        RegistrationDTO.class,
                        dto.getId(),
                        RegistrationItem.VALIDATION_PROBLEMS
                        )
                    )
                );
        }
        validationProblemsButton.setCaption("<span class=\"" + RegistrationStyles.BUTTON_BADGE +"\"> " + dto.getValidationProblems().size() + "</span>");
        validationProblemsButton.setCaptionAsHtml(true);

        Button messageButton = regItemButtons.getMessagesButton();
        messageButton.addClickListener(e -> getViewEventBus().publish(this,
                    new ShowDetailsEvent<RegistrationDTO, Integer>(
                        e,
                        RegistrationDTO.class,
                        dto.getId(),
                        RegistrationItem.MESSAGES
                        )
                    )
                );
        messageButton.setStyleName(ValoTheme.BUTTON_TINY);

        RegistrationStateLabel stateLabel = new RegistrationStateLabel().update(dto.getStatus());
        Label submitterLabel = new Label(dto.getSubmitterUserName());
        submitterLabel.setStyleName(LABEL_NOWRAP + " submitter");
        submitterLabel.setIcon(FontAwesome.USER);
        submitterLabel.setContentMode(ContentMode.HTML);
        CssLayout stateAndSubmitter = new CssLayout(stateLabel, submitterLabel);


        if(UserHelper.fromSession().userIsRegistrationCurator() || UserHelper.fromSession().userIsAdmin()) {
            Button editRegistrationButton = new Button(FontAwesome.COG);
            editRegistrationButton.setStyleName(ValoTheme.BUTTON_TINY);
            editRegistrationButton.setDescription("Edit registration");
            editRegistrationButton.addClickListener(e -> getViewEventBus().publish(this, new RegistrationEditorAction(
                EditorActionType.EDIT,
                dto.getId(),
                null,
                this
                )));
            regItemButtons.addComponent(editRegistrationButton);
        }

        PermissionDebugUtils.addGainPerEntityPermissionButton(regItemButtons, Registration.class, dto.getId(),
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
        getViewEventBus().publish(this, new TypeDesignationWorkingsetEditorAction(
                EditorActionType.ADD,
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

    @Override
    public Map<Integer, RegistrationDetailsItem> getRegistrationItemMap(){
        return Collections.unmodifiableMap(registrationItemMap);
    }


}
