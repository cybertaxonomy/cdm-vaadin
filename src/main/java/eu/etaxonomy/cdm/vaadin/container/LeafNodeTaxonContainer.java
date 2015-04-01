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
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.IsNull;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.RowItem;

import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class LeafNodeTaxonContainer extends CdmSQLContainer implements Container.Hierarchical  {

    private static final Logger logger = Logger.getLogger(LeafNodeTaxonContainer.class);

    public static final String ID = "Id";
    public static final String NAME_ID = "Name";
    public static final String ACCTAXON_ID = "AccTaxonId";
    public static final String PB_ID = "Pb";
    public static final String FN_ID = "Fn";
    public static final String UNP_ID = "Unp";
    public static final String UNR_ID = "Unr";
    public static final String RANK_ID = "Rank";
    public static final String HAS_SYN_ID = "HasSynonyms";

    public Set<Filter> currentFilters;


    private Filter nrFilter, unpFilter, unfFilter, unpbFilter, rankFilter,  classificationFilter, synonymFilter;
    private SimpleStringFilter nameFilter;

    private int classificationId = -1;

    private final Map<Object,List<Object>> taxonSynonymMap;

    private final CdmSQLContainer synonymContainer;


    /**
     * @param delegate
     * @throws SQLException
     */
    public LeafNodeTaxonContainer(int classificationId) throws SQLException {
        super(CdmQueryFactory.generateTaxonBaseQuery(ID, NAME_ID, PB_ID, UNP_ID, RANK_ID, HAS_SYN_ID));
        this.synonymContainer = new CdmSQLContainer(CdmQueryFactory.generateSynonymofTaxonQuery(ID, NAME_ID));
        this.classificationId = classificationId;
        taxonSynonymMap = new HashMap<Object,List<Object>>();
        initFilters();
        addContainerFilter(classificationFilter);
        addContainerFilter(rankFilter);
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
        //synonymContainer.disableContentsChangeEvents();
        try {
            Filter synonymOfTaxonFilter = new Compare.Equal("sr.relatedto_id", Integer.valueOf(itemId.toString()));
            synonymContainer.addContainerFilter(synonymOfTaxonFilter);
            synList = new ArrayList<Object>();
            synList.addAll(synonymContainer.getItemIds());
            for(Object synItemId : synList) {
                addRowItem((RowItem) synonymContainer.getItem(synItemId));
            }
            synonymContainer.removeAllContainerFilters();
            // cache the synonyms for later
            taxonSynonymMap.put(itemId, synList);

            return synList;
        } finally {
            //synonymContainer.enableContentsChangeEvents();
        }


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
//
//        disableContentsChangeEvents();
//        try {
//            Collection<?> taxontemIds = getItemIds();
//            return taxontemIds;
//        } finally {
//            enableContentsChangeEvents();
//        }

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



}
