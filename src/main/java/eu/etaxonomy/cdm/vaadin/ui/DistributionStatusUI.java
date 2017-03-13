package eu.etaxonomy.cdm.vaadin.ui;

import org.apache.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.distributionStatus.RedirectAfterLoginView;

@Theme("macosx")
@Title("Distribution Editor")
@SpringUI(path="distribution")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DistributionStatusUI extends AbstractAuthenticatedUI{

    private final static Logger logger = Logger.getLogger(DistributionStatusUI.class);

	private static final String FIRST_VIEW = "firstView";

	public DistributionStatusUI () {
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
