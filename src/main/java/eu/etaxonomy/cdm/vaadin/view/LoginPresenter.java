/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.concurrent.ListenableFuture;
import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBusListener;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.application.ICdmRepository;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationAttemptEvent;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.cdm.vaadin.event.UserAccountEvent;
import eu.etaxonomy.cdm.vaadin.ui.PasswordResetUI;
import eu.etaxonomy.cdm.vaadin.util.VaadinServletUtilities;
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
@ViewScope
public class LoginPresenter extends AbstractPresenter<LoginView> implements EventBusListener<AuthenticationAttemptEvent> {

    private static final long serialVersionUID = 4020699735656994791L;

    private static final Logger log = Logger.getLogger(LoginPresenter.class);

    private final static String PROPNAME_USER = "cdm-vaadin.login.usr";

    private final static String PROPNAME_PASSWORD = "cdm-vaadin.login.pwd";

    private String redirectToState;

    protected EventBus.UIEventBus uiEventBus;

    @Autowired
    @Qualifier("cdmRepository")
    private ICdmRepository repo;

//    @Override
//    protected void eventViewBusSubscription(ViewEventBus viewEventBus) {
//        viewEventBus.subscribe(this);
//    }

    @Autowired
    protected void setUIEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

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
                    uiEventBus.publish(this, new AuthenticationSuccessEvent(userName));
                    log.debug("redirecting to " + redirectToState);
                    uiEventBus.publish(this, new NavigationEvent(redirectToState));
                }
            }
        } catch (AuthenticationException e){
            getView().showErrorMessage("Login failed! Please check your username and password.");
        }
        return false;
    }

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

    @Override
    public void onEvent(Event<AuthenticationAttemptEvent> event) {
        if(getView()!= null){
            authenticate(event.getPayload().getUserName(), getView().getLoginDialog().getPassword().getValue());
        } else {
            log.info("view is NULL, not yet disposed LoginPresenter?");
        }
    }

    @EventBusListenerMethod
    public void onPasswordRevoveryEvent(UserAccountEvent event) throws MalformedURLException, ExecutionException {

        if(event.getAction().equals(UserAccountEvent.UserAccountAction.REQUEST_PASSWORD_RESET)) {
            requestPasswordReset();
        }
    }

    private void requestPasswordReset() throws MalformedURLException, ExecutionException {
        String userNameOrEmail = getView().getLoginDialog().getUserNameOrEmail().getValue();
        URL servletBaseUrl = VaadinServletUtilities.getServletBaseUrl();
        logger.debug("PasswordRevoveryEvent for " + servletBaseUrl + ", userNameOrEmail:" + userNameOrEmail);
        // Implementation note: UI modifications allied in the below callback methods will not affect the UI
        // immediately, therefore we use a CountDownLatch
        CountDownLatch finshedSignal = new CountDownLatch(1);
        List<Throwable> asyncException = new ArrayList<>(1);
        ListenableFuture<Boolean> futureResult = repo.getPasswordResetService().emailResetToken(
                userNameOrEmail,
                servletBaseUrl.toString() + "/app/" + PasswordResetUI.PATH + "#!" + PasswordResetViewBean.NAME + "/%s");
        futureResult.addCallback(
                    successFuture -> {
                        finshedSignal.countDown();
                    },
                    exception -> {
                        // possible MailException
                        asyncException.add(exception);
                        finshedSignal.countDown();
                    }
                );
        boolean asyncTimeout = false;
        Boolean result = false;
        try {
            finshedSignal.await(2, TimeUnit.SECONDS);
            result = futureResult.get();
        } catch (InterruptedException e) {
            asyncTimeout = true;
        }
        if(!asyncException.isEmpty()) {
            getView().getLoginDialog().getMessageSendRecoveryEmailLabel()
            .setValue("Sending an password reset email to you has failed. Please try again later or contect the support in case this error persists.");
            getView().getLoginDialog().getMessageSendRecoveryEmailLabel().setStyleName(ValoTheme.LABEL_FAILURE);
        } else {
            if(!asyncTimeout && result) {
                getView().getLoginDialog().getMessageSendRecoveryEmailLabel().setValue("An email with a password reset link has been sent to you.");
                getView().getLoginDialog().getMessageSendRecoveryEmailLabel().setStyleName(ValoTheme.LABEL_SUCCESS);
                getView().getLoginDialog().getSendOnetimeLogin().setEnabled(false);
                getView().getLoginDialog().getUserNameOrEmail().setEnabled(false);
                getView().getLoginDialog().getUserNameOrEmail().setReadOnly(true);

            } else {
                getView().getLoginDialog().getMessageSendRecoveryEmailLabel().setValue("A timeout has occured, please try again.");
                getView().getLoginDialog().getMessageSendRecoveryEmailLabel().setStyleName(ValoTheme.LABEL_FAILURE);
            }
        }
    }

}
