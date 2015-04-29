// $Id$
/**
 * Copyright (C) 2015 EDIT
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
import java.util.Set;

import org.apache.log4j.Logger;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.data.util.sqlcontainer.RowItem;

import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class LeafNodeTaxonContainer extends CdmSQLContainer implements Container.Hierarchical  {

    private static final Logger logger = Logger.getLogger(LeafNodeTaxonContainer.class);


    public static final String NAME_ID = "Name";
    public static final String ACCTAXON_ID = "AccTaxonId";
    public static final String PB_ID = "Pb";
    public static final String FN_ID = "Fn";
    public static final String UNP_ID = "Unp";
    public static final String UNR_ID = "Unr";
    public static final String RANK_ID = "Rank";
    public static final String HAS_SYN_ID = "HasSynonyms";

    public Set<Filter> currentFilters;


    private Filter nrFilter, unpFilter, unfFilter, unpbFilter, rankFilter,  classificationFilter, synonymFilter, idFilter;
    private SimpleStringFilter nameFilter;

    private int classificationId = -1;

    private final Map<RowId, RowItem> synItems = new HashMap<RowId, RowItem>();

    private final Map<Object,List<Object>> taxonSynonymMap;

    private final CdmSQLContainer synonymContainer;


    /**
     * @param delegate
     * @throws SQLException
     */
    public LeafNodeTaxonContainer(int classificationId) throws SQLException {
        super(CdmQueryFactory.generateTaxonBaseQuery(NAME_ID, PB_ID, UNP_ID, RANK_ID, HAS_SYN_ID));
        this.synonymContainer = new CdmSQLContainer(CdmQueryFactory.generateSynonymofTaxonQuery(NAME_ID));
        this.synonymContainer.sort(new String[]{NAME_ID}, new boolean[]{true});
        this.classificationId = classificationId;
        taxonSynonymMap = new HashMap<Object,List<Object>>();
        initFilters();
        addContainerFilter(classificationFilter);
        //addContainerFilter(rankFilter);
    }

    private void initFilters() {
        //nrFilter = new Compare.Equal(StatusPresenter.UNR_ID, true);
        unpFilter = new Compare.Equal("tb.unplaced", true);
        //unfFilter = new Compare.Equal(StatusPresenter.FN_ID, false);
        unpbFilter = new Compare.Equal("tb.publish", false);
        classificationFilter = new Compare.Equal("tn.classification_id",classificationId);
        rankFilter = new Compare.Equal("dtb.titleCache","Species");
        synonymFilter = new Not(new IsNull("sr.relatedto_id"));

        currentFilters = new HashSet<Filter>();
    }



    public void setUnplacedFilter() {
        addContainerFilter(unpFilter);
    }


    public void removeUnplacedFilter() {
        removeContainerFilter(unpFilter);
    }


    public void setUnpublishedFilter() {
        addContainerFilter(unpbFilter);
    }


    public void removeUnpublishedFilter() {
        removeContainerFilter(unpbFilter);
    }


    public void setNameFilter(String filterString) {
        removeNameFilter();
        nameFilter = new SimpleStringFilter("tnb.titleCache", filterString, true, true);
        addContainerFilter(nameFilter);
    }


    public void removeNameFilter() {
        removeContainerFilter(nameFilter);
    }

    public void setIdFilter(Object itemId) {
        removeIdFilter();
        idFilter = new Compare.Equal("tb.id", itemId.toString());
        addContainerFilter(idFilter);
    }

    public void removeIdFilter() {
        removeContainerFilter(idFilter);
    }

    public void removeDynamicFilters() {
        removeUnplacedFilter();
        removeUnpublishedFilter();
        removeNameFilter();
        removeIdFilter();

    }
    public int getTotalNoOfTaxa() {
        return size();
    }


    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#getChildren(java.lang.Object)
     */
    @Override
    public Collection<?> getChildren(Object itemId) {
        List<Object> synList = taxonSynonymMap.get(itemId);
        if(synList != null) {
            return synList;
        }

        return addToSynonymCache(itemId);
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#getParent(java.lang.Object)
     */
    @Override
    public Object getParent(Object itemId) {
        return null;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#rootItemIds()
     */
    @Override
    public Collection<?> rootItemIds() {
        return getItemIds();
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#setParent(java.lang.Object, java.lang.Object)
     */
    @Override
    public boolean setParent(Object itemId, Object newParentId) throws UnsupportedOperationException {
        return true;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#areChildrenAllowed(java.lang.Object)
     */
    @Override
    public boolean areChildrenAllowed(Object itemId) {

        Property hasSynProperty = getItem(itemId).getItemProperty(HAS_SYN_ID);
        if(hasSynProperty == null) {
            return false;
        }
        return (Long)hasSynProperty.getValue() > 0;


    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#setChildrenAllowed(java.lang.Object, boolean)
     */
    @Override
    public boolean setChildrenAllowed(Object itemId, boolean areChildrenAllowed) throws UnsupportedOperationException {
        return true;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#isRoot(java.lang.Object)
     */
    @Override
    public boolean isRoot(Object itemId) {
        return true;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.Container.Hierarchical#hasChildren(java.lang.Object)
     */
    @Override
    public boolean hasChildren(Object itemId) {
        return true;
    }

    public boolean isSynonym(Object itemId) {
        return synonymContainer.containsId(itemId);
    }

    public void removeTaxonFromCache(Object itemId) {
        taxonSynonymMap.remove(itemId);
    }

    public void refreshSynonymCache() {
        for(Object taxonItemId  : taxonSynonymMap.keySet()) {
            addToSynonymCache(taxonItemId);
        }
    }

    private List<Object> addToSynonymCache(Object taxonItemId) {
        Filter synonymOfTaxonFilter = new Compare.Equal("sr.relatedto_id", Integer.valueOf(taxonItemId.toString()));
        synonymContainer.addContainerFilter(synonymOfTaxonFilter);
        List<Object> synList = new ArrayList<Object>();
        synList.addAll(synonymContainer.getItemIds());
        for(Object synItemId : synList) {
            addSynItem((RowItem) synonymContainer.getItem(synItemId));
        }
        synonymContainer.removeAllContainerFilters();

        taxonSynonymMap.put(taxonItemId, synList);

        return synList;
    }

    @Override
    public Item getItem(Object itemId) {
        Item item = synItems.get(itemId);
        if(item == null) {
            item = super.getItem(itemId);
        }
        return item;
    }


    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        taxonSynonymMap.clear();
        synItems.clear();
        return super.removeAllItems();
    }

    @Override
    public void refresh() {
        synItems.clear();
        refreshSynonymCache();
        super.refresh();
    }

    public void addSynItem(RowItem rowItem) {
        synItems.put(rowItem.getId(), rowItem);

    }

}
