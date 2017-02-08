package eu.etaxonomy.cdm.vaadin.ui;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.servlet.CdmVaadinConversationalServlet;
import eu.etaxonomy.cdm.vaadin.view.NaviTestView;

@Theme("chameleon")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class NavigatorTestUI extends AbstractAuthenticatedUI {

	Navigator navigator;

	private static final String FIRST_VIEW = "firstView";
	public static final String SECOND_VIEW = "secondView";

	private final static Logger logger =
			Logger.getLogger(NavigatorTestUI.class.getName());

	@WebServlet(value = {"/app-test/navi/*"}, asyncSupported = true)
	public static class Servlet extends CdmVaadinConversationalServlet {

	}

	@Override
	protected void doInit(VaadinRequest request) {
		getPage().setTitle("Navigation Example");
		NaviTestView ntv1 = new NaviTestView();
		ntv1.setText("Congratulations! you have reached the first view. If you have got here without logging in there we are in trouble :)");

		NaviTestView ntv2 = new NaviTestView();
		ntv2.setText("Wow! you made it to the second view. Get yourself a beer - preferably a Krusovice Cerne");

		ntv2.removeButton();

        UI.getCurrent().getNavigator().addView(FIRST_VIEW, ntv1);
        UI.getCurrent().getNavigator().addView(SECOND_VIEW, ntv2);

	}

	@Override
	public String getFirstViewName() {
		return FIRST_VIEW;
	}

}
