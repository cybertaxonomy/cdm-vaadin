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

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class FieldListEditor<V extends Object, F extends AbstractField<V>>  extends CompositeCustomField<List<V>> {

    private static final long serialVersionUID = 4670707714503199599L;

    private Class<F> fieldType;

    private Class<V> itemType;

    //NOTE: Managing the item
    //      IDs makes BeanContainer more complex to use, but it is necessary in some cases where the
    //      equals() or hashCode() methods have been reimplemented in the bean.
    //      TODO CdmBase an a reimplemented equals method, do we need to use the BeanContainer instead?
    private BeanItemContainer<V> beans;

   //private LinkedList<V> itemList = new LinkedList<>();

    private int GRID_COLS = 2;

    private GridLayout grid = new GridLayout(GRID_COLS,1);

    public  FieldListEditor(Class<V> itemType, Class<F> fieldType, String caption){
        this.fieldType = fieldType;
        this.itemType = itemType;
        setCaption(caption);
        beans = new BeanItemContainer<V>(itemType);
    }

    private Component buttonGroup(){
        Button add = new Button(FontAwesome.PLUS);
        Button remove = new Button(FontAwesome.MINUS);
        Button moveUp = new Button(FontAwesome.ARROW_UP);
        Button moveDown = new Button(FontAwesome.ARROW_DOWN);
        CssLayout buttonGroup = new CssLayout(add, remove, moveUp, moveDown);
        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

        addStyledComponents(add, remove, moveUp, moveDown);

        return buttonGroup;
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

        beans.addAll(newValue);

        grid.setRows(newValue.size());
        int row = 0;
        for(V val : newValue){
            try {
                F field = fieldType.newInstance();
                addStyledComponent(field);
                field.setWidth(100, Unit.PERCENTAGE);
                field.setValue(val);
                grid.addComponent(field, 0, row);
                grid.addComponent(buttonGroup(), 1, row);
                row++;
            } catch (InstantiationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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








}
