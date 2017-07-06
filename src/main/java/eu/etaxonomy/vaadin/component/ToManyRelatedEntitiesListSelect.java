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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * Manages the a collection of items internally as LinkedList<V>. If the Collection to operate on is a Set a Converter must be
 * set. THe internally used fields are used in un-buffered mode.
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

    protected boolean isOrderedCollection = false;

    private boolean withEditButton = false;

    private static final int GRID_X_FIELD = 0;

    protected boolean addEmptyRowOnInitContent = true;

    //NOTE: Managing the item
    //      IDs makes BeanContainer more complex to use, but it is necessary in some cases where the
    //      equals() or hashCode() methods have been re-implemented in the bean.
    //      TODO CdmBase has a re-implemented equals method, do we need to use the BeanContainer instead?
    private BeanItemContainer<V> beans;

   //private LinkedList<V> itemList = new LinkedList<>();

    private int GRID_COLS = 2;

    private GridLayout grid = new GridLayout(GRID_COLS, 1);

    public  ToManyRelatedEntitiesListSelect(Class<V> itemType, Class<F> fieldType, String caption){
        this.fieldType = fieldType;
        this.itemType = itemType;
        setCaption(caption);
        beans = new BeanItemContainer<V>(itemType);

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
     * @param field
     * @return
     */
    private void addRowAfter(F field) {

        Integer row = findRow(field);

        grid.insertRow(row + 1);

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
     *
     */
    protected void updateValue() {
        try {
            setValue(getValueFromNestedFields());
        } catch (ReadOnlyException e){
            logger.debug("datasource is readonly, only internal value was updated");
        }
    }

    /**
     * @param field
     * @return
     */
    protected ClickListener newEditButtonClicklistener(F field) {
        return null;
    }

    /**
     * @return
     */
    protected ClickListener newAddButtonClicklistener(F field) {
        return null;
    }

    /**
     * @return
     */
    protected ClickListener newRemoveButtonClicklistener(F field) {
        return null;
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

         grid.removeAllComponents();
         grid.setRows(1);

        if(newValue != null){
            // FIMXE is it really needed to backup as linked list?
            LinkedList<V> linkedList;
            if(newValue instanceof LinkedList){
                linkedList = (LinkedList<V>) newValue;
            } else {
                linkedList = new LinkedList<>(newValue);
            }
            super.setInternalValue(linkedList);

            // newValue is already converted, need to use the original value from the data source
            isOrderedCollection = List.class.isAssignableFrom(getPropertyDataSource().getValue().getClass());

            //FIXME is beans really used?
            beans.addAll(linkedList);

            int row = 0;
            if(newValue.size() > 0){
                for(V val : linkedList){
                    row = addNewRow(row, val);
                }
            }
        }

        if(newValue == null || newValue.isEmpty()) {
            // add an empty row
            addNewRow(0, null);
        }
    }

    /**
     * @param row
     * @param val
     * @return
     */
    protected int addNewRow(int row, V val) {
        try {
            F field = newFieldInstance(val);
            addStyledComponent(field);

            // important! all fields must be un-buffered
            field.setBuffered(false);

            if(getNestedFields().size() == grid.getRows()){
                grid.setRows(grid.getRows() + 1);
            }
            grid.addComponent(field, GRID_X_FIELD, row);
            grid.addComponent(buttonGroup(field), 1, row);
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

    private Component buttonGroup(F field){

        CssLayout buttonGroup = new CssLayout();
        Button add = new Button(FontAwesome.PLUS);
        add.setDescription("Add item");
        add.addClickListener(e -> addRowAfter(field));

        if(withEditButton){
            Button edit = new Button(FontAwesome.EDIT);
            ClickListener editClickListerner = newEditButtonClicklistener(field);
            if(editClickListerner != null){
                edit.addClickListener(editClickListerner);
            }
            buttonGroup.addComponent(edit);
            addStyledComponents(edit);
        }

        Button remove = new Button(FontAwesome.MINUS);
        remove.setDescription("Remove item");
        remove.addClickListener(e -> removeRow(field));


        buttonGroup.addComponent(add);
        buttonGroup.addComponent(remove);
        addStyledComponents(add, remove);
        if(isOrderedCollection){
            Button moveUp = new Button(FontAwesome.ARROW_UP);
            moveUp.setDescription("Move up");
            moveUp.addClickListener(e -> moveRowUp(field));
            Button moveDown = new Button(FontAwesome.ARROW_DOWN);
            moveDown.addClickListener(e -> moveRowDown(field));
            moveDown.setDescription("Move down");

            buttonGroup.addComponents(moveUp, moveDown);
            addStyledComponents(moveUp, moveDown);
        }
        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        return buttonGroup;
    }

    private void updateButtonStates(){

        int fieldsCount = getNestedFields().size();
        for(int row = 0; row < fieldsCount; row++){

            boolean isFirst = row == 0;
            boolean isLast = row == fieldsCount - 1;

            CssLayout buttonGroup = (CssLayout) grid.getComponent(GRID_X_FIELD + 1, row);

            // add
            buttonGroup.getComponent(0).setEnabled(isLast || isOrderedCollection);
            // remove
            buttonGroup.getComponent(1).setEnabled(!isFirst);
            // up
            if(buttonGroup.getComponentCount() > 2){
                buttonGroup.getComponent(2).setEnabled(!isFirst);
                // down
                buttonGroup.getComponent(3).setEnabled(!isLast);
            }
        }
    }


    protected List<F> getNestedFields(){
        List<F> nestedFields = new ArrayList<>(grid.getRows());
        for(int r = 0; r < grid.getRows(); r++){
            F f = (F) grid.getComponent(GRID_X_FIELD, r);
            if(f == null){
                logger.debug(String.format("NULL field at %d,%d", GRID_X_FIELD, r));
            } else {
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
        F field = fieldType.newInstance();
        field.setWidth(100, Unit.PERCENTAGE);
        field.setValue(val);
        return field;
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
        getNestedFields().forEach(f -> f.commit());
        // calling super.commit() is useless if operating on a transient property!!
        super.commit();
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
        getNestedFields().forEach(f -> {
                logger.trace(String.format("getValueFromNestedFields() - %s:%s",
                       f != null ? f.getClass().getSimpleName() : "null",
                       f != null ? f.getValue() : "null"
                ));
                if(f != null){
                    nestedValues.add(f.getValue());
                }
            });
        return nestedValues;
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

    public void withEditButton(boolean withEditButton){
        this.withEditButton = withEditButton;
    }

}
