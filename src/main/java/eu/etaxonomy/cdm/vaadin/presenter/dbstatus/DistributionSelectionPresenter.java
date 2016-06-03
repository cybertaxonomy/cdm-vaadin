package eu.etaxonomy.cdm.vaadin.presenter.dbstatus;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionSelectionView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionTableView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.IDistributionSelectionComponent;

public class DistributionSelectionPresenter implements IDistributionSelectionComponent.DistributionSelectionComponentListener {

	DistributionSelectionView view;

	public DistributionSelectionPresenter(DistributionSelectionView dsv) {
		this.view = dsv;
		view.addListener(this);
		view.dataBinding();
	}

	@Override
	public void buttonClick(TermVocabulary<DefinedTermBase> term, TaxonNode taxonNode) throws SQLException {
	    VaadinSession.getCurrent().setAttribute("taxonNodeUuid", taxonNode.getUuid());
	    VaadinSession.getCurrent().setAttribute("selectedTerm", term.getUuid());

	    DistributionTableView dtv = new DistributionTableView();
	    new DistributionTablePresenter(dtv);
	    UI.getCurrent().getNavigator().addView("table", dtv);
	    //navigate to table view
        UI.getCurrent().getNavigator().navigateTo("table");
	}

	@Override
	public List<Classification> getClassificationList() {
		IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
		List<Classification> classificationList = classificationService.listClassifications(null, null, null, NODE_INIT_STRATEGY());
		return classificationList;
	}

	@Override
	public List<TermVocabulary<DefinedTermBase>> getNamedAreaList() {

		IVocabularyService vocabularyService = CdmSpringContextHelper.getVocabularyService();
		List<TermVocabulary<DefinedTermBase>> termList = vocabularyService.findByTermType(TermType.NamedArea);
		return termList;
	}

	 private List<String> NODE_INIT_STRATEGY(){
	        return Arrays.asList(new String[]{
	            "taxon.sec",
	            "taxon.name",
	            "classification"
	    });}
}
