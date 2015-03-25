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

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Compare.Equal;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.statement.CdmStatementDelegate;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.CdmSQLStringDecorator;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class StatusPresenter implements StatusComponentListener {

    public static final String NAME_ID = "Name";
    public static final String PB_ID = "Pb";
    public static final String FN_ID = "Fn";
    public static final String UNP_ID = "Unp";
    public static final String UNR_ID = "Unr";

    private static final String FROM_QUERY = " FROM TaxonNode tn inner join TaxonBase tb on tn.taxon_id=tb.id inner join TaxonNameBase tnb on tb.name_id=tnb.id  inner join DefinedTermBase dtb on tnb.rank_id=dtb.id";
    private static final String SELECT_QUERY="SELECT tb.id as taxon_id, tnb.titleCache as " + NAME_ID + " , tb.publish as " + PB_ID + " , tb.unplaced as " +  UNP_ID + FROM_QUERY;
    private static final String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;

    private static final String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

    private final IStatusComposite composite;

    private LeafNodeTaxonContainer container;

    private Equal nrFilter, unpFilter, unfFilter, unpbFilter;
    private SimpleStringFilter nameFilter;

    private int totalNoOfItems = 0;

    public StatusPresenter(IStatusComposite composite) {
        this.composite = composite;
        composite.setListener(this);
        initFilters();
        // TODO: Need to evaluate the various sql dialects and make sure that these
        // queries are compatible with all
        QueryBuilder.setStringDecorator(new CdmSQLStringDecorator());
    }

    private void initFilters() {
        //nrFilter = new Compare.Equal(StatusPresenter.UNR_ID, true);
        unpFilter = new Compare.Equal("tb.unplaced", true);
        //unfFilter = new Compare.Equal(StatusPresenter.FN_ID, false);
        unpbFilter = new Compare.Equal("tb.publish", false);
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#load()
     */
    @Override
    public LeafNodeTaxonContainer loadTaxa(int classificationId) throws SQLException {
        FreeformQuery query = new FreeformQuery("This query is not used", CdmSpringContextHelper.getConnectionPool(), "taxon_id");
        CdmStatementDelegate cdmStatementDelegate = new CdmStatementDelegate(SELECT_QUERY, COUNT_QUERY, CONTAINS_QUERY);
        query.setDelegate(cdmStatementDelegate);

        Filter rankFilter = new Compare.Equal("dtb.titleCache","Species");
        Filter classifcationFilter = new Compare.Equal("tn.classification_id",classificationId);

        container = new LeafNodeTaxonContainer(query);
        //container.addContainerFilter(rankFilter);
        container.addContainerFilter(classifcationFilter);
        totalNoOfItems = container.size();
        return container;
    }

    @Override
    public void setUnplacedFilter() {
        container.addContainerFilter(unpFilter);
    }

    @Override
    public void removeUnplacedFilter() {
        container.removeContainerFilter(unpFilter);
    }

    @Override
    public void setUnpublishedFilter() {
        container.addContainerFilter(unpbFilter);
    }

    @Override
    public void removeUnpublishedFilter() {
        container.removeContainerFilter(unpbFilter);
    }

    @Override
    public void setNameFilter(String filterString) {
        removeNameFilter();
        nameFilter = new SimpleStringFilter("tnb.titleCache", filterString, true, true);
        container.addContainerFilter(nameFilter);
    }

    @Override
    public void removeNameFilter() {
        container.removeContainerFilter(nameFilter);
    }

    @Override
    public int getCurrentSize() {
        return container.size();
    }

    @Override
    public int getSize() {
        return totalNoOfItems;
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#loadClassifications()
     */
    @Override
    public CdmSQLContainer loadClassifications() throws SQLException {
        CdmSQLContainer container = CdmSQLContainer.newInstance("Classification");
        return container;
    }

}
