/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.vaadin.spring.events.EventScope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.component.LoginDialog;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationAttemptEvent;
import eu.etaxonomy.cdm.vaadin.event.PasswordRevoveryEvent;
import eu.etaxonomy.cdm.vaadin.event.RegisterNewUserEvent;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
@SpringView(name=LoginViewBean.NAME)
public class LoginViewBean  extends AbstractView<LoginPresenter> implements LoginView, View  {

    private static final long serialVersionUID = 8527714663738364972L;

    public static final String NAME = "login";

    private LoginDialog loginDialog = new LoginDialog();

    public LoginViewBean() {
        super();
    }

    @Override
    protected void initContent() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.addComponent(loginDialog);
        root.setMargin(true);
        root.setComponentAlignment(loginDialog, Alignment.MIDDLE_CENTER);
        setCompositionRoot(root);

        loginDialog.getLoginButton().addClickListener(e -> handleLoginClick(e));
        loginDialog.getRegisterButton().addClickListener(e -> getViewEventBus().publish(EventScope.UI, this, new RegisterNewUserEvent(e)));
        loginDialog.getSendOnetimeLogin().addClickListener(e -> getViewEventBus().publish(EventScope.UI, this, new PasswordRevoveryEvent(e)));
        // NOTE: null viewName will be replaced by the default view name in NavigationManagerBean
        loginDialog.getCancelLoginButton().addClickListener(e -> getViewEventBus().publish(EventScope.UI, this, new NavigationEvent(null)));
        loginDialog.getCancelRegistrationButton().addClickListener(e -> getViewEventBus().publish(EventScope.UI, this, new NavigationEvent(null)));
    }

    /**
     * @param e
     */
    private void handleLoginClick(ClickEvent e) {
        getViewEventBus().publish(this, new AuthenticationAttemptEvent(e, loginDialog.getUserName().getValue()));
    }

    @Override
    public LoginDialog getLoginDialog(){
        return loginDialog;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    // TODO pull up to AbstractView and let AbstractView implement View?
    public void enter(ViewChangeEvent event) {
        getPresenter().onViewEnter();
    }

    @Override
    public void showErrorMessage(String text){
        loginDialog.getMessageLabel().setVisible(true);
        loginDialog.getMessageLabel().setStyleName(ValoTheme.BUTTON_TINY + " " +  ValoTheme.LABEL_FAILURE);
        loginDialog.getMessageLabel().setValue(text);
    }


    @Override
    public void clearMessage(){
        loginDialog.getMessageLabel().setVisible(false);
        loginDialog.getMessageLabel().setStyleName("");
        loginDialog.getMessageLabel().setValue("");
    }
}
