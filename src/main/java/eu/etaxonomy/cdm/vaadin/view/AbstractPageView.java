/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * A Page based on a <code>CssLayout</code> expanded to full size having a header and sub-header.
 *
 * The whole header section is build using a single <code>Label</code> for better performance.
 *
 * @author a.kohlbecker
 * @since Mar 20, 2017
 *
 */
public abstract class AbstractPageView<P extends AbstractPresenter> extends AbstractView<P>  {

    private VerticalLayout layout;

    private List<Component> contentComponents = new ArrayList<>();

    private Label header;


    /**
     *
     */
    public AbstractPageView() {
        layout = new VerticalLayout();
        layout.setSizeFull();

        header = new Label();
        header.setStyleName(ValoTheme.LABEL_HUGE);
        header.setWidth(100, Unit.PERCENTAGE);
        header.setContentMode(ContentMode.HTML);
        updateHeader();
        layout.addComponent(header);

        setCompositionRoot(layout);
        this.setSizeFull();
    }

    /**
     *
     */
    public void updateHeader() {
        header.setValue("<div id=\"header\">" + getHeaderText() + "</div><div id=\"subheader\">" + getSubHeaderText() + "</div>");
    }

    protected AbstractLayout getLayout() {
        return layout;
    }

    protected void removeContentComponents(){
        for(Component c : contentComponents){
            layout.removeComponent(c);
        }

    }

    protected void addContentComponent(Component component, Float expandRatio){
        contentComponents.add(component);
        layout.addComponent(component);
        if(expandRatio != null){
            layout.setExpandRatio(component, expandRatio);
        }
    }



    /**
     * Provides the sub header text
     *
     * @return
     */
    protected abstract String getHeaderText();

    /**
     * Provides the header text
     *
     * @return
     */
    protected abstract  String getSubHeaderText();



}
