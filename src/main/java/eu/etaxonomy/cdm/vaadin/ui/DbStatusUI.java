package eu.etaxonomy.cdm.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.DistributionSelectionPresenter;
import eu.etaxonomy.cdm.vaadin.servlet.CdmVaadinConversationalServlet;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionSelectionView;

@Theme("macosx")
@Title("CDM Board")
@SuppressWarnings("serial")
public class DbStatusUI extends AbstractAuthenticatedUI{

	//    @WebServlet(value = "/*", asyncSupported = true, initParams = {
	//			@WebInitParam(name="org.atmosphere.cpr.asyncSupport", value="org.atmosphere.container.Jetty9AsyncSupportWithWebSocket")
	//	})

	private static final String FIRST_VIEW = "selection";


	@WebServlet(value = {"/app/dbstatus/*"}, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DbStatusUI.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
	public static class Servlet extends CdmVaadinConversationalServlet {
	}


	@Override
	protected void doInit() {
		Navigator navigator = UI.getCurrent().getNavigator();
		DistributionSelectionView dsv = new DistributionSelectionView();
		new DistributionSelectionPresenter(dsv);
		navigator.addView("selection", dsv);


	}

	@Override
	public String getFirstViewName() {
		return FIRST_VIEW;
	}


}
