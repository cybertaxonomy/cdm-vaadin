/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vaadin.spring.events.EventScope;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.fieldgroup.FieldGroup.FieldGroupInvalidValueException;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.MarginHandler;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.persistence.permission.PermissionDeniedException;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.dialog.ContinueAlternativeCancelDialog;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormat;
import eu.etaxonomy.cdm.vaadin.event.EditorActionContextFormatter;
import eu.etaxonomy.cdm.vaadin.ui.PopupEditorDefaultStatusMessageSource;
import eu.etaxonomy.vaadin.component.NestedFieldGroup;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.event.FieldReplaceEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorDeleteEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;
import eu.etaxonomy.vaadin.util.PropertyIdPath;

/**
 * Optional with a deleteBtn button which can be enabled with {@link #withDeleteButton(boolean)}
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 */
public abstract class AbstractPopupEditor<DTO extends Object, P extends AbstractEditorPresenter<DTO,P,V>, V extends ApplicationView<V,P>>
        extends AbstractPopupView<V,P> {

    private static final long serialVersionUID = 5944874629527570061L;
    private static final Logger logger = LogManager.getLogger();

    private static final String READ_ONLY_MESSAGE_TEXT = "The editor is in read-only mode. You do not have authority to edit this data.";

    private BeanFieldGroup<DTO> fieldGroup;

    private VerticalLayout mainLayout;

    private Layout fieldLayout;

    private HorizontalLayout buttonLayout;

    private Button saveBtn;

    private Button cancelBtn;

    private Button deleteBtn;

    private CssLayout toolBar = new CssLayout();

    private CssLayout toolBarButtonGroup = new CssLayout();

    private Label contextBreadcrumbsLabel = new Label();

    private Label statusMessageLabel = new Label();

    private Set<String> statusMessages = new HashSet<>();

    private GridLayout gridLayoutCache;

    private boolean isBeanLoaded;

    private Stack<EditorActionContext> context = new Stack<>();

    private boolean isContextUpdated;

    private boolean isAdvancedMode = false;

    protected List<Component> advancedModeComponents = new ArrayList<>();

    private Button advancedModeButton;

    private EditorFormConfigurator<? extends AbstractPopupEditor<DTO, P,V>> editorComponentsConfigurator;

    private boolean withDeleteButton;

    public AbstractPopupEditor(Layout layout, Class<DTO> dtoType) {

        mainLayout = new VerticalLayout();
        // IMPORTANT: mainLayout must be set to full size otherwise the
        // popup window may have problems with automatic resizing of its
        // content.
        mainLayout.setSizeFull();

        setCompositionRoot(mainLayout);

        fieldGroup = new BeanFieldGroup<>(dtoType);
        fieldGroup.addCommitHandler(new SaveHandler());

        toolBar.addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
        toolBar.setWidth(100, Unit.PERCENTAGE);
        contextBreadcrumbsLabel.setId("context-breadcrumbs");
        contextBreadcrumbsLabel.setWidthUndefined();
        contextBreadcrumbsLabel.setContentMode(com.vaadin.shared.ui.label.ContentMode.HTML);
        toolBar.addComponent(contextBreadcrumbsLabel);
        toolBarButtonGroup.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        toolBarButtonGroup.setWidthUndefined();
        toolBar.addComponent(toolBarButtonGroup);
        toolBar.setVisible(false);

        fieldLayout = layout;
        fieldLayout.setWidthUndefined();
        if(fieldLayout instanceof AbstractOrderedLayout){
            ((AbstractOrderedLayout)fieldLayout).setSpacing(true);
        }
        if(MarginHandler.class.isAssignableFrom(fieldLayout.getClass())){
            ((MarginHandler)fieldLayout).setMargin(new MarginInfo(false, true, true, true));
        }

        buttonLayout = new HorizontalLayout();
        buttonLayout.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        buttonLayout.setSpacing(true);

        saveBtn = new Button("Save", FontAwesome.SAVE);
        saveBtn.setStyleName(ValoTheme.BUTTON_PRIMARY);
        saveBtn.addClickListener(e -> save());

        cancelBtn = new Button("Cancel", FontAwesome.REMOVE);
        cancelBtn.addClickListener(e -> cancelEditorDialog());

        deleteBtn = new Button("Delete", FontAwesome.TRASH);
        deleteBtn.setStyleName(ValoTheme.BUTTON_DANGER);
        deleteBtn.addClickListener(e -> delete());
        deleteBtn.setVisible(false);

        buttonLayout.addComponents(deleteBtn, saveBtn, cancelBtn);
        // deleteBtn is initially invisible, let saveBtn take all space
        buttonLayout.setExpandRatio(saveBtn, 1);
        buttonLayout.setComponentAlignment(deleteBtn, Alignment.TOP_RIGHT);
        buttonLayout.setComponentAlignment(saveBtn, Alignment.TOP_RIGHT);
        buttonLayout.setComponentAlignment(cancelBtn, Alignment.TOP_RIGHT);

        statusMessageLabel.setSizeFull();
        statusMessageLabel.setContentMode(com.vaadin.shared.ui.label.ContentMode.HTML);

        HorizontalLayout statusMessageLayout = new HorizontalLayout();
        statusMessageLayout.setSizeFull();
        statusMessageLayout.addComponent(statusMessageLabel);
        statusMessageLayout.setMargin(new MarginInfo(false, true, false, true));

        mainLayout.addComponents(toolBar, fieldLayout, statusMessageLayout, buttonLayout);
        mainLayout.setComponentAlignment(statusMessageLayout, Alignment.BOTTOM_RIGHT);
        mainLayout.setComponentAlignment(toolBar, Alignment.TOP_RIGHT);

        updateToolBarVisibility();

        UI currentUI = UI.getCurrent();
        //Note AM: why not "currentUI instanceof PopupEditorDefaultStatusMessageSource"
        if(PopupEditorDefaultStatusMessageSource.class.isAssignableFrom(currentUI.getClass())){
            String message = ((PopupEditorDefaultStatusMessageSource)currentUI).defaultStatusMarkup(this.getClass());
            addStatusMessage(message);
        }
    }

    protected VerticalLayout getMainLayout() {
        return mainLayout;
    }

    protected Layout getFieldLayout() {
        return fieldLayout;
    }

    private GridLayout gridLayout() {
        if(gridLayoutCache == null){
            if(fieldLayout instanceof GridLayout){
                gridLayoutCache = (GridLayout)fieldLayout;
            } else {
                throw new RuntimeException("The fieldlayout of this editor is not a GridLayout");
            }
        }
        return gridLayoutCache;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        if(readOnly){
            statusMessageLabel.setValue(READ_ONLY_MESSAGE_TEXT);
            statusMessageLabel.addStyleName(ValoTheme.LABEL_COLORED);
        } else {
            statusMessageLabel.setValue(null);
        }
        statusMessageLabel.setVisible(readOnly);
        logger.info("Set saveBtn.visible to " + !readOnly);
        saveBtn.setVisible(!readOnly);
        updateDeleteButtonState();
        cancelBtn.setCaption(readOnly ? "Close" : "Cancel");
        recursiveReadonly(readOnly, (AbstractComponentContainer)getFieldLayout());
    }

    protected void recursiveReadonly(boolean readOnly, AbstractComponentContainer layout) {
        for(Component c : layout){
            c.setReadOnly(readOnly);
            if(c instanceof AbstractComponentContainer){
                recursiveReadonly(readOnly, (AbstractComponentContainer)c);
            }
        }
    }

    protected AbstractLayout getToolBar() {
        return toolBar;
    }

    protected void toolBarAdd(Component c) {
        toolBar.addComponent(c, toolBar.getComponentIndex(toolBarButtonGroup) - 1);
        updateToolBarVisibility();
    }

    protected void toolBarButtonGroupAdd(Component c) {
        toolBarButtonGroup.addComponent(c);
        updateToolBarVisibility();
    }

    protected void toolBarButtonGroupRemove(Component c) {
        toolBarButtonGroup.removeComponent(c);
        updateToolBarVisibility();
    }

    private void updateToolBarVisibility() {
        boolean showToolbar = toolBarButtonGroup.getComponentCount() + toolBar.getComponentCount() > 1;
        toolBar.setVisible(toolBarButtonGroup.getComponentCount() + toolBar.getComponentCount() > 1);
        if(!showToolbar){
            mainLayout.setMargin(new MarginInfo(true, false, false, false));
        } else {
            mainLayout.setMargin(false);
        }
    }

    /**
     * The top tool-bar is initially invisible.
     */
    protected void setToolBarVisible(boolean visible){
        toolBar.setVisible(visible);
    }

    public boolean isAdvancedMode() {
        return isAdvancedMode;
    }

    public void setAdvancedMode(boolean isAdvancedMode) {
        this.isAdvancedMode = isAdvancedMode;
        advancedModeComponents.forEach(c -> c.setVisible(isAdvancedMode));
    }

    public void setAdvancedModeEnabled(boolean activate){
        if(activate && advancedModeButton == null){
            advancedModeButton = new Button(FontAwesome.WRENCH); // FontAwesome.FLASK
            advancedModeButton.setIconAlternateText("Advanced mode");
            advancedModeButton.addStyleName(ValoTheme.BUTTON_TINY);
            toolBarButtonGroupAdd(advancedModeButton);
            advancedModeButton.addClickListener(e -> {
                setAdvancedMode(!isAdvancedMode);
                }
            );

        } else if(advancedModeButton != null) {
            toolBarButtonGroupRemove(advancedModeButton);
            advancedModeButton = null;
        }
    }

    public void registerAdvancedModeComponents(Component ... c){
        advancedModeComponents.addAll(Arrays.asList(c));
    }

    // ------------------------ event handler ------------------------ //

    private class SaveHandler implements CommitHandler {

        private static final long serialVersionUID = 2047223089707080659L;

        @Override
        public void preCommit(CommitEvent commitEvent) throws CommitException {
            logger.debug("preCommit(), publishing EditorPreSaveEvent");
            // notify the presenter to start a transaction
            viewEventBus.publish(this, new EditorPreSaveEvent<DTO>(AbstractPopupEditor.this, getBean()));
        }

        @Override
        public void postCommit(CommitEvent commitEvent) throws CommitException {
            try {
                if(logger.isTraceEnabled()){
                    logger.trace("postCommit() publishing EditorSaveEvent for " + getBean().toString());
                }
                // notify the presenter to persist the bean and to commit the transaction
                viewEventBus.publish(this, new EditorSaveEvent<DTO>(AbstractPopupEditor.this, getBean()));
                if(logger.isTraceEnabled()){
                    logger.trace("postCommit() publishing DoneWithPopupEvent");
                }
                // notify the NavigationManagerBean to close the window and to dispose the view
                viewEventBus.publish(EventScope.UI, this, new DoneWithPopupEvent(AbstractPopupEditor.this, Reason.SAVE));
            } catch (Exception e) {
                logger.error(e);
                throw new CommitException("Failed to store data to backend", e);
            }
        }
    }

    protected void addCommitHandler(CommitHandler commitHandler) {
        fieldGroup.addCommitHandler(commitHandler);
    }

    protected void cancelEditorDialog(){

        if(fieldGroup.isModified()){

            ContinueAlternativeCancelDialog editorModifiedDialog = new ContinueAlternativeCancelDialog(
                    "Cancel editor",
                    "<p>The editor has been modified.<br>Do you want to save your changes or discard them?<p>",
                    "Discard",
                    "Save");
            ClickListener saveListener = e -> {editorModifiedDialog.close(); save();};
            ClickListener discardListener = e -> {editorModifiedDialog.close(); cancel();};
            ClickListener cancelListener = e -> editorModifiedDialog.close();
            editorModifiedDialog.addAlternativeClickListener(saveListener);
            editorModifiedDialog.addContinueClickListener(discardListener);
            editorModifiedDialog.addCancelClickListener(cancelListener);

            UI.getCurrent().addWindow(editorModifiedDialog);
        } else {
            cancel();
        }
    }

    /**
     * Cancel editing and discard all modifications.
     */
    @Override
    public void cancel() {
        fieldGroup.discard();
        viewEventBus.publish(EventScope.UI, this, new DoneWithPopupEvent(this, Reason.CANCEL));
    }

    private void delete() {
        viewEventBus.publish(this, new EditorDeleteEvent<DTO>(this, fieldGroup.getItemDataSource().getBean()));
        viewEventBus.publish(EventScope.UI, this, new DoneWithPopupEvent(this, Reason.DELETE));
    }

    /**
     * Save the changes made in the editor.
     */
    private void save() {
        try {
            fieldGroup.commit();
        } catch (CommitException e) {
            fieldGroup.getFields().forEach(f -> ((AbstractField<?>)f).setValidationVisible(true));
            Throwable cause = e.getCause();
            while(cause != null) {
                if(cause instanceof FieldGroupInvalidValueException){
                    FieldGroupInvalidValueException invalidValueException = (FieldGroupInvalidValueException)cause;
                    updateFieldNotifications(invalidValueException.getInvalidFields());
                    int invalidFieldsCount = invalidValueException.getInvalidFields().size();
                    Notification.show("The entered data in " + invalidFieldsCount + " field" + (invalidFieldsCount > 1 ? "s": "") + " is incomplete or invalid.");
                    break;
                } else if(cause instanceof PermissionDeniedException){
                    PermissionDeniedException permissionDeniedException = (PermissionDeniedException)cause;
                    Notification.show("Permission denied", permissionDeniedException.getMessage(), Type.ERROR_MESSAGE);
                    break;
                }
                cause = cause.getCause();
            }
            if(cause == null){
                // no known exception type found
                logger.error(e);
                PopupEditorException pee = null;
                try {
                    pee  = new PopupEditorException("Error saving popup editor", this, e);
                } catch (Throwable t) {
                    /* IGORE errors which happen during the construction of the PopupEditorException */
                }
                if(pee != null){
                    throw pee;
                }
                throw new RuntimeException(e);
            }
        }
    }

    private void updateFieldNotifications(Map<Field<?>, InvalidValueException> invalidFields) {
        for(Field<?> f : invalidFields.keySet()){
            if(f instanceof AbstractField){
                String message = invalidFields.get(f).getHtmlMessage();
                ((AbstractField<?>)f).setComponentError(new UserError(message, ContentMode.HTML, ErrorLevel.ERROR));
            }
        }
    }

    // ------------------------ field adding methods ------------------------ //

    protected TextField addTextField(String caption, String propertyId) {
        return addField(new TextFieldNFix(caption), propertyId);
    }

    protected TextField addTextField(String caption, String propertyId, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {
        return addField(new TextFieldNFix(caption), propertyId, column1, row1, column2, row2);
    }

    protected TextField addTextField(String caption, String propertyId, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        return addField(new TextFieldNFix(caption), propertyId, column, row);
    }

    protected SwitchableTextField addSwitchableTextField(String caption, String textPropertyId, String switchPropertyId, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {

        SwitchableTextField field = new SwitchableTextField(caption);
        field.bindTo(fieldGroup, textPropertyId, switchPropertyId);
        addComponent(field, column1, row1, column2, row2);
        return field;
    }

    protected SwitchableTextField addSwitchableTextField(String caption, String textPropertyId, String switchPropertyId, int column, int row)
            throws OverlapsException, OutOfBoundsException {

        SwitchableTextField field = new SwitchableTextField(caption);
        field.bindTo(fieldGroup, textPropertyId, switchPropertyId);
        addComponent(field, column, row);
        return field;
    }

    protected PopupDateField addDateField(String caption, String propertyId) {
        return addField(new PopupDateField(caption), propertyId);
    }

    protected CheckBox addCheckBox(String caption, String propertyId) {
        return addField(new CheckBox(caption), propertyId);
    }

    protected CheckBox addCheckBox(String caption, String propertyId, int column, int row){
        return addField(new CheckBox(caption), propertyId, column, row);
    }

    protected <T extends Field> T addField(T field, String propertyId) {
        fieldGroup.bind(field, propertyId);
        if(NestedFieldGroup.class.isAssignableFrom(field.getClass())){
            ((NestedFieldGroup)field).registerParentFieldGroup(fieldGroup);
        }
        addComponent(field);
        return field;
    }

    /**
     * Can only be used if the <code>fieldlayout</code> is a GridLayout.
     *
     * @param field
     *            the field to be added, not <code>null</code>.
     * @param propertyId
     * @param column
     *            the column index, starting from 0.
     * @param row
     *            the row index, starting from 0.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cell is outside the grid area.
     */
    protected <T extends Field> T addField(T field, String propertyId, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        fieldGroup.bind(field, propertyId);
        if(NestedFieldGroup.class.isAssignableFrom(field.getClass())){
            ((NestedFieldGroup)field).registerParentFieldGroup(fieldGroup);
        }
        addComponent(field, column, row);
        return field;
    }

    /**
     * Can only be used if the <code>fieldlayout</code> is a GridLayout.
     */
    protected <T extends Field> T addField(T field, String propertyId, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {
        if(propertyId != null){
            fieldGroup.bind(field, propertyId);
            if(NestedFieldGroup.class.isAssignableFrom(field.getClass())){
                ((NestedFieldGroup)field).registerParentFieldGroup(fieldGroup);
            }
        }
        addComponent(field, column1, row1, column2, row2);
        return field;
    }

    protected Field<?> getField(Object propertyId){
        return fieldGroup.getField(propertyId);
    }

    public PropertyIdPath boundPropertyIdPath(Field<?> field){

        PropertyIdPath propertyIdPath = null;
        Object propertyId = fieldGroup.getPropertyId(field);

        if(propertyId == null){
            // not found in the editor field group. Maybe the field is bound to a nested fieldgroup?
            // 1. find the NestedFieldGroup implementations from the field up to the editor
            PropertyIdPath nestedPropertyIds = new PropertyIdPath();
            Field<?> parentField = field;
            HasComponents parentComponent = parentField.getParent();
            logger.debug("field: " + parentField.getClass().getSimpleName());
            while(parentComponent != null){
                if (logger.isDebugEnabled()){logger.debug("parentComponent: " + parentComponent.getClass().getSimpleName());}
                if(NestedFieldGroup.class.isAssignableFrom(parentComponent.getClass()) && AbstractField.class.isAssignableFrom(parentComponent.getClass())){
                    Optional<FieldGroup> parentFieldGroup = ((NestedFieldGroup)parentComponent).getFieldGroup();
                    if(parentFieldGroup.isPresent()){
                        Object propId = parentFieldGroup.get().getPropertyId(parentField);
                        if(propId != null){
                            if (logger.isDebugEnabled()){logger.debug("propId: " + propId.toString());}
                            nestedPropertyIds.addParent(propId);
                        }
                        if (logger.isDebugEnabled()){logger.debug("parentField: " + parentField.getClass().getSimpleName());}
                        parentField = (Field<?>)parentComponent;
                    } else {
                        if (logger.isDebugEnabled()){logger.debug("parentFieldGroup is null, continuing ...");}
                    }
                } else if(parentComponent == this) {
                    // we reached the editor itself
                    Object propId = fieldGroup.getPropertyId(parentField);
                    if(propId != null){
                        if (logger.isDebugEnabled()){logger.debug("propId: " + propId.toString());}
                        nestedPropertyIds.addParent(propId);
                    }
                    propertyIdPath = nestedPropertyIds;
                    break;
                }
                parentComponent = parentComponent.getParent();
            }
            // 2. check the NestedFieldGroup binding the field is direct or indirect child component of the editor
//            NO lONGER NEEDED
//            parentComponent = parentField.getParent(); // get component containing the last parent field found
//            while(true){
//                if(parentComponent == getFieldLayout()){
//                    propertyIdPath = nestedPropertyIds;
//                    break;
//                }
//                parentComponent = parentComponent.getParent();
//            }
        } else {
            propertyIdPath = new PropertyIdPath(propertyId);
        }
        return propertyIdPath;
    }

    protected void addComponent(Component component) {
        fieldLayout.addComponent(component);
        applyDefaultComponentStyles(component);
    }

    protected void bindField(Field field, String propertyId){
        fieldGroup.bind(field, propertyId);
    }

    protected void unbindField(Field field){
        fieldGroup.unbind(field);
    }

    public void applyDefaultComponentStyles(Component component) {
        component.addStyleName(getDefaultComponentStyles());
    }

    protected abstract String getDefaultComponentStyles();

    /**
     * Can only be used if the <code>fieldlayout</code> is a GridLayout.
     * <p>
     * Adds the component to the grid in cells column1,row1 (NortWest corner of
     * the area.) End coordinates (SouthEast corner of the area) are the same as
     * column1,row1. The coordinates are zero-based. Component width and height
     * is 1.
     *
     * @param component
     *            the component to be added, not <code>null</code>.
     * @param column
     *            the column index, starting from 0.
     * @param row
     *            the row index, starting from 0.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cell is outside the grid area.
     */
    public void addComponent(Component component, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        applyDefaultComponentStyles(component);
        gridLayout().addComponent(component, column, row, column, row);
    }

    /**
     * Can only be used if the <code>fieldlayout</code> is a GridLayout.
     * <p>
     * Adds a component to the grid in the specified area. The area is defined
     * by specifying the upper left corner (column1, row1) and the lower right
     * corner (column2, row2) of the area. The coordinates are zero-based.
     * </p>
     *
     * <p>
     * If the area overlaps with any of the existing components already present
     * in the grid, the operation will fail and an {@link OverlapsException} is
     * thrown.
     * </p>
     *
     * @param component
     *            the component to be added, not <code>null</code>.
     * @param column1
     *            the column of the upper left corner of the area <code>c</code>
     *            is supposed to occupy. The leftmost column has index 0.
     * @param row1
     *            the row of the upper left corner of the area <code>c</code> is
     *            supposed to occupy. The topmost row has index 0.
     * @param column2
     *            the column of the lower right corner of the area
     *            <code>c</code> is supposed to occupy.
     * @param row2
     *            the row of the lower right corner of the area <code>c</code>
     *            is supposed to occupy.
     * @throws OverlapsException
     *             if the new component overlaps with any of the components
     *             already in the grid.
     * @throws OutOfBoundsException
     *             if the cells are outside the grid area.
     */
    public void addComponent(Component component, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {
        applyDefaultComponentStyles(component);
        gridLayout().addComponent(component, column1, row1, column2, row2);
    }

    public void setSaveButtonEnabled(boolean enabled){
        saveBtn.setEnabled(enabled);
    }

    protected void setSaveButtonVisible(boolean enabled){
        saveBtn.setVisible(enabled);
    }

    protected void setSaveButtonCaption(String caption) {
        saveBtn.setCaption(caption);
    }

    public void withDeleteButton(boolean withDelete){

        this.withDeleteButton = withDelete;
        if(withDeleteButton){
            buttonLayout.setExpandRatio(saveBtn, 0);
            buttonLayout.setExpandRatio(deleteBtn, 1);
        } else {
            buttonLayout.setExpandRatio(saveBtn, 1);
            buttonLayout.setExpandRatio(deleteBtn, 0);
        }
        updateDeleteButtonState();
    }

    private void updateDeleteButtonState() {
        deleteBtn.setVisible(withDeleteButton && !isReadOnly());
    }

    public boolean addStatusMessage(String message){
        boolean returnVal = statusMessages.add(message);
        updateStatusLabel();
        return returnVal;
    }

    public boolean removeStatusMessage(String message){
        boolean returnVal = statusMessages.remove(message);
        updateStatusLabel();
        return returnVal;
    }

    private void updateStatusLabel() {
        String text = "";
        for(String s : statusMessages){
            text += s + "</br>";
        }
        statusMessageLabel.setValue(text);
        statusMessageLabel.setVisible(!text.isEmpty());
        statusMessageLabel.addStyleName(ValoTheme.LABEL_COLORED);
    }

    private void updateContextBreadcrumbs() {

        List<EditorActionContext> contextInfo = new ArrayList<>(getEditorActionContext());
        String breadcrumbs = "";
        EditorActionContextFormatter formatter = new EditorActionContextFormatter();

        int cnt = 0;
        for(EditorActionContext cntxt : contextInfo){
            cnt++;
            boolean isLast = cnt == contextInfo.size();
            boolean isFirst = cnt == 1;

            boolean doClass = false; // will be removed in future
            boolean classNameForMissingPropertyPath = true; // !doClass;
            boolean doProperties = true;
            boolean doCreateOrNew = !isFirst;
            String contextmarkup = formatter.format(
                    cntxt,
                    new EditorActionContextFormat(doClass, doProperties, classNameForMissingPropertyPath, doCreateOrNew,
                            EditorActionContextFormat.TargetInfoType.FIELD_CAPTION, (isLast ? "active" : ""))
                    );
//            if(!isLast){
//                contextmarkup += " " + FontAwesome.ANGLE_RIGHT.getHtml() + " ";
//            }
            if(isLast){
                contextmarkup = "<li><span class=\"crumb active\">" + contextmarkup + "</span></li>";
            } else {
                contextmarkup = "<li><span class=\"crumb\">" + contextmarkup + "</span></li>";
            }
            breadcrumbs += contextmarkup;
        }
        contextBreadcrumbsLabel.setValue("<ul class=\"breadcrumbs\">" + breadcrumbs + "</ul>");
    }

    // ------------------------ data binding ------------------------ //

    protected void bindDesign(Component component) {
        fieldLayout.removeAllComponents();
        fieldGroup.bindMemberFields(component);
        fieldLayout.addComponent(component);
    }


    public final void loadInEditor(Object identifier) {

        DTO beanToEdit = getPresenter().loadBeanById(identifier);
        fieldGroup.setItemDataSource(beanToEdit);
        afterItemDataSourceSet();
        getPresenter().onViewFormReady(beanToEdit);
        updateContextBreadcrumbs();
        isBeanLoaded = true;
    }

    /**
     * Passes the beanInstantiator to the presenter method {@link AbstractEditorPresenter#setBeanInstantiator(BeanInstantiator)}
     *
     * @param beanInstantiator
     */
    public final void setBeanInstantiator(BeanInstantiator<DTO> beanInstantiator) {
        if(AbstractCdmEditorPresenter.class.isAssignableFrom(getPresenter().getClass())){
            ((CdmEditorPresenterBase)getPresenter()).setBeanInstantiator(beanInstantiator);
        } else {
            throw new RuntimeException("BeanInstantiator can only be set for popup editors with a peresenter of the type CdmEditorPresenterBase");
        }
    }

    /**
     * Returns the bean contained in the itemDatasource of the fieldGroup.
     */
    public DTO getBean() {
        if(fieldGroup.getItemDataSource() != null){
            return fieldGroup.getItemDataSource().getBean();

        }
        return null;
    }

    /**
     * @return true once the bean has been loaded indicating that all fields have
     *   been setup configured so that the editor is ready for use.
     */
    public boolean isBeanLoaded() {
        return isBeanLoaded;
    }

    /**
     * This method should only be used by the presenter of this view
     *
     * @param bean
     */
    protected void updateItemDataSource(DTO bean) {
        fieldGroup.getItemDataSource().setBean(bean);
    }

    /**
     * This method is called after setting the item data source whereby the
     * {@link FieldGroup#configureField(Field<?> field)} method will be called.
     * In this method all fields are set to default states defined for the fieldGroup.
     * <p>
     * You can now implement this method if you need to modify the state or value of individual fields.
     */
    protected void afterItemDataSourceSet() {
        if(editorComponentsConfigurator != null){
            editorComponentsConfigurator.updateComponentStates(this);
        }
    }


    // ------------------------ issue related temporary solutions --------------------- //
    /**
     * Publicly accessible equivalent to getPreseneter(), needed for
     * managing the presenter listeners.
     * <p>
     * TODO: refactor the presenter listeners management to get rid of this method
     *
     * @return
     * @deprecated marked deprecated to emphasize on the special character of this method
     *    which should only be used internally see #6673
     */
    @Deprecated
    public P presenter() {
        return getPresenter();
    }

    /**
     * Returns the context of editor actions for this editor.
     * The context submitted with {@link #setParentContext(Stack)} will be updated
     * to represent the current context.
     *
     * @return the context
     */
    public Stack<EditorActionContext> getEditorActionContext() {
        if(!isContextUpdated){
            if(getBean() == null){
                throw new RuntimeException("getContext() is only possible after the bean is loaded");
            }
            context.push(new EditorActionContext(getBean(), this));
            isContextUpdated = true;
        }
        return context;
    }

    /**
     * Attempts to find an item in the context of editor actions for this editor,
     * having a parentView matching the <code>viewType</code> by {@link Class#isAssignableFrom(Class)}.
     */
    @SuppressWarnings("unchecked")
    public <VIEW extends AbstractView> Optional<VIEW> findViewInEditorActionContext(Class<VIEW> viewType) {
        Stack<EditorActionContext> ctxt = getEditorActionContext();
        for(int i = ctxt.size(); i > 0; --i) {
            if(viewType.isAssignableFrom(ctxt.get(i).getParentView().getClass())){
                return Optional.of((VIEW) ctxt.get(i).getParentView());
            }
        }
        return Optional.empty();
    }

    /**
     * Set the context of editor actions parent to this editor
     *
     * @param context the context to set
     */
    public void setParentEditorActionContext(Stack<EditorActionContext> context, Field<?> targetField) {
        if(context != null){
            this.context.addAll(context);
        }
        if(targetField != null){
            this.context.get(context.size() - 1).setTargetField(targetField);
        }
    }

    protected AbstractField<String> replaceComponent(String propertyId, AbstractField<String> oldField,
            AbstractField<String> newField, int column1, int row1, int column2, int row2) {

        String value = oldField.getValue();
        newField.setCaption(oldField.getCaption());
        GridLayout grid = (GridLayout)getFieldLayout();
        grid.removeComponent(oldField);

        unbindField(oldField);
        addField(newField, propertyId, column1, row1, column2, row2);
        getViewEventBus().publish(this, new FieldReplaceEvent<>(this, oldField, newField));
        // important: set newField value at last!
        newField.setValue(value);
        return newField;
    }

    public EditorFormConfigurator<? extends AbstractPopupEditor<DTO,P,V>> getEditorComponentsConfigurator() {
        return editorComponentsConfigurator;
    }

    public void setEditorComponentsConfigurator(
            EditorFormConfigurator<? extends AbstractPopupEditor<DTO,P,V>> editorComponentsConfigurator) {
        this.editorComponentsConfigurator = editorComponentsConfigurator;
    }
}