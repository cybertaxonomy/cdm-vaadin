/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.security;

import org.apache.log4j.Logger;
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
 */
public class AnnotationBasedAccessControlBean implements ViewInstanceAccessControl {


    private final static Logger logger = Logger.getLogger(AnnotationBasedAccessControlBean.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {

//        if(view.getClass().isAnnotationPresent(RequireAuthentication.class)){
//            return currentSecurityContext().getAuthentication().isAuthenticated();
//        }
        // no RequireAuthentication annotation => grant access

        if(AccessRestrictedView.class.isAssignableFrom(view.getClass())){
            AccessRestrictedView resticedView = (AccessRestrictedView)view;
            if(resticedView.allowAnonymousAccess()){
                if(logger.isTraceEnabled()){
                    logger.trace("anonymous access to " + view.getClass().getName() + " allowed");
                }
                return true;
            } else {
                Authentication authentication = currentSecurityContext().getAuthentication();
                if(authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
                    if(logger.isTraceEnabled()){
                        logger.trace("allowing authenticated user " + authentication.getName() + " to access " + view.getClass().getName() );
                    }
                    return true;
                }

                if(logger.isTraceEnabled()){
                    logger.trace("denying access to " + view.getClass().getName());
                }
                return false;
                // FIMXE implement further checks
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
