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
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;

import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.statement.CdmStatementDelegate;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite.StatusComponentListener;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class StatusPresenter implements StatusComponentListener {

    private static final String FROM_QUERY = "FROM TaxonNode tn inner join TaxonBase tb on tn.taxon_id=tb.id inner join TaxonNameBase tnb on tb.name_id=tnb.id  inner join DefinedTermBase dtb on tnb.rank_id=dtb.id";
    private static final String SELECT_QUERY="SELECT tb.id as taxon_id, tnb.titleCache as Name, tb.publish as Pb " + FROM_QUERY;
    private static final String COUNT_QUERY = "SELECT count(*) " + FROM_QUERY;

    private static final String CONTAINS_QUERY = "SELECT * FROM TaxonBase tb WHERE tb.id = ?";

    private final IStatusComposite composite;

    public StatusPresenter(IStatusComposite composite) {
        this.composite = composite;
        composite.setListener(this);
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

        LeafNodeTaxonContainer container = new LeafNodeTaxonContainer(query);
        container.addContainerFilter(rankFilter);
        container.addContainerFilter(classifcationFilter);
        return container;
    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener#loadClassifications()
     */
    @Override
    public CdmSQLContainer loadClassifications() throws SQLException {
        CdmSQLContainer container = CdmSQLContainer.newInstance("Classification");
        container.addOrderBy(new OrderBy("titleCache", true));
        return container;
    }

}
