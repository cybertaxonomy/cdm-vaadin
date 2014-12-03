package eu.etaxonomy.cdm.vaadin.ui;

import java.util.logging.Logger;

import org.springframework.security.core.Authentication;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.presenter.AuthenticationPresenter;
import eu.etaxonomy.cdm.vaadin.view.AuthenticationView;

public abstract class AbstractAuthenticatedUI extends UI {

	Navigator navigator;
	
	private static final String AUTHENTICATION_VIEW = "abstractAuthenticatedUI";

	private final static Logger logger =
			Logger.getLogger(AbstractAuthenticatedUI.class.getName());

	@Override
	protected void init(VaadinRequest request) {		
        
        // Create a navigator to control the views
        navigator = new Navigator(this, this);
        
        AuthenticationView av = new AuthenticationView();
        navigator.addView(AUTHENTICATION_VIEW, av);
        
        
        new AuthenticationPresenter(av);
        // Create and register the views
        Authentication authentication = (Authentication) VaadinSession.getCurrent().getAttribute("authentication");			
        
        doInit();
        
        if(authentication != null && authentication.isAuthenticated()) {
        	UI.getCurrent().getNavigator().navigateTo(getFirstViewName());
        } else {
        	UI.getCurrent().getNavigator().navigateTo(AUTHENTICATION_VIEW);
        }
	}
	
	protected abstract void doInit();
	
	public abstract String getFirstViewName();

}
