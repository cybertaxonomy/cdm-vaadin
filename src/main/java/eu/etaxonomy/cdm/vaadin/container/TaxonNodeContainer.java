package eu.etaxonomy.cdm.vaadin.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;
	public static final String LABEL = "label";

	private final List<String> nodeInitStrategy = Arrays.asList(new String[]{
	        "taxon.sec",
	        "taxon.name",
	        "classification"
	});

	/**
	 * Creates a new taxon node container
	 * @param parentNode the parent node which will <b>not</b> be included
	 * in the result but only its child nodes
	 */
	public TaxonNodeContainer(TaxonNode parentNode) {
		addContainerProperty(LABEL, String.class, "[no taxon]");
		if(parentNode==null){
            //just load classifications
            IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
            List<Classification> classificationList = classificationService.listClassifications(null, null, null, nodeInitStrategy);
            for (Classification classification : classificationList) {
                TaxonNode rootNode = classification.getRootNode();
                Item item = addItem(rootNode);
                if(rootNode.getClassification()!=null){
                    item.getItemProperty(LABEL).setValue(rootNode.getClassification().getName().getText());
                }
            }
        }
		else{
		    addItem(parentNode);
		}
//		getTaxonNodeList(parentNode);
	}

	@Override
	public Collection<?> getChildren(Object itemId) {
	    List<TaxonNode> childNodes = ((TaxonNode) itemId).getChildNodes();
	    for (TaxonNode taxonNode : childNodes) {
            if(!containsId(taxonNode)){
                setParent(taxonNode, itemId);
            }
        }
        return childNodes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object itemId) {
	    return ((TaxonNode) itemId).getParent();
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object itemId) {
        return !((TaxonNode)itemId).getChildNodes().isEmpty();
    }

	public void getTaxonNodeList(TaxonNode parentNode) {
		List<TaxonNode> nodes = new ArrayList<TaxonNode>();


		if(parentNode==null){
			//just load classifications
			IClassificationService classificationService = CdmSpringContextHelper.getClassificationService();
			List<Classification> classificationList = classificationService.listClassifications(null, null, null, nodeInitStrategy);
			for (Classification classification : classificationList) {
			    TaxonNode rootNode = classification.getRootNode();
			    Item item = addItem(rootNode);
				setChildrenAllowed(rootNode, false);
				nodes.add(rootNode);
                if(rootNode.getClassification()!=null){
                    item.getItemProperty(LABEL).setValue(rootNode.getClassification().getName().getText());
                }
			}
		}
		else{
			//load child nodes
	        addChildNodes(parentNode);
		}
	}

	private void addChildNodes(TaxonNode parentNode) {
		List<TaxonNode> childNodes = parentNode.getChildNodes();
		setChildrenAllowed(parentNode, !childNodes.isEmpty());
		boolean hasValidChildren = false;
		for (TaxonNode taxonNode : childNodes) {
			if(taxonNode!=null && taxonNode.getTaxon()!=null && taxonNode.getTaxon().getName()!=null){
				Rank rank = taxonNode.getTaxon().getName().getRank();
				if(rank!=null && rank.isHigher(Rank.SPECIES())){
				    Item item = addItem(taxonNode);
					setParent(taxonNode, parentNode);
					if(taxonNode.getTaxon()!=null){
					    item.getItemProperty(LABEL).setValue(taxonNode.getTaxon().getName().getTitleCache());
					}
					else if(taxonNode.getClassification()!=null){
                        item.getItemProperty(LABEL).setValue(taxonNode.getClassification().getName().getText());
                    }
					hasValidChildren = true;

					addChildNodes(taxonNode);
				}
			}
		}
		setChildrenAllowed(parentNode, hasValidChildren);
	}

}
