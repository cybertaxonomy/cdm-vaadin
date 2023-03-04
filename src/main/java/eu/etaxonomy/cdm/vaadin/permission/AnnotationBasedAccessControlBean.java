/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import java.io.Serializable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;

/**
 * @author a.kohlbecker
 * @since Apr 24, 2017
 *
 * (FIMXE 1. consider renaming this class and its interface, since it is no longer annotation based!!!!)
 *
 * FIMXE 2. this class should implement ViewAccessControl. The view class and annotations can be accessed
 * via the application context before the view bean has been created. see #7967
 */
public class AnnotationBasedAccessControlBean implements ViewInstanceAccessControl, Serializable {

    private static final long serialVersionUID = -4232241572782673248L;

    private static final Logger logger = LogManager.getLogger();


    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {

//        if(view.getClass().isAnnotationPresent(RequireAuthentication.class)){
//            return currentSecurityContext().getAuthentication().isAuthenticated();
//        }
        // no RequireAuthentication annotation => grant access

        Class<? extends View> viewClass = view.getClass();

        if(AccessRestrictedView.class.isAssignableFrom(viewClass)){
            AccessRestrictedView restricedView = (AccessRestrictedView)view;
            if(restricedView.allowAnonymousAccess()){
                if(logger.isTraceEnabled()){
                    logger.trace("anonymous access to " + viewClass.getName() + " allowed");
                }
                return true;
            } else {
                Authentication authentication = currentSecurityContext().getAuthentication();
                if(authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                    if(logger.isTraceEnabled()){
                        logger.trace("allowing authenticated user " + authentication.getName() + " to access " + viewClass.getName() );
                    }
                    return true;
                }

                if(logger.isTraceEnabled()){
                    logger.trace("denying access to " + viewClass.getName());
                }
                restricedView.releaseResourcesOnAccessDenied();
                return false;
                // FIMXE implement further checks
                // TODO use the UserHelperBean?
            }
        }

        return true;
    }

    /**
     * @return
     *
     * FIXME is it ok to use the SecurityContextHolder or do we need to hold the context in the vaadin session?
     */
    private SecurityContext currentSecurityContext() {
        return SecurityContextHolder.getContext();
    }




}
