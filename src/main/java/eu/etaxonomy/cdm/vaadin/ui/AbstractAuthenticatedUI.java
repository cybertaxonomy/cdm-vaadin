package eu.etaxonomy.cdm.vaadin.ui;

import java.net.URI;
import java.util.logging.Logger;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;
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



        // Create and register the views
        CdmVaadinAuthentication cvAuthentication = (CdmVaadinAuthentication) VaadinSession.getCurrent().getAttribute(CdmVaadinAuthentication.KEY);

        doInit(request);
        URI uri = Page.getCurrent().getLocation();
        String context = VaadinServlet.getCurrent().getServletContext().getContextPath();
        if(ignoreAuthentication || (cvAuthentication != null && cvAuthentication.isAuthenticated(uri, context))) {
            if(cvAuthentication != null) {
                cvAuthentication.setSecurityContextAuthentication(uri, context);
            }
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