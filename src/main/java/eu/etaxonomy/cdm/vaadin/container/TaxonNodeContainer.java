package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends IndexedContainer {

	public static final String LABEL = "label";
	
	/**
	 * Creates a new taxon node container
	 * @param parentNode the parent node which will <b>not</b> be included
	 * in the result but only its child nodes
	 */
	public TaxonNodeContainer(TaxonNode parentNode) {
		List<TaxonNode> taxonNodeList = getTaxonNodeList(parentNode);
		addContainerProperty(LABEL, String.class, "[no taxon]");
		for (TaxonNode taxonNode : taxonNodeList) {
			Item item = addItem(taxonNode);
			if(taxonNode.getTaxon()!=null){
				item.getItemProperty(LABEL).setValue(taxonNode.getTaxon().getName().getTitleCache());
			}
			else if(taxonNode.getClassification()!=null){
				item.getItemProperty(LABEL).setValue(taxonNode.getClassification().getName().getText());
			}
		}
	}
	
	public List<TaxonNode> getTaxonNodeList(TaxonNode parentNode) {
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();
		
		List<String> nodeInitStrategy = Arrays.asList(new String[]{
	            "taxon.sec",
	            "taxon.name",
	            "classification"
	    });

		if(parentNode==null){
			//just load classifications
			IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
			List<Classification> classificationList = classificationService.listClassifications(null, null, null, nodeInitStrategy);
			for (Classification classification : classificationList) {
				nodes.add(classification.getRootNode());
			}
		}
		else{
			//load child nodes
			nodes.addAll(addChildNodes(parentNode));
		}
		return nodes;
	}

	private Collection<? extends TaxonNode> addChildNodes(TaxonNode parentNode) {
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();
		for (TaxonNode taxonNode : parentNode.getChildNodes()) {
			if(taxonNode!=null && taxonNode.getTaxon()!=null && taxonNode.getTaxon().getName()!=null){
				Rank rank = taxonNode.getTaxon().getName().getRank();
				if(rank!=null && rank.isHigher(Rank.SPECIES())){
					nodes.add(taxonNode);
					addChildNodes(taxonNode);
				}
			}
		}
		return nodes;
	}
	
}
