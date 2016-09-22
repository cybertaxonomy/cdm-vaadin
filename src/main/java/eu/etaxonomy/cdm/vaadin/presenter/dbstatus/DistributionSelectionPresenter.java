package eu.etaxonomy.cdm.vaadin.presenter.dbstatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionSelectionView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.DistributionTableView;
import eu.etaxonomy.cdm.vaadin.view.dbstatus.IDistributionSelectionComponent;

public class DistributionSelectionPresenter implements IDistributionSelectionComponent.DistributionSelectionComponentListener {

	private DistributionSelectionView view;

	public DistributionSelectionPresenter(DistributionSelectionView dsv) {
		this.view = dsv;
		view.addListener(this);
		view.dataBinding();
	}

	@Override
	public void buttonClick(TaxonNode taxonNode, TermVocabulary<DefinedTermBase> term) throws SQLException {
		DistributionTableView dtv = new DistributionTableView();
		new DistributionTablePresenter(dtv);
		UI.getCurrent().getNavigator().addView("table", dtv);

		DistributionEditorUtil.openDistributionView(taxonNode, term);
	}

	@Override
	public List<TaxonNode> getTaxonNodeList() {
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();

		IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
		ITaxonNodeService taxonNodeService = CdmSpringContextHelper.getTaxonNodeService();
		List<Classification> classificationList = classificationService.listClassifications(null, null, null, NODE_INIT_STRATEGY());
		for (Classification classification : classificationList) {
			nodes.add(classification.getRootNode());
		}
		for (Classification classification : classificationList) {
			List<TaxonNode> allNodesForClassification = taxonNodeService.listAllNodesForClassification(classification, null, null);
			for (TaxonNode taxonNode : allNodesForClassification) {
				if(taxonNode.getTaxon()!=null && taxonNode.getTaxon().getName()!=null && taxonNode.getTaxon().getName().getRank()!=null){
					Rank rank = taxonNode.getTaxon().getName().getRank();
					if(rank.isHigher(Rank.SPECIES()) || rank.equals(Rank.SPECIES())){
						nodes.add(taxonNode);
					}
				}
			}
		}
		return nodes;
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
