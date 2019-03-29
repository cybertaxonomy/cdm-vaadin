/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.event.EntityEditorActionEvent;
import eu.etaxonomy.vaadin.event.EntityEditorActionListener;
import eu.etaxonomy.vaadin.permission.EditPermissionTester;

/**
 * Manages the a collection of items internally as LinkedList<V>. If the Collection to operate on is a Set a Converter must be
 * set. Internally used fields are used in un-buffered mode. The actual instances of the field type <code>F</code> to be used to
 * edit or select the entities is created by a implementation of the <code>EntityFieldInstantiator</code>,
 * see {@link #setEntityFieldInstantiator(EntityFieldInstantiator).
 *
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class ToManyRelatedEntitiesListSelect<V extends Object, F extends AbstractField<V>>  extends CompositeCustomField<List<V>> {

    private static final long serialVersionUID = 4670707714503199599L;

    private static final Logger logger = Logger.getLogger(ToManyRelatedEntitiesListSelect.class);

    protected Class<F> fieldType;

    protected Class<V> itemType;

    private FieldGroup parentFieldGroup = null;

    private Boolean valueInitiallyWasNull = null;

    protected boolean isOrderedCollection = false;

    /**
     * with a button to edit existing and to add new entities
     */
    private boolean withEditButton = false;

    protected boolean addEmptyRowOnInitContent = true;

    private EntityFieldInstantiator<F> entityFieldInstantiator;

    private EditPermissionTester editPermissionTester;

    private EntityEditorActionListener editActionListener;

    /**
     * X index of the data field in the grid
     */
    private static final int GRID_X_FIELD = 0;

    private static final int GRID_X_BUTTON_GROUP = 1;

    private int GRID_COLS = 2;

    private GridLayout grid = new GridLayout(GRID_COLS, 1);

    private boolean creatingFields;

    private List<Validator> fieldValidators = new ArrayList<>();

    public  ToManyRelatedEntitiesListSelect(Class<V> itemType, Class<F> fieldType, String caption){
        this.fieldType = fieldType;
        this.itemType = itemType;
        setCaption(caption);
    }

    /**
     * @param field
     * @return
     */
    protected Integer findRow(F field) {
        Integer row = null;
        for(int r = 0; r < grid.getRows(); r++){
            if(grid.getComponent(GRID_X_FIELD, r).equals(field)){
                row = r;
                break;
            }
        }
        return row;
    }

    /**
     *
     * @return an unmodifiable List of the data Fields
     */
    protected List<F> fields() {
        Integer row = null;
        List<F> fields = new ArrayList<>();
        for(int r = 0; r < grid.getRows(); r++){
            fields.add((F) grid.getComponent(GRID_X_FIELD, r));
        }
        return fields;
    }

    /**
     * @param field
     * @return
     */
    private void addRowAfter(F field) {

        List<V> nestedValues = getValueFromNestedFields();

        if(isOrderedCollection){

        } else {

        }

        Integer row = findRow(field);

        grid.insertRow(row + 1);

        // setting null as value for new rows
        // see newFieldInstance() !!!
        addNewRow(row + 1, null);
        updateValue();

    }

    /**
     * @param field
     * @return
     */
    private void removeRow(F field) {

        Integer row = findRow(field);
        grid.removeRow(row);
        // TODO remove from nested fields
        updateValue();
        updateButtonStates();
    }


    /**
     * @param field
     * @return
     */
    private void moveRowDown(F field) {

        Integer row = findRow(field);
        swapRows(row);
    }

    /**
     * @param field
     * @return
     */
    private void moveRowUp(F field) {

        Integer row = findRow(field);
        swapRows(row - 1);
    }

    /**
     * @param i
     */
    private void swapRows(int i) {
        if(i >= 0 && i + 1 < grid.getRows()){
            grid.replaceComponent(grid.getComponent(GRID_X_FIELD, i), grid.getComponent(GRID_X_FIELD, i + 1));
            grid.replaceComponent(grid.getComponent(GRID_X_FIELD  + 1 , i), grid.getComponent(GRID_X_FIELD + 1, i + 1));
            updateButtonStates();
            updateValue();
        } else {
            throw new RuntimeException("Cannot swap rows out of the grid bounds");
        }
    }

    /**
     * update Value is only called in turn of UI changes like adding, removing, swapping rows
     */
    private void updateValue() {
        List<V> nestedValues = getValueFromNestedFields();
        List<V> beanList = getValue();
        if(beanList != null){
            beanList.clear();
            beanList.addAll(nestedValues);
        }
        setInternalValue(beanList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {
        grid.setColumnExpandRatio(0, 1.0f);
        // set internal value to null to add an empty row

        if(addEmptyRowOnInitContent){
            // add an empty row
            setInternalValue(null);
        }
        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class getType() {
        return List.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(List<V> newValue) {

        super.setInternalValue(newValue);

        if(valueInitiallyWasNull == null){
            valueInitiallyWasNull = newValue == null;
        }

        if(newValue != null){
            // newValue is already converted, need to use the original value from the data source
            boolean isListType = List.class.isAssignableFrom(getPropertyDataSource().getValue().getClass());
            // if(valueInitiallyWasNull && isOrderedCollection != isListType){
            if(valueInitiallyWasNull && isOrderedCollection != isListType){
                // need to reset the grid in this case, so that the button groups are created correctly
                clearRows();
            }
            isOrderedCollection = isListType;
        } else {
            clearRows();
        }

        if(!creatingFields){
            createFieldsForData();
        }
    }

    private void clearRows() {
        grid.removeAllComponents();
        grid.setRows(1);
    }

    private void createFieldsForData(){

        creatingFields = true;
        List<V> data = getValue();
        if(data == null || data.isEmpty()){
            data = Arrays.asList((V)null);
        }
        for(int row = 0; row < data.size(); row++){
            boolean newRowNeeded = true;
            if(grid.getRows() > row){
                Component fieldComponent = grid.getComponent(GRID_X_FIELD, row);
                if(fieldComponent != null){
                    newRowNeeded = false;
                    F field = (F)fieldComponent;
                    if(data.get(row) != null && field.getValue() != data.get(row)){
                        field.setValue(data.get(row));
                    }
                }
            }
            if(newRowNeeded){
                addNewRow(row, data.get(row));
            } else {
                // update the editOrCreate buttons
                ButtonGroup bg = (ToManyRelatedEntitiesListSelect<V, F>.ButtonGroup) grid.getComponent(GRID_X_BUTTON_GROUP, row);
                updateEditOrCreateButton(bg, data.get(row));
            }
        }
        creatingFields = false;
    }

    /**
     * Obtains the List of values directly from the nested fields and ignores the
     * value of the <code>propertyDataSource</code>. This is useful when the ToManyRelatedEntitiesListSelect
     * is operating on a transient field, in which case the property is considered being read only by vaadin
     * so that the commit is doing nothing.
     *
     * See also {@link AbstractCdmEditorPresenter#handleTransientProperties(DTO bean)}
     *
     * @return
     */
    public List<V> getValueFromNestedFields() {
        List<V> nestedValues = new ArrayList<>();
        for(F f : getNestedFields()) {
            logger.trace(
                    String.format("getValueFromNestedFields() - %s:%s",
                       f != null ? f.getClass().getSimpleName() : "null",
                       f != null && f.getValue() != null ? f.getValue() : "null"
            ));
            V value = f.getValue();
            if(f != null /*&& value != null*/){
                nestedValues.add(f.getValue());
            }
         }
        return nestedValues;
    }

    /**
     * @param row the row index, starting from 0.
     * @param val
     * @return
     */
    protected int addNewRow(int row, V val) {
        try {
            F field = newFieldInstance(val);
            for(Validator validator : fieldValidators) {
                field.addValidator(validator);
            }
            ButtonGroup buttonGroup = new ButtonGroup(field);
            updateEditOrCreateButton(buttonGroup, val);
            field.addValueChangeListener(e -> {
                if(!creatingFields){
                    updateValue();
                    Object value = e.getProperty().getValue();
                    updateEditOrCreateButton(buttonGroup, value);
                    fireValueChange(true);
                }
            });
            Property ds = getPropertyDataSource();
            if(ds != null){
                Object parentVal = ds.getValue();
            }
            addStyledComponent(field);

            // important! all fields must be un-buffered
            field.setBuffered(false);

            if(getNestedFields().size() == grid.getRows()){
                grid.setRows(grid.getRows() + 1);
            }
            grid.addComponent(field, GRID_X_FIELD, row);
            grid.addComponent(buttonGroup, GRID_X_BUTTON_GROUP, row);
            updateButtonStates();
            nestFieldGroup(field);
            row++;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return row;
    }

    /**
     * @param buttonGroup
     * @param value
     */
    public void updateEditOrCreateButton(ButtonGroup buttonGroup, Object value) {

        if(!withEditButton || buttonGroup == null || buttonGroup.getEditOrCreateButton() == null){
            return;
        }

        ButtonFactory buttonStyle;
        if(value == null){
            buttonStyle = ButtonFactory.CREATE_NEW;
        } else {
            buttonStyle = ButtonFactory.EDIT_ITEM;
        }
        buttonGroup.getEditOrCreateButton().setIcon(buttonStyle.getIcon());
        buttonGroup.getEditOrCreateButton().setDescription(buttonStyle.getDescription());
    }

    class ButtonGroup extends CssLayout{

        private Button editOrCreate;

        ButtonGroup (F field){

            Button add = ButtonFactory.ADD_ITEM.createButton();
            add.setDescription("Add item");
            add.addClickListener(e -> addRowAfter(field));

            if(withEditButton){
                editOrCreate = ButtonFactory.EDIT_ITEM.createButton();
                editOrCreate.addClickListener(e -> editOrCreate(field));
                addComponent(editOrCreate);
                addStyledComponents(editOrCreate);
            }

            Button remove = ButtonFactory.REMOVE_ITEM.createButton();
            remove.setDescription("Remove item");
            remove.addClickListener(e -> removeRow(field));


            addComponent(add);
            addComponent(remove);
            addStyledComponents(add, remove);
            if(isOrderedCollection){
                Button moveUp = new Button(FontAwesome.ARROW_UP);
                moveUp.setDescription("Move up");
                moveUp.addClickListener(e -> moveRowUp(field));
                Button moveDown = new Button(FontAwesome.ARROW_DOWN);
                moveDown.addClickListener(e -> moveRowDown(field));
                moveDown.setDescription("Move down");

                addComponents(moveUp, moveDown);
                addStyledComponents(moveUp, moveDown);
            }
            setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        }

        Button getEditOrCreateButton(){
            return editOrCreate;
        }

    }

    /**
     * @param e
     * @return
     */
    private void editOrCreate(F field) {

        if(editActionListener == null){
            throw new RuntimeException("editActionListener missing");
        }

        if(field.getValue() == null){
            // create
            editActionListener.onEntityEditorActionEvent(new EntityEditorActionEvent<V>(EditorActionType.ADD, null, field));
        } else {
            // edit
            V value = field.getValue();
            editActionListener.onEntityEditorActionEvent(new EntityEditorActionEvent<V>(EditorActionType.EDIT, (Class<V>) value.getClass(), value, field));
        }
    }

    private void updateButtonStates(){

        boolean isWritable = !getState().readOnly;
        int fieldsCount = getNestedFields().size();

        for(int row = 0; row < fieldsCount; row++){

            boolean isFirst = row == 0;
            boolean isLast = row == fieldsCount - 1;

            F field = (F) grid.getComponent(GRID_X_FIELD, row);
            CssLayout buttonGroup = (CssLayout) grid.getComponent(GRID_X_FIELD + 1, row);

            int addButtonIndex = 0;
            if(withEditButton){
                addButtonIndex++;
                // edit
                Button editCreateButton = ((Button)buttonGroup.getComponent(0));
                editCreateButton.setDescription(field.getValue() == null ? "New" : "Edit");
                editCreateButton.setEnabled(isWritable && (field.getValue() == null
                        || field.getValue() != null && testEditButtonPermission(field.getValue())));
            }
            // add
            buttonGroup.getComponent(addButtonIndex).setEnabled(isWritable && (isLast || isOrderedCollection));
            // remove
            // can be always true, removing the last entry causes an new empty entry to be added.
            buttonGroup.getComponent(addButtonIndex + 1).setEnabled(isWritable);
            // up
            if(isOrderedCollection && buttonGroup.getComponentCount() >  addButtonIndex + 2){
                buttonGroup.getComponent(addButtonIndex + 2).setEnabled(isWritable && !isFirst);
                // down
                buttonGroup.getComponent(addButtonIndex + 3).setEnabled(isWritable && !isLast);
            }
        }
    }

    /**
     * @param field
     * @return
     */
    protected boolean testEditButtonPermission(Object rowValue) {
        if(editPermissionTester != null) {
            return editPermissionTester.userHasEditPermission(rowValue);
        } else {
            return true;
        }
    }


    protected List<F> getNestedFields(){

        List<F> nestedFields = new ArrayList<>(grid.getRows());
        for(int r = 0; r < grid.getRows(); r++){
            F f = (F) grid.getComponent(GRID_X_FIELD, r);
            if(f == null){
                logger.debug(String.format("NULL field at %d,%d", GRID_X_FIELD, r));
            } else {
                logger.trace(String.format("field " + f.hashCode() + " at %d,%d", GRID_X_FIELD, r) + ", value: " + f.getValue());
                nestedFields.add(f);
            }
        }
        return Collections.unmodifiableList(nestedFields);
    }

    /**
     *
     * @param val
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    protected F newFieldInstance(V val) throws InstantiationException, IllegalAccessException {

        F field;
        if(entityFieldInstantiator != null){
            field = entityFieldInstantiator.createNewInstance();
        } else {
            field = fieldType.newInstance();
        }

        field.setWidth(100, Unit.PERCENTAGE);
        field.setValue(val);

        // TODO
        // when passing null as value the field must take care of creating a new
        // instance by overriding setValue() in future we could improve this by passing a
        // NewInstanceFactory to this class
        return field;
    }

    /**
     * Adds the validator to the list of validators which
     * are applied to new fields and adds the validator to
     * existing fields
     *
     * @param validator
     */
    public void addFieldValidator(Validator validator){
        fieldValidators.add(validator);
        for(F field : fields()) {
            field.addValidator(validator);
        }
    }

    /**
     * removes the validator from the list of validators which
     * are applied to new fields and removes the validator from
     * existing fields
     *
     * @param validator
     */
    public void removeFieldValidator(Validator validator){
        fieldValidators.remove(validator);
        for(F field : fields()) {
            field.removeValidator(validator);
        }
    }

    /**
     * @return a unmodifialble List of the fieldValidators
     */
    public List<Validator> getFieldValidators() {
        return Collections.unmodifiableList(fieldValidators);
    }

    /**
     * Handle the data binding of the sub fields. Sub-fields can either be composite editor fields
     * or 'simple' fields, usually select fields.
     * <p>
     * Composite editor fields allow editing the nested bean Items and must implement the
     * {@link NestedFieldGroup} interface. Simple fields are only instantiated in
     * {@link #newFieldInstance(Object)} where the value of the field is set. No further binding is needed
     * for these 'simple' fields.
     *
     * @param field
     */
    protected void nestFieldGroup(F field) {
        if(NestedFieldGroup.class.isAssignableFrom(fieldType) && parentFieldGroup != null){
            ((NestedFieldGroup)field).registerParentFieldGroup(parentFieldGroup);
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * However, this class has no local fieldGroup but must delegate to the nested NestedFieldGroup
     * if there are any. This happens in {@link #nestFieldGroup(AbstractField)}.
     * <p>
     */
    @Override
    public FieldGroup getFieldGroup() {
        return null;
    }

    /**
     * This ToMany-CompositeCustomField has no own fields and this no local fieldGroup (see {@link #getFieldGroup()})
     * which allow changing data. Editing of the list items is delegated to
     * a list of sub-fields which are responsible for editing and committing the changes.
     * Therefore the <code>parentFieldGroup</code> is only stored in a local field so that it can
     * be passed to per item fields in {@link #nestFieldGroup}
     *
     * {@inheritDoc}
     */
    @Override
    public void registerParentFieldGroup(FieldGroup parent) {
        parentFieldGroup = parent;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commit() throws SourceException, InvalidValueException {

        List<F> nestedFields = getNestedFields();
        Set<F> emptyFields = new HashSet<>();
        for(F f : nestedFields){
            f.commit();
            if(f.getValue() == null){
                emptyFields.add(f);
            }
        }
        for(F deleteF : emptyFields){
            removeRow(deleteF);
        }
        /*
        List<V> list = (List<V>) getPropertyDataSource().getValue();

        Person p = Person.NewInstance();
        p.setTitleCache("Hacky", true);
        list.add((V) p);

        List<V> clonedList = new ArrayList<>(list);
        list.clear();
        for(V value : clonedList){
            if(value != null){
                list.add(value);
            }
        }
        //
         */
        // calling super.commit() is useless if operating on a transient property!!
        super.commit();
//        if(getValue().isEmpty() && valueInitiallyWasNull){
//            setPropertyDataSource(null);
//        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        grid.setWidth(width);
    }

    @Override
    public void setWidth(float width, Unit unit){
        super.setWidth(width, unit);
        if(grid != null){
            grid.setWidth(width, unit);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        // no default styles
    }


    /**
     * with a button edit existing and to add new entities
     */
    public void withEditButton(boolean withEditButton){
        this.withEditButton = withEditButton;
        if(getPropertyDataSource() != null) {
            throw new RuntimeException("withEditButton must not be changed after the datasource is set.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasNullContent() {

        for(Field f : getNestedFields()){
            if(f instanceof CompositeCustomField){
                if(!((CompositeCustomField)f).hasNullContent()){
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return the enityFieldInstantiator
     */
    public EntityFieldInstantiator<F> getEntityFieldInstantiator() {
        return entityFieldInstantiator;
    }

    /**
     * @param enityFieldInstantiator the enityFieldInstantiator to set
     */
    public void setEntityFieldInstantiator(EntityFieldInstantiator<F> entityFieldInstantiator) {
        this.entityFieldInstantiator = entityFieldInstantiator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        updateButtonStates();
    }


    /**
     * @return the editPermissionTester
     */
    public EditPermissionTester getEditPermissionTester() {
        return editPermissionTester;
    }

    /**
     * @param editPermissionTester the editPermissionTester to set
     */
    public void setEditPermissionTester(EditPermissionTester editPermissionTester) {
        this.editPermissionTester = editPermissionTester;
    }

    /**
     * @return the editActionListener
     */
    public EntityEditorActionListener getEditActionListener() {
        return editActionListener;
    }

    /**
     * @param editActionListener the editActionListener to set
     */
    public void setEditActionListener(EntityEditorActionListener editActionListener) {
        this.editActionListener = editActionListener;
    }

}
