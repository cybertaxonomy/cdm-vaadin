package eu.etaxonomy.cdm.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.servlet.CdmVaadinConversationalServlet;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.RedirectAfterLoginView;

@Theme("macosx")
@Title("CDM Board")
@SuppressWarnings("serial")
public class DbStatusUIRedirect extends AbstractAuthenticatedUI{

	private static final String FIRST_VIEW = "firstView";

    /*
     * NOTE: productionMode=true seems not to have any effect here, maybe because we are using multiple Servlets?
     * The is therefore set globally in the web.xml
     */
	@WebServlet(value = {"/app/dbstatus/*"}, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = true, ui = DbStatusUIRedirect.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
	public static class Servlet extends CdmVaadinConversationalServlet {
	}


	@Override
	protected void doInit(VaadinRequest request) {
		Navigator navigator = UI.getCurrent().getNavigator();
		RedirectAfterLoginView view = new RedirectAfterLoginView();
		navigator.addView(getFirstViewName(), view);
	}

	@Override
	public String getFirstViewName() {
		return FIRST_VIEW;
	}

}
