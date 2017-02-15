package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

@SpringComponent
@Scope("prototype")
public class RedirectAfterLoginView extends CustomComponent implements View{

	private static final long serialVersionUID = 7678509076808950380L;

	@Autowired
	private DistributionTableView distributionTableView = null;

	@Override
	public void enter(ViewChangeEvent event) {
	    //navigate to table view
		UI.getCurrent().getNavigator().removeView(DistributionEditorUtil.VIEW_TABLE);
		UI.getCurrent().getNavigator().addView(DistributionEditorUtil.VIEW_TABLE, distributionTableView);
	    UI.getCurrent().getNavigator().navigateTo(DistributionEditorUtil.VIEW_TABLE);

	    DistributionEditorUtil.clearSessionAttributes();
	    distributionTableView.openDistributionSettings();
	}

}
