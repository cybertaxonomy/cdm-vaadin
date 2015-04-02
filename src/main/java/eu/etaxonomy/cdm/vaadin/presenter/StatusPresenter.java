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

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmSQLStringDecorator;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class StatusPresenter implements StatusComponentListener {

    private final IStatusComposite composite;

    private LeafNodeTaxonContainer container;

    private ITaxonService taxonService;

    private int totalNoOfTaxa = 0;

    public StatusPresenter(IStatusComposite composite) {
        this.composite = composite;
        composite.setListener(this);
        // TODO: Need to evaluate the various sql dialects and make sure that these
        // queries are compatible with all
        QueryBuilder.setStringDecorator(new CdmSQLStringDecorator());

        initServices();
    }

    private void initServices() {
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
        container = new LeafNodeTaxonContainer(classificationId);
        totalNoOfTaxa = container.getTotalNoOfTaxa();
        return container;
    }

    @Override
    public void refresh() {
        container.refresh();
    }
    @Override
    public void setUnplacedFilter() {
        container.setUnplacedFilter();
    }

    @Override
    public void removeUnplacedFilter() {
        container.removeUnplacedFilter();
    }

    @Override
    public void setUnpublishedFilter() {
        container.setUnpublishedFilter();
    }

    @Override
    public void removeUnpublishedFilter() {
        container.removeUnpublishedFilter();
    }

    @Override
    public void setNameFilter(String filterString) {
        container.setNameFilter(filterString);
    }

    @Override
    public void removeNameFilter() {
        container.removeNameFilter();
    }

    @Override
    public int getCurrentNoOfTaxa() {
        return container.size();
    }

    @Override
    public int getTotalNoOfTaxa() {
        return totalNoOfTaxa;
    }

    @Override
    public void addChildren(Object parentItemId) {
        //container.addChildren(parentItemId);
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#loadClassifications()
     */
    @Override
    public CdmSQLContainer loadClassifications() throws SQLException {
        CdmSQLContainer container = CdmSQLContainer.newInstance("Classification");
        return container;
    }


    @Override
    public void updatePublished(boolean pb, Object itemId) {
        UUID uuid = UUID.fromString((String)container.getItem(itemId).getItemProperty(LeafNodeTaxonContainer.UUID_ID).getValue());
        Taxon taxon = CdmBase.deproxy(taxonService.load(uuid), Taxon.class);
        boolean currentPb = taxon.isPublish();
        if(currentPb != pb) {
            taxon.setPublish(pb);
            taxonService.merge(taxon);
        }
    }


}
