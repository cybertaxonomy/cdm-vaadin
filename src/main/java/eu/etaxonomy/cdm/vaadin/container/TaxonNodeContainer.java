package eu.etaxonomy.cdm.vaadin.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.vaadin.data.Item;
import com.vaadin.data.util.HierarchicalContainer;

import eu.etaxonomy.cdm.persistence.dto.TaxonNodeDto;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class TaxonNodeContainer extends HierarchicalContainer {

	private static final long serialVersionUID = 102401340698963360L;

	public static final String LABEL = "titleCache";

	private Map<Integer, Boolean> itemCache = new HashMap<>();

	/**
     * Creates a new taxon node container
	 * @param roots the root elements of the table
	 */
	public TaxonNodeContainer(Collection<TaxonNodeDto> roots) {
	    addContainerProperty(LABEL, String.class, "[no taxon]");
	    for (TaxonNodeDto root: roots) {
	        addItem(root);
	        addChildItems(root);
        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Item addItem(Object itemId) {
	    if(itemId instanceof TaxonNodeDto){
	        TaxonNodeDto dto = (TaxonNodeDto) itemId;
	        Item item = super.addItem(itemId);
	        item.getItemProperty(TaxonNodeContainer.LABEL).setValue(dto.getTitleCache());
	        itemCache.put(((TaxonNodeDto) itemId).getId(), false);
	        return item;
        }
	    return null;
	}

    /**
     * @param parent
     */
    public void addChildItems(TaxonNodeDto parent) {
        if(itemCache.get(parent.getId()).equals(Boolean.FALSE)){
            Collection<TaxonNodeDto> children = CdmSpringContextHelper.getTaxonNodeService().listChildNodesAsTaxonNodeDto(parent);
            setChildrenAllowed(parent, !children.isEmpty());
            for (TaxonNodeDto child : children) {
                Item childItem = addItem(child);
                if(childItem!=null){
                    setParent(child, parent);
                    addChildItems(child);
                }
                   /*
                Collection<UuidAndTitleCache<TaxonNode>> grandChildren = CdmSpringContextHelper.getTaxonNodeService().listChildNodesAsUuidAndTitleCache(child);
                setChildrenAllowed(child, !grandChildren.isEmpty());*/
            }
            itemCache.put(parent.getId(), true);
        }
    }

}
