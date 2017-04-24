/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.security;

import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.navigator.View;
import com.vaadin.spring.access.ViewInstanceAccessControl;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.security.annotation.RequireAuthentication;

/**
 * @author a.kohlbecker
 * @since Apr 24, 2017
 *
 */
public class AnnotationBasedAccessControlBean implements ViewInstanceAccessControl {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAccessGranted(UI ui, String beanName, View view) {
        if(view.getClass().getAnnotation(RequireAuthentication.class) != null){
            return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
        }
        // no RequireAuthentication annotation => grant access
        return true;
    }




}
