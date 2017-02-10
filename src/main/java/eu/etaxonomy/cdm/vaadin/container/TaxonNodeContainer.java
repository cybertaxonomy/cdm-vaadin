package eu.etaxonomy.cdm.vaadin.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.persistence.dto.UuidAndTitleCache;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;

	public static final String LABEL = "titleCache";

	private Map<Object, Object> itemCache = new HashMap<>();

	@Autowired
	private ITaxonNodeService taxonNodeService;
	/**
     * Creates a new taxon node container
	 * @param roots the root elements of the table
	 */
	public TaxonNodeContainer(Collection<UuidAndTitleCache<TaxonNode>> roots) {
	    addContainerProperty(LABEL, String.class, "[no taxon]");
	    for (UuidAndTitleCache<TaxonNode> root: roots) {
	        addItem(root);
	        addChildItems(root);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item addItem(Object itemId) {
	    if(itemId instanceof UuidAndTitleCache){
	        UuidAndTitleCache<TaxonNode> uuidAndTitleCache = (UuidAndTitleCache<TaxonNode>) itemId;
	        Item item = super.addItem(itemId);
	        item.getItemProperty(TaxonNodeContainer.LABEL).setValue(uuidAndTitleCache.getTitleCache());
	        itemCache.put(((UuidAndTitleCache<TaxonNode>) itemId).getId(), false);
	        return item;
        }
	    return null;
	}

    /**
     * @param parent
     */
    public void addChildItems(UuidAndTitleCache<TaxonNode> parent) {
        if(itemCache.get(parent.getId()).equals(Boolean.FALSE)){
            Collection<UuidAndTitleCache<TaxonNode>> children = taxonNodeService.listChildNodesAsUuidAndTitleCache(parent);
            setChildrenAllowed(parent, !children.isEmpty());
            for (UuidAndTitleCache<TaxonNode> child : children) {
                Item childItem = addItem(child);
                if(childItem!=null){
                    setParent(child, parent);
                }
                Collection<UuidAndTitleCache<TaxonNode>> grandChildren = taxonNodeService.listChildNodesAsUuidAndTitleCache(child);
                setChildrenAllowed(child, !grandChildren.isEmpty());
            }
            itemCache.put(parent.getId(), true);
        }
    }

}
