package eu.etaxonomy.cdm.vaadin.presenter;

import java.net.URI;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;
import eu.etaxonomy.cdm.vaadin.view.IAuthenticationComponent;

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
