/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.util.List;

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

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class ToManyRelatedEntitiesListSelect<V extends Object, F extends AbstractField<V>>  extends CompositeCustomField<List<V>> {

    private static final long serialVersionUID = 4670707714503199599L;

    protected Class<F> fieldType;

    protected Class<V> itemType;

    private FieldGroup parentFieldGroup = null;

    protected boolean isOrderedCollection = false;

    private boolean withEditButton = false;

    //NOTE: Managing the item
    //      IDs makes BeanContainer more complex to use, but it is necessary in some cases where the
    //      equals() or hashCode() methods have been reimplemented in the bean.
    //      TODO CdmBase an a reimplemented equals method, do we need to use the BeanContainer instead?
    private BeanItemContainer<V> beans;

   //private LinkedList<V> itemList = new LinkedList<>();

    private int GRID_COLS = 2;

    private GridLayout grid = new GridLayout(GRID_COLS,1);

    public  ToManyRelatedEntitiesListSelect(Class<V> itemType, Class<F> fieldType, String caption){
        this.fieldType = fieldType;
        this.itemType = itemType;
        setCaption(caption);
        beans = new BeanItemContainer<V>(itemType);
    }

    private Component buttonGroup(F field){

        CssLayout buttonGroup = new CssLayout();
        Button add = new Button(FontAwesome.PLUS);
        ClickListener addclickListerner = newRemoveButtonClicklistener(field);
        if(addclickListerner != null){
            add.addClickListener(addclickListerner);
        }

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
        ClickListener removeclickListerner = newRemoveButtonClicklistener(field);
        if(removeclickListerner != null){
            remove.addClickListener(removeclickListerner);
        }

        buttonGroup.addComponent(add);
        buttonGroup.addComponent(remove);
        addStyledComponents(add, remove);
        if(isOrderedCollection){
            Button moveUp = new Button(FontAwesome.ARROW_UP);
            Button moveDown = new Button(FontAwesome.ARROW_DOWN);
            buttonGroup.addComponents(moveUp, moveDown);
            addStyledComponents(moveUp, moveDown);
        }
        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        return buttonGroup;
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

        // newValue is already converted, need to use the original value from the dataasource
        isOrderedCollection = List.class.isAssignableFrom(getPropertyDataSource().getValue().getClass());

        beans.addAll(newValue);

        grid.setRows(newValue.size());
        int row = 0;
        for(V val : newValue){
            row = addNewRow(row, val);
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
            grid.addComponent(field, 0, row);
            grid.addComponent(buttonGroup(field), 1, row);
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
     * @param field
     */
    protected void nestFieldGroup(F field) {
        if(NestedFieldGroup.class.isAssignableFrom(fieldType) && parentFieldGroup != null){
            ((NestedFieldGroup)field).registerParentFieldGroup(parentFieldGroup);
        }
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

    @Override
    public void registerParentFieldGroup(FieldGroup parent) {
        parentFieldGroup = parent;
    }

    public void withEditButton(boolean withEditButton){
        this.withEditButton = withEditButton;
    }








}
