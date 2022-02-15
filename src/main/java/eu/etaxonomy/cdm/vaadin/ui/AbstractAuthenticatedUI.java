package eu.etaxonomy.cdm.vaadin.ui;

import java.util.logging.Logger;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.common.URI;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;
import eu.etaxonomy.cdm.vaadin.view.AuthenticationView;

/**
 * @author c.mathew
 * @since 2015
 *
 * @deprecated Use per View based authentication instead. This is provided by the {@link SpringViewProvider}.
 */
@Deprecated
public abstract class AbstractAuthenticatedUI extends CdmBaseUI {

    private static final long serialVersionUID = 4018850353646930682L;

    private Navigator navigator;

	private static final String AUTHENTICATION_VIEW = "abstractAuthenticatedUI";

	private boolean ignoreAuthentication = false;

    private boolean enabled;

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
        URI uri = new URI(Page.getCurrent().getLocation());
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

	 @Override
    public void setEnabled(boolean state) {
	     this.enabled = state;
	 }

	 @Override
    public boolean isEnabled() {
	     return enabled;
	 }

}
