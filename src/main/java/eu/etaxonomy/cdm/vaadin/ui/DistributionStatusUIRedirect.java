package eu.etaxonomy.cdm.vaadin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.distributionStatus.RedirectAfterLoginView;

@Theme("edit")
@Title("CDM Distribution Status Editor")
// @SpringUI(path="distribution") // not needed since this UI is used in the context of DistributionStatusUI
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DistributionStatusUIRedirect extends AbstractAuthenticatedUI{

	private static final String FIRST_VIEW = "firstView";

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
