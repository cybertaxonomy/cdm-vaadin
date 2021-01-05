/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.common.URI;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;

public class AuthenticationPresenter implements IAuthenticationComponent.AuthenticationComponentListener{

    @Override
    public boolean login(URI uri, String context, String userName, String password) {

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
        AuthenticationManager authenticationManager = (AuthenticationManager) CdmSpringContextHelper.getCurrent().getBean("authenticationManager");
        Authentication authentication = authenticationManager.authenticate(token);

        if(authentication != null && authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            CdmVaadinAuthentication cvAuthentication = (CdmVaadinAuthentication) VaadinSession.getCurrent().getAttribute(CdmVaadinAuthentication.KEY);
            if(cvAuthentication == null) {
                cvAuthentication = new CdmVaadinAuthentication();
            }
            cvAuthentication.addAuthentication(uri, context, authentication);
            CdmVaadinSessionUtilities.setCurrentAttribute(CdmVaadinAuthentication.KEY, cvAuthentication);
            return true;
        }

        return false;
    }
}