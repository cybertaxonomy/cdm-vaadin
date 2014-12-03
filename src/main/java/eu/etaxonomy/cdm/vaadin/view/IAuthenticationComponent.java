package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.security.core.Authentication;


public interface IAuthenticationComponent {
	
	public interface AuthenticationComponentListener {
        Authentication login(String userName, String password);
    }
    public void addListener(AuthenticationComponentListener listener);
    

}
