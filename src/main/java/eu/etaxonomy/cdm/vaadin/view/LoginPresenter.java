/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.vaadin.event.AuthenticationAttemptEvent;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 * The {@link LoginView} is used as replacement view in the scope of other views.
 * Therefore the LoginPresenter must be in <b>UIScope</b> so that the LoginPresenter
 * is available to all Views.
 * <p>
 * The LoginPresenter offers a <b>auto login feature for developers</b>. To activate the auto login
 * you need to provide the <code>user name</code> and <code>password</code> using the environment variables
 * <code>cdm-vaadin.login.usr</code> and <code>cdm-vaadin.login.pwd</code>, e.g.:
 * <pre>
 * -Dcdm-vaadin.login.usr=admin -Dcdm-vaadin.login.pwd=00000
 * </pre>
 *
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
@SpringComponent
@UIScope // DO NOT CHANGE !!! LoginPresenter must be in UIScope so that the LoginPresenter is available to all Views.
public class LoginPresenter extends AbstractPresenter<LoginView> {

    private static final long serialVersionUID = 4020699735656994791L;

    private static final Logger log = Logger.getLogger(LoginPresenter.class);

    private final static String PROPNAME_USER = "cdm-vaadin.login.usr";

    private final static String PROPNAME_PASSWORD = "cdm-vaadin.login.pwd";

    @Autowired
    protected ApplicationEventPublisher eventBus;

    private String redirectToState;

    public boolean authenticate(String userName, String password) {

        getView().clearMessage();

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        AuthenticationManager authenticationManager = getRepo().getAuthenticationManager();
        try {
            Authentication authentication = authenticationManager.authenticate(token);
            if(authentication != null && authentication.isAuthenticated()) {
                log.debug("user '" + userName + "' authenticated");
                currentSecurityContext().setAuthentication(authentication);
                if(NavigationManager.class.isAssignableFrom(getNavigationManager().getClass())){
                    eventBus.publishEvent(new AuthenticationSuccessEvent(userName));
                    log.debug("redirecting to " + redirectToState);
                    eventBus.publishEvent(new NavigationEvent(redirectToState));
                }
            }
        } catch (AuthenticationException e){
            getView().showErrorMessage("Login failed! Please check your username and password.");
        }
        return false;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        List<String> redirectToStateTokens = getNavigationManager().getCurrentViewParameters();
        String currentViewName = getNavigationManager().getCurrentViewName();

        if(currentViewName.equals(LoginViewBean.NAME) && redirectToStateTokens.isEmpty()){
            // login view is shown in turn to an explicit login request of the user (e.g. login button pressed)
            // use the redirectToStateTokens 1-n as redirectToState
            //FIXME implement : redirectToState = UserView.NAME

        } else {
            // the login view is shown instead of the requested view for which the user needs to login
            redirectToState = String.join("/", redirectToStateTokens);
        }

        // attempt to auto login
        if(StringUtils.isNotEmpty(System.getProperty(PROPNAME_USER)) && StringUtils.isNotEmpty(System.getProperty(PROPNAME_PASSWORD))){
            log.warn("Performing autologin with user " + System.getProperty(PROPNAME_USER));
            authenticate(System.getProperty(PROPNAME_USER), System.getProperty(PROPNAME_PASSWORD));
        }
    }

    @EventListener
    protected void onLoginEvent(AuthenticationAttemptEvent e){
        authenticate(e.getUserName(), getView().getLoginDialog().getPassword().getValue());
    }



}
