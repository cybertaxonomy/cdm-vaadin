package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.DistributionTablePresenter;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

public class RedirectAfterLoginView extends CustomComponent implements View{

	private static final long serialVersionUID = 7678509076808950380L;

	@Override
	public void enter(ViewChangeEvent event) {
	    //navigate to table view
		DistributionTableView distributionTableView = new DistributionTableView();
		new DistributionTablePresenter(distributionTableView);
		UI.getCurrent().getNavigator().addView(DistributionEditorUtil.VIEW_TABLE, distributionTableView);
	    UI.getCurrent().getNavigator().navigateTo(DistributionEditorUtil.VIEW_TABLE);

	    distributionTableView.openSettings();
	}

}
