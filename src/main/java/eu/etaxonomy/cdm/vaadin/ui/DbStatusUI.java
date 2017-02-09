package eu.etaxonomy.cdm.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.servlet.CdmVaadinConversationalServlet;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.RedirectAfterLoginView;

@Theme("macosx")
@Title("CDM Board")
@SpringUI(path="/app/distribution")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DbStatusUI extends AbstractAuthenticatedUI{

    private final static Logger logger = Logger.getLogger(DbStatusUI.class);

	private static final String FIRST_VIEW = "firstView";

	@WebServlet(urlPatterns = {"/*"}, name="DbStatusUIServlet", asyncSupported = true)
	public static class Servlet extends CdmVaadinConversationalServlet {
	}

	public DbStatusUI () {
	    logger.debug("constructor");
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
