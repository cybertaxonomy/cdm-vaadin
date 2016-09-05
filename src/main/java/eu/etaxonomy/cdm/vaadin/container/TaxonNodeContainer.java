package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.ui.Tree;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;
	public static final String LABEL = "label";
	
	private Tree tree;
	
	/**
	 * Creates a new taxon node container
	 * @param parentNode the parent node which will <b>not</b> be included
	 * in the result but only its child nodes
	 */
	public TaxonNodeContainer(TaxonNode parentNode, Tree tree) {
		this.tree = tree;
		List<TaxonNode> taxonNodeList = getTaxonNodeList(parentNode, tree);
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
	
	public List<TaxonNode> getTaxonNodeList(TaxonNode parentNode, Tree tree) {
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
				TaxonNode rootNode = classification.getRootNode();
				setChildrenAllowed(rootNode, false);
				nodes.add(rootNode);
			}
		}
		else{
			//load child nodes
			nodes.addAll(addChildNodes(parentNode, tree));
		}
		return nodes;
	}

	private Collection<? extends TaxonNode> addChildNodes(TaxonNode parentNode, Tree tree) {
		tree.addItem(parentNode);
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();
		List<TaxonNode> childNodes = parentNode.getChildNodes();
		setChildrenAllowed(parentNode, !childNodes.isEmpty());
		for (TaxonNode taxonNode : childNodes) {
			if(taxonNode!=null && taxonNode.getTaxon()!=null && taxonNode.getTaxon().getName()!=null){
				Rank rank = taxonNode.getTaxon().getName().getRank();
				if(rank!=null && rank.isHigher(Rank.SPECIES())){
					nodes.add(taxonNode);
					tree.setParent(taxonNode, parentNode);
					addChildNodes(taxonNode, tree);
				}
				else{
					tree.setChildrenAllowed(parentNode, false);
				}
			}
		}
		return nodes;
	}
	
}
