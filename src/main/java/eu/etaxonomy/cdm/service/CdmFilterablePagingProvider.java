/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.api.service.IIdentifiableEntityService;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public class CdmFilterablePagingProvider<T extends IdentifiableEntity, V extends T> implements FilterablePagingProvider<V>, FilterableCountProvider {


    private static final List<String> DEFAULT_INIT_STRATEGY = Arrays.asList("$");

    public static final String QUERY_STRING_PLACEHOLDER = "{query-string}";

    private static final Logger logger = Logger.getLogger(CdmFilterablePagingProvider.class);

    private int pageSize = 20;

    private IIdentifiableEntityService<T> service;

    private Class<V> type = null;

    private MatchMode matchMode = MatchMode.ANYWHERE;

    private List<OrderHint> orderHints = OrderHint.ORDER_BY_TITLE_CACHE.asList();

    List<String> initStrategy = DEFAULT_INIT_STRATEGY;

    private List<Criterion> criteria = new ArrayList<>();

    private List<Restriction<?>> restrictions = new ArrayList<>();


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

        // Logger.getLogger("org.hibernate.SQL").setLevel(Level.TRACE);
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

        // Logger.getLogger("org.hibernate.SQL").setLevel(Level.TRACE);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<V> findEntities(int firstRow, String filter) {

        checkNotMixed();

        Integer pageIndex = firstRow / pageSize;
        Pager<V> page;
        if(!restrictions.isEmpty()){
            List<Restriction<?>> preparedRestrictions = prepareRestrictions(filter, matchMode);
            page = (Pager<V>) service.findByTitleWithRestrictions(
                    type,
                    filter,
                    matchMode,
                    preparedRestrictions,
                    pageSize,
                    pageIndex ,
                    orderHints,
                    initStrategy
                    );
        } else {
            page = (Pager<V>) service.findByTitle(
                    type,
                    filter,
                    matchMode,
                    criteria,
                    pageSize,
                    pageIndex ,
                    orderHints,
                    initStrategy
                    );
        }

        if(logger.isTraceEnabled()){
            logger.trace("findEntities() - page: " + page.getCurrentIndex() + "/" + page.getPagesAvailable() + " totalRecords: " + page.getCount() + "\n" + page.getRecords());
        }
        return page.getRecords();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public int size(String filter) {

        checkNotMixed();

        Pager<V> page;
        if(!restrictions.isEmpty()){
            List<Restriction<?>> preparedRestrictions = prepareRestrictions(filter, matchMode);
            page = (Pager<V>) service.findByTitleWithRestrictions(
                    type,
                    filter,
                    matchMode,
                    preparedRestrictions,
                    1,
                    0,
                    null,
                    null
                  );
        } else {
            page = (Pager<V>) service.findByTitle(
                    type,
                    filter,
                    matchMode,
                    criteria,
                    1,
                    0,
                    null,
                    null
                  );
        }

        if(logger.isTraceEnabled()){
            logger.trace("size() -  count: " + page.getCount().intValue());
        }
        return page.getCount().intValue();
    }

    /**
     * @return
     */
    private List<Restriction<?>> prepareRestrictions(String filter, MatchMode matchMode) {
        List<Restriction<?>> prepared = new ArrayList<>(restrictions.size());
        for(Restriction<?> r : restrictions) {
            List<Object> values = new ArrayList<>(r.getValues().size());
            for(Object v : r.getValues()){
                if(v instanceof String){
                    String expandedValue = ((String)v).replace(QUERY_STRING_PLACEHOLDER, matchMode.queryStringFrom(filter));
                    values.add(expandedValue);
                } else {
                    values.add(v);
                }
            }
            prepared.add(new Restriction(r.getPropertyName(), r.getOperator(), r.getMatchMode(), values.toArray(new String[values.size()])));
        }
        return prepared;
    }

    /**
     *
     */
    protected void checkNotMixed() {
        if(!restrictions.isEmpty() && !criteria.isEmpty()){
            throw new RuntimeException("Citeria and Restrictions must not be used at the same time");
        }
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

    /**
     * The list of criteria is initially empty.
     *
     * @return the criteria
     */
    public List<Criterion> getCriteria() {
        return criteria;
    }

    public void addCriterion(Criterion criterion){
        criteria.add(criterion);
    }

    /**
     * The list of restrictions is initially empty.
     * <p>
     * Occurrences of the {@link QUERY_STRING_PLACEHOLDER} in the value
     * of String type Restrictions will be replaced by the <code>filter</code> parameter passed to the paging provider.
     *
     *
     * @return the restrictions
     */
    public List<Restriction<?>> getRestrictions() {
        return restrictions;
    }

    /**
     * Occurrences of the {@link QUERY_STRING_PLACEHOLDER} in the value
     * of String type Restrictions will be replaced by the <code>filter</code> parameter passed to the paging provider.
     *
     * @param restriction
     */
    public void addRestriction(Restriction<?> restriction){
        restrictions.add(restriction);
    }
}
