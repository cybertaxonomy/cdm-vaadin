package eu.etaxonomy.cdm.vaadin.presenter;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.AuthenticationView;
import eu.etaxonomy.cdm.vaadin.view.IAuthenticationComponent;



public class AuthenticationPresenter implements IAuthenticationComponent.AuthenticationComponentListener{

	private final AuthenticationView view;

	public AuthenticationPresenter(AuthenticationView view) {
		this.view = view;
		view.addListener(this);
	}

	@Override
	public Authentication login(String userName, String password) {
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userName, password);
		AuthenticationManager authenticationManager = (AuthenticationManager) CdmSpringContextHelper.getCurrent().getBean("authenticationManager");
		Authentication authentication = authenticationManager.authenticate(token);
		return authentication;
	}

}
