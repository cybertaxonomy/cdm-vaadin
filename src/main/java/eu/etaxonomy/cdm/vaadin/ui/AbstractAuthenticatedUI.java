package eu.etaxonomy.cdm.vaadin.ui;

import java.util.logging.Logger;

import org.springframework.security.core.Authentication;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.presenter.AuthenticationPresenter;
import eu.etaxonomy.cdm.vaadin.view.AuthenticationView;

public abstract class AbstractAuthenticatedUI extends CdmBaseUI {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	Navigator navigator;

	private static final String AUTHENTICATION_VIEW = "abstractAuthenticatedUI";

	private boolean ignoreAuthentication = false;

	private final static Logger logger =
			Logger.getLogger(AbstractAuthenticatedUI.class.getName());

	@Override
	protected void init(VaadinRequest request) {

	    super.init(request);
        // Create a navigator to control the views
        navigator = new Navigator(this, this);

        AuthenticationView av = new AuthenticationView();
        navigator.addView(AUTHENTICATION_VIEW, av);


        new AuthenticationPresenter(av);
        // Create and register the views
        Authentication authentication = (Authentication) VaadinSession.getCurrent().getAttribute("authentication");

        doInit(request);

        if(ignoreAuthentication || (authentication != null && authentication.isAuthenticated())) {
        	UI.getCurrent().getNavigator().navigateTo(getFirstViewName());
        } else {
        	UI.getCurrent().getNavigator().navigateTo(AUTHENTICATION_VIEW);
        }
	}

	protected abstract void doInit(VaadinRequest request);

	public abstract String getFirstViewName();

	public void setIgnoreAuthentication(boolean ignoreAuthentication) {
	    this.ignoreAuthentication = ignoreAuthentication;
	}

}
