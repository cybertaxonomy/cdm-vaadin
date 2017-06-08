/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.Arrays;
import java.util.List;

import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.api.service.IIdentifiableEntityService;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public class CdmFilterablePagingProvider<T extends IdentifiableEntity> implements FilterablePagingProvider<T>, FilterableCountProvider {

    private int pageSize = 20;

    private IIdentifiableEntityService<T> service;

    private MatchMode matchMode = MatchMode.ANYWHERE;

    private List<OrderHint> orderHints = OrderHint.ORDER_BY_TITLE_CACHE.asList();


    /**
     * @return the matchMode
     */
    protected MatchMode getMatchMode() {
        return matchMode;
    }

    /**
     * @param matchMode the matchMode to set
     */
    protected void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    /**
     * @return the orderHints
     */
    protected List<OrderHint> getOrderHints() {
        return orderHints;
    }

    /**
     * @param orderHints the orderHints to set
     */
    protected void setOrderHints(List<OrderHint> orderHints) {
        this.orderHints = orderHints;
    }

    /**
     * @param service
     */
    public CdmFilterablePagingProvider(IIdentifiableEntityService<T> service) {
        super();
        this.service = service;
    }

    /**
     * @param service
     * @param matchMode
     * @param orderHints
     */
    public CdmFilterablePagingProvider(IIdentifiableEntityService<T> service, MatchMode matchMode,
            List<OrderHint> orderHints) {
        super();
        this.service = service;
        this.matchMode = matchMode;
        this.orderHints = orderHints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<T> findEntities(int firstRow, String filter) {
        Pager<T> page = service.findByTitle(
                null,
                filter,
                matchMode,
                null,
                pageSize,
                firstRow,
                orderHints,
                Arrays.asList("$")
              );
        return page.getRecords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(String filter) {
        Pager<T> page = service.findByTitle(
                null,
                filter,
                matchMode,
                null,
                1,
                0,
                null,
                null
              );
        return page.getCount().intValue();
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

}
