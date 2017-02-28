/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.devday.ui.MainMenu;
import com.vaadin.devday.ui.UIInitializedEvent;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.view.phycobank.TestView;

/**
 * @author a.kohlbecker
 * @since Feb 24, 2017
 *
 */
@Theme("edit-valo")
@Title("Registration")
@SpringUI(path="registration")
@Viewport("width=device-width, initial-scale=1")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
// @PreserveOnRefresh
// @Push
public class RegistrationUI extends UI {

    private static final long serialVersionUID = -8626236111335323691L;

    @Autowired
    private ViewDisplay viewDisplay;

    /*
     * this HACKY solution forces the bean to be instantiated, TODO do it properly
     */
//    @Autowired
//    MenuBeanDiscoveryBean bean;

    @Autowired
    private MainMenu mainMenu;

    @Autowired
    ApplicationEventPublisher eventBus;

    public RegistrationUI() {

    }

    @Override
    protected void init(VaadinRequest request) {
        addStyleName(ValoTheme.UI_WITH_MENU);
        Responsive.makeResponsive(this);

        setContent((Component) viewDisplay);

        mainMenu.addMenuItem(TestView.NAME, FontAwesome.AMBULANCE, TestView.NAME);

        eventBus.publishEvent(new UIInitializedEvent());
    }
}
