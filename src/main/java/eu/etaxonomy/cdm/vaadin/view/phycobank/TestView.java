/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.phycobank;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

import com.vaadin.devday.ui.MenuItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.internal.ViewScopeImpl;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author a.kohlbecker
 * @since Feb 24, 2017
 *
 */
//@SpringView
@SpringComponent
@Scope(scopeName = ViewScopeImpl.VAADIN_VIEW_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
@MenuItem(name="Test", icon=FontAwesome.INFO_CIRCLE, order = 0)
public class TestView extends VerticalLayout  implements View {

    /**
     *
     */
    private static final long serialVersionUID = 6152530138547633828L;

    public TestView(){
        Label label = new Label();
        label.setDescription("This is only a test!");
        this.addComponent(label);
    }

    @PostConstruct
    protected void initialize() {


    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }



}
