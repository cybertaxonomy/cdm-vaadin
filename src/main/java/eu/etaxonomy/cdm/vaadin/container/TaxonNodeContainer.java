package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends IndexedContainer {

	public static final String LABEL = "label";

	public TaxonNodeContainer() {
		List<TaxonNode> classificationList = getTaxonNodeList();
		addContainerProperty(LABEL, String.class, "[no taxon]");
		for (TaxonNode taxonNode : classificationList) {
			Item item = addItem(taxonNode);
			if(taxonNode.getTaxon()!=null){
				item.getItemProperty(LABEL).setValue(taxonNode.getTaxon().getName().getTitleCache());
			}
			else if(taxonNode.getClassification()!=null){
				item.getItemProperty(LABEL).setValue(taxonNode.getClassification().getName().getText());
			}
		}
	}
	
	public List<TaxonNode> getTaxonNodeList() {
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();
		
		List<String> nodeInitStrategy = Arrays.asList(new String[]{
	            "taxon.sec",
	            "taxon.name",
	            "classification"
	    });

		IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
		ITaxonNodeService taxonNodeService = CdmSpringContextHelper.getTaxonNodeService();
		List<Classification> classificationList = classificationService.listClassifications(null, null, null, nodeInitStrategy);
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
	
}
