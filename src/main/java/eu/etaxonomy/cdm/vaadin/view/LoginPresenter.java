/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.vaadin.event.AuthenticationAttemptEvent;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 * The {@link LoginView is used as replacement view in the scope of other views.
 * Therefore the LoginPresenter must be in <b>UIScope</b> so that the LoginPresenter
 * is available to all Views.
 *
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
@SpringComponent
@UIScope // DO NOT CHANGE !!! LoginPresenter must be in UIScope so that the LoginPresenter is available to all Views.
public class LoginPresenter extends AbstractPresenter<LoginView> {

    private static final long serialVersionUID = 4020699735656994791L;

    @Autowired
    protected ApplicationEventPublisher eventBus;

    /**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    private SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }

    public boolean authenticate(String userName, String password){

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        AuthenticationManager authenticationManager = (AuthenticationManager) CdmSpringContextHelper.getCurrent().getBean("authenticationManager");
        Authentication authentication = authenticationManager.authenticate(token);

        if(authentication != null && authentication.isAuthenticated()) {
            currentSecurityContext().setAuthentication(authentication);
            if(NavigationManager.class.isAssignableFrom(getNavigationManager().getClass())){
                getNavigationManager().reloadCurrentView();
                eventBus.publishEvent(new AuthenticationSuccessEvent(userName));
            }
        }
        return false;
    }


    @EventListener
    protected void onLoginEvent(AuthenticationAttemptEvent e){
        authenticate(e.getUserName(), getView().getLoginDialog().getPassword().getValue());
    }



}
