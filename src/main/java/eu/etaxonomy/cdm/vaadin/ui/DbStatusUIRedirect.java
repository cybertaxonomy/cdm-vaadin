package eu.etaxonomy.cdm.vaadin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.dbstatus.RedirectAfterLoginView;

@Theme("edit")
@Title("CDM Board")
// @SpringUI(path="/app/distribution") // not needed since this UI is used in the context of DbStatusUI
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DbStatusUIRedirect extends AbstractAuthenticatedUI{

	private static final String FIRST_VIEW = "firstView";

// not needed since this UI is used in the context of DbStatusUI
//	@WebServlet(value = {"/*"}, asyncSupported = true)
//	public static class Servlet extends CdmVaadinConversationalServlet {
//	}


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
