// $Id$
/**
* Copyright (C) 2016 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container.Hierarchical;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;

/**
 * @author pplitzner
 * @date 18.10.2016
 *
 */
public class TaxonTreeContainer extends CdmSQLContainer implements Hierarchical{

    private static final long serialVersionUID = 5488629563366944491L;

    private final Collection<RowId> rootItemIds = new HashSet<>();
    private final Map<Object, List<Object>> parentChildMap = new HashMap<>();
    private final Map<Object, Object> childParentMap = new HashMap<>();
    private CdmSQLContainer childrenContainer;

    public TaxonTreeContainer(TaxonNode parentNode) throws SQLException {
        super(CdmQueryFactory.generateTaxonTreeQuery("Name", Integer.toString(parentNode.getClassification().getId())));
        childrenContainer = new CdmSQLContainer(CdmQueryFactory.generateTaxonTreeQuery("Name", Integer.toString(parentNode.getClassification().getId())));
        List<TaxonNode> childNodes = parentNode.getChildNodes();
        for (TaxonNode taxonNode : childNodes) {
            rootItemIds.add(new RowId(taxonNode.getId()));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> getChildren(Object itemId) {
        List<Object> children = parentChildMap.get(itemId);
        if(children==null){
            children = updateChildren(itemId);
        }
        return children;
    }

    private List<Object> updateChildren(Object itemId) {
        List<Object> children;
        Filter childrenOfTaxonFilter = new Compare.Equal("tn.parent_id", Integer.valueOf(itemId.toString()));
        childrenContainer.addContainerFilter(childrenOfTaxonFilter);
        children = new ArrayList<>();
        Collection<?> itemIds = childrenContainer.getItemIds();
        for (Object object : itemIds) {
            childParentMap.put(object, itemId);
            children.add(object);
        }
        childrenContainer.removeAllContainerFilters();

        parentChildMap.put(itemId, children);
        return children;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParent(Object itemId) {
        return childParentMap.get(itemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasChildren(Object itemId) {
        List<Object> children = parentChildMap.get(itemId);
        if(children==null){
            children = updateChildren(itemId);
        }
        return !children.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<?> rootItemIds() {
        return rootItemIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {
        return hasChildren(itemId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRoot(Object itemId) {
        return itemId==null?true:rootItemIds.contains(itemId);
    }



}
