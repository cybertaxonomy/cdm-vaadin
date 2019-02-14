/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.toolbar;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventBusListener;

import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 *
 * @author a.kohlbecker
 * @since Sep 28, 2017
 *
 */
@SpringComponent("registrationToolbar")
@UIScope
public class RegistrationToolbar extends HorizontalLayout implements Toolbar, EventBusListener<AuthenticationSuccessEvent>, DisposableBean {

    private static final long serialVersionUID = 2594781255088231474L;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private UIEventBus uiEventBus;

    @Autowired
    protected final void setUIEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

    @Autowired
    protected NavigationManager navigationManager;

    @Autowired
    UserHelper userHelper;

    CssLayout buttonGroup = new CssLayout();
    Button loginButton;
    Button logoutButton;
    Button userButton;

    public RegistrationToolbar() {
    }

    @Override
    public void initialize(){

        setWidth("100%");
        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        loginButton = new Button("login");
        userButton = new Button(FontAwesome.USER);
        logoutButton = new Button("logout");

        logoutButton.addClickListener(e -> performLogut());
        loginButton.addClickListener(e -> performLogin());
        buttonGroup.addComponent(loginButton);
        buttonGroup.addComponent(logoutButton);
        buttonGroup.addComponent(userButton);
        addComponent(buttonGroup);
        setComponentAlignment(buttonGroup, Alignment.MIDDLE_RIGHT);
        updateAuthenticationButtons();
    }


    @Override
    public void onEvent(org.vaadin.spring.events.Event<AuthenticationSuccessEvent> event) {
        updateAuthenticationButtons();
    }

    /**
     * @param event
     */
    protected void updateAuthenticationButtons() {

        if(userHelper.userIsAutheticated() && !userHelper.userIsAnnonymous()){
            userButton.setCaption(userHelper.userName());
            userButton.setVisible(true);
            logoutButton.setVisible(true);
            loginButton.setVisible(false);

        } else {
            userButton.setCaption(null);
            userButton.setVisible(false);
            logoutButton.setVisible(false);
            loginButton.setVisible(true);
        }
    }

    /**
     * @return
     */
    private void performLogin() {
        uiEventBus.publish(this, new NavigationEvent("login", navigationManager.getCurrentViewName()));
    }


    private void performLogut() {

        userHelper.logout();
        updateAuthenticationButtons();
        navigationManager.reloadCurrentView();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        uiEventBus.unsubscribe(this);
    }

}
