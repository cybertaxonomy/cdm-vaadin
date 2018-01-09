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
public class CdmFilterablePagingProvider<T extends IdentifiableEntity, V extends T> implements FilterablePagingProvider<V>, FilterableCountProvider {

    /**
     *
     */
    private static final List<String> DEFAULT_INIT_STRATEGY = Arrays.asList("$");

    private int pageSize = 20;

    private IIdentifiableEntityService<T> service;

    private Class<V> type = null;

    private MatchMode matchMode = MatchMode.ANYWHERE;

    private List<OrderHint> orderHints = OrderHint.ORDER_BY_TITLE_CACHE.asList();

    List<String> initStrategy = DEFAULT_INIT_STRATEGY;


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
     * With defaults for matchMode = MatchMode.ANYWHERE and orderHints = OrderHint.ORDER_BY_TITLE_CACHE
     *
     */
    public CdmFilterablePagingProvider(IIdentifiableEntityService<T> service) {
        this(service, null);
    }

    /**
     * With defaults for matchMode = MatchMode.ANYWHERE and orderHints = OrderHint.ORDER_BY_TITLE_CACHE
     *
     */
    public CdmFilterablePagingProvider(IIdentifiableEntityService<T> service, Class<V> type) {
        super();
        this.type = type;
        this.service = service;
    }


    public CdmFilterablePagingProvider(IIdentifiableEntityService<T> service, MatchMode matchMode, List<OrderHint> orderHints) {
        this(service, null, matchMode, orderHints);
    }

    public <S extends T> CdmFilterablePagingProvider(IIdentifiableEntityService<T> service, Class<V> type, MatchMode matchMode, List<OrderHint> orderHints) {
        super();
        this.type = type;
        this.service = service;
        this.matchMode = matchMode;
        this.orderHints = orderHints;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<V> findEntities(int firstRow, String filter) {

        Pager<V> page = (Pager<V>) service.findByTitle(
                type,
                filter,
                matchMode,
                null,
                pageSize,
                firstRow,
                orderHints,
                initStrategy
              );
        return page.getRecords();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(String filter) {

        Pager<V> page = (Pager<V>) service.findByTitle(
                type,
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

    /**
     * @return the initStrategy
     */
    public List<String> getInitStrategy() {
        return initStrategy;
    }

    /**
     * @param initStrategy the initStrategy to set
     */
    public void setInitStrategy(List<String> initStrategy) {
        this.initStrategy = initStrategy;
    }

}
