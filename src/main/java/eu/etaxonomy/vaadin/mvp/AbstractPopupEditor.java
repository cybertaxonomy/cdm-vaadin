/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.util.Map;

import org.apache.log4j.Logger;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.fieldgroup.FieldGroup.FieldGroupInvalidValueException;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.GridLayout.OutOfBoundsException;
import com.vaadin.ui.GridLayout.OverlapsException;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.vaadin.component.NestedFieldGroup;
import eu.etaxonomy.vaadin.component.SwitchableTextField;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent.Reason;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractPopupEditor<DTO extends Object, P extends AbstractEditorPresenter<DTO>>
    extends AbstractPopupView<P> {

    private BeanFieldGroup<DTO> fieldGroup;

    private VerticalLayout mainLayout;

    private Layout fieldLayout;

    private HorizontalLayout buttonLayout;

    private Button save;

    private Button cancel;

    private CssLayout toolBar = new CssLayout();

    private CssLayout toolBarButtonGroup = new CssLayout();

    private GridLayout _gridLayoutCache;

    public AbstractPopupEditor(Layout layout, Class<DTO> dtoType) {

        setWidthUndefined();

        mainLayout = new VerticalLayout();
        mainLayout.setWidthUndefined();

        fieldGroup = new BeanFieldGroup<>(dtoType);
        fieldGroup.addCommitHandler(new SaveHandler());

        setCompositionRoot(mainLayout);

        toolBar.addStyleName(ValoTheme.WINDOW_TOP_TOOLBAR);
        toolBar.setWidth(100, Unit.PERCENTAGE);
        toolBarButtonGroup.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        toolBarButtonGroup.setWidthUndefined();
        toolBar.addComponent(toolBarButtonGroup);
        toolBar.setVisible(false);

        fieldLayout = layout;
        fieldLayout.setWidthUndefined();
        if(fieldLayout instanceof AbstractOrderedLayout){
            ((AbstractOrderedLayout)fieldLayout).setSpacing(true);
        }

        buttonLayout = new HorizontalLayout();
        buttonLayout.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttonLayout.setWidth(100, Unit.PERCENTAGE);
        buttonLayout.setSpacing(true);

        save = new Button("Save", FontAwesome.SAVE);
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(e -> onSaveClicked());

        cancel = new Button("Cancel", FontAwesome.TRASH);
        cancel.addClickListener(e -> onCancelClicked());

        buttonLayout.addComponents(save, cancel);
        buttonLayout.setExpandRatio(save, 1);
        buttonLayout.setComponentAlignment(save, Alignment.TOP_RIGHT);
        buttonLayout.setComponentAlignment(cancel, Alignment.TOP_RIGHT);

        mainLayout.addComponents(toolBar, fieldLayout, buttonLayout);
        mainLayout.setComponentAlignment(toolBar, Alignment.TOP_RIGHT);
    }

    protected VerticalLayout getMainLayout() {
        return mainLayout;
    }

    protected Layout getFieldLayout() {
        return fieldLayout;
    }

    /**
     * @return
     */
    private GridLayout gridLayout() {
        if(_gridLayoutCache == null){
            if(fieldLayout instanceof GridLayout){
                _gridLayoutCache = (GridLayout)fieldLayout;
            } else {
                throw new RuntimeException("The fieldlayout of this editor is not a GridLayout");
            }
        }
        return _gridLayoutCache;
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        save.setVisible(!readOnly);
        cancel.setCaption(readOnly ? "Close" : "Cancel");
    }

    /**
     * @return
     * @return
     */
    protected AbstractLayout getToolBar() {
        return toolBar;
    }

    /**
     * @return
     * @return
     */
    protected void toolBarAdd(Component c) {
        toolBar.addComponent(c, toolBar.getComponentIndex(toolBarButtonGroup) - 1);
        updateToolBarVisibility();
    }

    /**
     * @return
     * @return
     */
    protected void toolBarButtonGroupAdd(Component c) {
        toolBarButtonGroup.addComponent(c);
        updateToolBarVisibility();
    }

    /**
     * @return
     * @return
     */
    protected void toolBarButtonGroupRemove(Component c) {
        toolBarButtonGroup.removeComponent(c);
        updateToolBarVisibility();
    }

    /**
     *
     */
    private void updateToolBarVisibility() {
        toolBar.setVisible(toolBarButtonGroup.getComponentCount() + toolBar.getComponentCount() > 1);

    }

    /**
     * The top tool-bar is initially invisible.
     *
     * @param visible
     */
    protected void setToolBarVisible(boolean visible){
        toolBar.setVisible(true);
    }



    // ------------------------ event handler ------------------------ //

    private class SaveHandler implements CommitHandler {

        private static final long serialVersionUID = 2047223089707080659L;

        @Override
        public void preCommit(CommitEvent commitEvent) throws CommitException {
            logger.debug("preCommit");
            // notify the presenter to start a transaction
            eventBus.publishEvent(new EditorPreSaveEvent(commitEvent, AbstractPopupEditor.this));
        }

        @Override
        public void postCommit(CommitEvent commitEvent) throws CommitException {
            try {
                // notify the presenter to persist the bean and to commit the transaction
                eventBus.publishEvent(new EditorSaveEvent(commitEvent, AbstractPopupEditor.this));

                // notify the NavigationManagerBean to close the window and to dispose the view
                eventBus.publishEvent(new DoneWithPopupEvent(AbstractPopupEditor.this, Reason.SAVE));
            } catch (Exception e) {
                throw new CommitException("Failed to store data to backend", e);
            }
        }
    }

    protected void addCommitHandler(CommitHandler commitHandler) {
        fieldGroup.addCommitHandler(commitHandler);
    }


    private void onCancelClicked() {
        fieldGroup.discard();
        eventBus.publishEvent(new DoneWithPopupEvent(this, Reason.CANCEL));
    }

    private void onSaveClicked() {
        try {
            fieldGroup.commit();
        } catch (CommitException e) {
            fieldGroup.getFields().forEach(f -> ((AbstractField<?>)f).setValidationVisible(true));
            if(e.getCause() != null && e.getCause() instanceof FieldGroupInvalidValueException){
                FieldGroupInvalidValueException invalidValueException = (FieldGroupInvalidValueException)e.getCause();
                updateFieldNotifications(invalidValueException.getInvalidFields());
                Notification.show("The entered data in " + invalidValueException.getInvalidFields().size() + " fields is incomplete or invalid.");
            } else if(e.getCause() != null && e.getCause().getCause() != null && e.getCause().getCause() instanceof PermissionDeniedException){
                PermissionDeniedException permissionDeniedException = (PermissionDeniedException)e.getCause().getCause();
                Notification.show("Permission denied", permissionDeniedException.getMessage(), Type.ERROR_MESSAGE);
            }
            else {
                Logger.getLogger(this.getClass()).error("Error saving", e);
                Notification.show("Error saving", Type.ERROR_MESSAGE);
            }
        }
    }

    /**
     * @param invalidFields
     */
    private void updateFieldNotifications(Map<Field<?>, InvalidValueException> invalidFields) {
        for(Field<?> f : invalidFields.keySet()){
            if(f instanceof AbstractField){
                String message = invalidFields.get(f).getHtmlMessage();
                ((AbstractField)f).setComponentError(new UserError(message, ContentMode.HTML, ErrorLevel.ERROR));
            }
        }

    }

    // ------------------------ field adding methods ------------------------ //


    protected TextField addTextField(String caption, String propertyId) {
        return addField(new TextField(caption), propertyId);
    }

    protected TextField addTextField(String caption, String propertyId, int column1, int row1,
            int column2, int row2)
            throws OverlapsException, OutOfBoundsException {
        return addField(new TextField(caption), propertyId, column1, row1, column2, row2);
    }

    protected TextField addTextField(String caption, String propertyId, int column, int row)
            throws OverlapsException, OutOfBoundsException {
        return addField(new TextField(caption), propertyId, column, row);
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
     *
     * @param field
     * @param propertyId
     * @param column1
     * @param row1
     * @param column2
     * @param row2
     * @return
     * @throws OverlapsException
     * @throws OutOfBoundsException
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

    protected void addComponent(Component component) {
        fieldLayout.addComponent(component);
        applyDefaultComponentStyles(component);
    }

    /**
     * @param component
     */
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



    // ------------------------ data binding ------------------------ //

    protected void bindDesign(Component component) {
        fieldLayout.removeAllComponents();
        fieldGroup.bindMemberFields(component);
        fieldLayout.addComponent(component);
    }

    public void showInEditor(DTO beanToEdit) {
        fieldGroup.setItemDataSource(beanToEdit);
        afterItemDataSourceSet();
    }

    /**
     * This method is called after setting the item data source whereby the {@link FieldGroup#configureField(Field<?> field)} method will be called.
     * In this method all fields are set to default states defined for the fieldGroup.
     * <p>
     * You can now implement this method if you need to configure the enable state of fields
     * individually.
     */
    protected void afterItemDataSourceSet() {

    }
}
