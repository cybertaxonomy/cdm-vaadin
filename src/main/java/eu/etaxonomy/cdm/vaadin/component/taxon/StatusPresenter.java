/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.taxon;

import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Compare.Equal;

import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonBase;
import eu.etaxonomy.cdm.vaadin.component.taxon.IStatusComposite.StatusComponentListener;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @since 10 Mar 2015
 *
 */
public class StatusPresenter implements StatusComponentListener {


    public final static String C_TCACHE_ID = "titleCache";

    private LeafNodeTaxonContainer leafNodeTaxonContainer;

    private CdmSQLContainer classificationContainer;

    private final ITaxonService taxonService;

    private int totalNoOfTaxa = 0;

    public StatusPresenter() {
        taxonService = CdmSpringContextHelper.getTaxonService();
    }


    @Override
    public void removeFilters() {
        removeUnplacedFilter();
        removeUnpublishedFilter();
        removeNameFilter();
    }

    @Override
    public LeafNodeTaxonContainer loadTaxa(int classificationId) throws SQLException {
        leafNodeTaxonContainer = new LeafNodeTaxonContainer(classificationId);
        totalNoOfTaxa = leafNodeTaxonContainer.getTotalNoOfTaxa();
        return leafNodeTaxonContainer;
    }

    @Override
    public Object getClassificationId(String classification) {
        if(classification == null) {
            return null;
        }
        Equal cnameFilter = new Compare.Equal("titleCache", classification);
        classificationContainer.addContainerFilter(cnameFilter);
        Collection<?> itemIds = classificationContainer.getItemIds();
        Object itemId = null;
        if(!itemIds.isEmpty()) {
            itemId = itemIds.iterator().next();
        }
        classificationContainer.removeContainerFilter(cnameFilter);
        return itemId;
    }

    @Override
    public void refresh() {
        leafNodeTaxonContainer.refresh();
    }
    @Override
    public void setUnplacedFilter() {
        leafNodeTaxonContainer.setUnplacedFilter();
    }

    @Override
    public void removeUnplacedFilter() {
        leafNodeTaxonContainer.removeUnplacedFilter();
    }

    @Override
    public void setUnpublishedFilter() {
        leafNodeTaxonContainer.setUnpublishedFilter();
    }

    @Override
    public void removeUnpublishedFilter() {
        leafNodeTaxonContainer.removeUnpublishedFilter();
    }

    @Override
    public void setNameFilter(String filterString) {
        leafNodeTaxonContainer.setNameFilter(filterString);
    }

    @Override
    public void removeNameFilter() {
        leafNodeTaxonContainer.removeNameFilter();
    }

    @Override
    public void setIdFilter(Object itemId) {
       leafNodeTaxonContainer.setIdFilter(itemId);
    }

    @Override
    public void removeIdFilter() {
        leafNodeTaxonContainer.removeDynamicFilters();
    }

    @Override
    public void removeDynamicFilters() {
        leafNodeTaxonContainer.removeDynamicFilters();
    }

    @Override
    public int getCurrentNoOfTaxa() {
        return leafNodeTaxonContainer.size();
    }

    @Override
    public int getTotalNoOfTaxa() {
        return totalNoOfTaxa;
    }

    @Override
    public boolean isSynonym(Object itemId) {
        return leafNodeTaxonContainer.isSynonym(itemId);
    }

    @Override
    public void refreshSynonymCache() {
        leafNodeTaxonContainer.refreshSynonymCache();
    }

    @Override
    public CdmSQLContainer loadClassifications() throws SQLException {
        classificationContainer = CdmSQLContainer.newInstance("Classification");
        return classificationContainer;
    }


    @Override
    public void updatePublished(boolean pb, Object itemId) {
        UUID uuid = UUID.fromString((String)leafNodeTaxonContainer.getItem(itemId).getItemProperty(CdmQueryFactory.UUID_ID).getValue());
        TaxonBase taxonBase  = taxonService.load(uuid);
        Taxon taxon = CdmBase.deproxy(taxonBase, Taxon.class);
        boolean currentPb = taxon.isPublish();
        if(currentPb != pb) {
            taxon.setPublish(pb);
            taxonService.merge(taxon);
        }
    }

    @Override
    public LeafNodeTaxonContainer getCurrentLeafNodeTaxonContainer() {
        return leafNodeTaxonContainer;
    }

    @Override
    public CdmSQLContainer getClassificationContainer() {
        return classificationContainer;
    }

}
