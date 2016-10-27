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
public class DbStatusUI extends AbstractAuthenticatedUI{

	private static final String FIRST_VIEW = "firstView";

	@WebServlet(value = {"/app/distribution/*"}, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = true, ui = DbStatusUI.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
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
