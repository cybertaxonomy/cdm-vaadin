// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import java.sql.SQLException;
import java.util.UUID;

import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class StatusPresenter implements StatusComponentListener {



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

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#load()
     */
    @Override
    public LeafNodeTaxonContainer loadTaxa(int classificationId) throws SQLException {
        leafNodeTaxonContainer = new LeafNodeTaxonContainer(classificationId);
        totalNoOfTaxa = leafNodeTaxonContainer.getTotalNoOfTaxa();
        return leafNodeTaxonContainer;
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

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#loadClassifications()
     */
    @Override
    public CdmSQLContainer loadClassifications() throws SQLException {
        classificationContainer = CdmSQLContainer.newInstance("Classification");
        return classificationContainer;
    }


    @Override
    public void updatePublished(boolean pb, Object itemId) {
        UUID uuid = UUID.fromString((String)leafNodeTaxonContainer.getItem(itemId).getItemProperty(CdmQueryFactory.UUID_ID).getValue());
        Taxon taxon = CdmBase.deproxy(taxonService.load(uuid), Taxon.class);
        boolean currentPb = taxon.isPublish();
        if(currentPb != pb) {
            taxon.setPublish(pb);
            taxonService.merge(taxon);
        }
    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener#getCurrentLeafNodeTaxonContainer()
     */
    @Override
    public LeafNodeTaxonContainer getCurrentLeafNodeTaxonContainer() {
        return leafNodeTaxonContainer;
    }

    @Override
    public CdmSQLContainer getClassificationContainer() {
        return classificationContainer;
    }

}
