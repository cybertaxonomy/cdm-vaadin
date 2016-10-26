package eu.etaxonomy.cdm.vaadin.container;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.persistence.dto.UuidAndTitleCache;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;
	public static final String LABEL = "titleCache";
	private Set<Object> itemCache = new HashSet<Object>();


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
	public TaxonNodeContainer(UuidAndTitleCache<TaxonNode> parentNode) {
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item addItem(Object itemId) {
	    if(itemId instanceof UuidAndTitleCache){
	        if(!itemCache.contains(((UuidAndTitleCache<TaxonNode>) itemId).getId())){
	            return super.addItem(itemId);
	        }
        } else {
            return super.addItem(itemId);
        }
	    return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean setParent(Object itemId, Object newParentId) {
	    itemCache.add(((UuidAndTitleCache<TaxonNode>) itemId).getId());;
	    return super.setParent(itemId, newParentId);
	}

}
