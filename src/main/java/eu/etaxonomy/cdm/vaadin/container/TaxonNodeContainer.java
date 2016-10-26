package eu.etaxonomy.cdm.vaadin.container;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.persistence.dto.UuidAndTitleCache;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;
	public static final String LABEL = "titleCache";
	private Set<Object> itemCache = new HashSet<Object>();

	/**
	 * Creates a new taxon node container
	 * @param parentNode the parent node which will <b>not</b> be included
	 * in the result but only its child nodes
	 */
	public TaxonNodeContainer(UuidAndTitleCache<TaxonNode> parentNode) {
		addContainerProperty(LABEL, String.class, "[no taxon]");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item addItem(Object itemId) {
	    if(itemId instanceof UuidAndTitleCache){
	        UuidAndTitleCache<TaxonNode> uuidAndTitleCache = (UuidAndTitleCache<TaxonNode>) itemId;
            if(!itemCache.contains(uuidAndTitleCache.getId())){
	            Item item = super.addItem(itemId);
	            item.getItemProperty(TaxonNodeContainer.LABEL).setValue(uuidAndTitleCache.getTitleCache());
                return item;
	        }
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
