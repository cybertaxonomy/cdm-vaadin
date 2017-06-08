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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    //NOTE: Managing the item
    //      IDs makes BeanContainer more complex to use, but it is necessary in some cases where the
    //      equals() or hashCode() methods have been reimplemented in the bean.
    //      TODO CdmBase has a reimplemented equals method, do we need to use the BeanContainer instead?
    private BeanItemContainer<V> beans;

   //private LinkedList<V> itemList = new LinkedList<>();

    private int GRID_COLS = 2;

    private GridLayout grid = new GridLayout(GRID_COLS,1);

    private Set<F> nestedFields = new HashSet<>() ;

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

        // newValue is already converted, need to use the original value from the data source
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
            nestedFields .add(field);
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
        nestedFields.forEach(f -> f.commit());
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
        nestedFields.forEach(f -> nestedValues.add(f.getValue()));
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
