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
import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

/**
 * TODO implement height methods for full component size support
 *
 * @author a.kohlbecker
 * @since May 12, 2017
 *
 */
@SuppressWarnings("serial")
public abstract class CompositeCustomField<T> extends CustomField<T> {

    private List<Component> styledComponents = new ArrayList<>();

    private List<Component> sizedComponents = new ArrayList<>();

    protected List<Component> getStyledComponents() {
        if(styledComponents == null){
            styledComponents = new ArrayList<>();
        }
        return styledComponents;
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addStyledComponent(Component component){
        applyCurrentStyleNames(component);
        return styledComponents.add(component);
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addStyledComponents(Component ... component){
        List<Component> componentList = Arrays.asList(component);
        componentList.forEach(c -> applyCurrentStyleNames(c));
        return styledComponents.addAll(componentList);
    }

    protected List<Component> getSizedComponents() {
        if(sizedComponents == null){
            sizedComponents = new ArrayList<>();
        }
        return sizedComponents;
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addSizedComponent(Component component){
        applyCurrentSize(component);
        return sizedComponents.add(component);
    }

    /**
     * Implementations preferably call this method in the constructor
     *
     * @param component
     * @return
     */
    protected boolean addSizedComponents(Component ... component){
        List<Component> componentList = Arrays.asList(component);
        componentList.forEach(c -> applyCurrentSize(c));
        return sizedComponents.addAll(componentList);
    }

    @Override
    public void setWidth(String width) {
        super.setWidth(width);
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidth(width);}});
    }

    @Override
    public void setWidth(float width, Unit unit){
        super.setWidth(width, unit);
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidth(width, unit);}});
    }

    @Override
    public void setWidthUndefined() {
        super.setWidthUndefined();
        getSizedComponents().forEach(c -> {if(c != null) {c.setWidthUndefined();}});
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        getStyledComponents().forEach(c -> c.setStyleName(style));
        addDefaultStyles();
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        getStyledComponents().forEach(c -> c.addStyleName(style));
    }

    protected void applyCurrentStyleNames(Component newSubComponent){
        newSubComponent.setStyleName(getStyleName());
    }

    protected void applyCurrentSize(Component newSubComponent){
        newSubComponent.setWidth(this.getWidth(), this.getWidthUnits());
    }

    /**
     * Implementations can may apply default styles to components added to <code>StyledComponents</code>
     * to prevent these styles from being overwritten when setStyleName() id called on the composite field.
     */
    protected abstract void addDefaultStyles();

}
