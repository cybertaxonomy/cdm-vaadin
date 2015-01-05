package eu.etaxonomy.cdm.vaadin.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.DistributionSelectionPresenter;
import eu.etaxonomy.cdm.vaadin.servlet.CdmVaadinConversationalServlet;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionSelectionView;

@Theme("mytheme")
@SuppressWarnings("serial")
public class DbStatusUI extends AbstractAuthenticatedUI{

	//    @WebServlet(value = "/*", asyncSupported = true, initParams = {
	//			@WebInitParam(name="org.atmosphere.cpr.asyncSupport", value="org.atmosphere.container.Jetty9AsyncSupportWithWebSocket")
	//	})

	private static final String FIRST_VIEW = "selection";
	
	@WebServlet(value = {"/app/dbstatus/*", "/VAADIN/*"}, asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DbStatusUI.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
	public static class Servlet extends CdmVaadinConversationalServlet {
	}


	@Override
	protected void doInit() {
		DistributionSelectionView dsv = new DistributionSelectionView();
		new DistributionSelectionPresenter(dsv);
		UI.getCurrent().getNavigator().addView("selection", dsv);
	}

	@Override
	public String getFirstViewName() {
		return FIRST_VIEW;
	}


}
