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
import com.vaadin.ui.CssLayout;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 * IMPORTANT see also {@link CompositeCustomField} which has almost the same functionality.
 */
@SuppressWarnings("serial")
public abstract class CompositeStyledComponent extends CssLayout {

    //-------
    private List<Component> styledComponents = new ArrayList<>();

    protected List<Component> getStyledComponents() {
        if(styledComponents == null){
            styledComponents = new ArrayList<>();
        }
        return styledComponents;
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
     */
    protected boolean addStyledComponents(Component ... components){
        List<Component> componentList = Arrays.asList(components);
        componentList.forEach(c -> applyCurrentStyleNames(c));
        return styledComponents.addAll(componentList);
    }

    /**
     * Implementations can may apply default styles to components added to <code>StyledComponents</code>
     * to prevent these styles from being overwritten when setStyleName() id called on the composite field.
     */
    protected abstract void addDefaultStyles();

}