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

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author a.kohlbecker
 * @since Feb 24, 2017
 *
 */
@SpringView(name=TestView2.NAME)
/*
 * MenuBeanDiscovery is not yet working with spring!!!!!
@SpringComponent
@Scope(scopeName = ViewScopeImpl.VAADIN_VIEW_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
@MenuItem(name="test", icon=FontAwesome.INFO_CIRCLE, order = 0)
*/
public class TestView2 extends VerticalLayout  implements View {

    public static final String NAME = "test2";

    private static final long serialVersionUID = 6152530138547633828L;

    public TestView2(){
        setWidth(100, Unit.PERCENTAGE);
        setMargin(true);
        this.addComponent(new Label("This is another test"));
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
