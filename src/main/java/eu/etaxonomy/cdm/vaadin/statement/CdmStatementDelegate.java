// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.statement;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.sqlcontainer.RowItem;
import com.vaadin.data.util.sqlcontainer.SQLUtil;
import com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;


/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class CdmStatementDelegate implements FreeformStatementDelegate {

    private List<Filter> filters;
    private List<OrderBy> orderBys;

    private final String select_query;
    private final String count_query;
    private final String contains_query;

    public CdmStatementDelegate(String select_query, String count_query, String contains_query) {
        this.select_query = select_query;
        this.count_query = count_query;
        this.contains_query = contains_query;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#getQueryString(int, int)
     */
    @Override
    public String getQueryString(int offset, int limit) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Use getQueryStatement method.");
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#getCountQuery()
     */
    @Override
    public String getCountQuery() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Use getCountStatement method.");
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#setFilters(java.util.List)
     */
    @Override
    public void setFilters(List<Filter> filters) throws UnsupportedOperationException {
        this.filters = filters;
    }


    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#setOrderBy(java.util.List)
     */
    @Override
    public void setOrderBy(List<OrderBy> orderBys) throws UnsupportedOperationException {
        this.orderBys = orderBys;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#storeRow(java.sql.Connection, com.vaadin.data.util.sqlcontainer.RowItem)
     */
    @Override
    public int storeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#removeRow(java.sql.Connection, com.vaadin.data.util.sqlcontainer.RowItem)
     */
    @Override
    public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformQueryDelegate#getContainsRowQueryString(java.lang.Object[])
     */
    @Override
    public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Use getContainsRowQueryStatement method.");
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate#getQueryStatement(int, int)
     */
    @Override
    public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(select_query);
        if (filters != null) {
            String filterString = QueryBuilder.getWhereStringForFilters(filters, sh);
            query.append(filterString);
        }
        query.append(getOrderByString());
        if (offset != 0 || limit != 0) {
            query.append(" LIMIT ").append(limit);
            query.append(" OFFSET ").append(offset);
        }
        sh.setQueryString(query.toString());
        return sh;
    }

    private String getOrderByString() {
        StringBuffer orderBuffer = new StringBuffer("");
        if (orderBys != null && !orderBys.isEmpty()) {
            orderBuffer.append(" ORDER BY ");
            OrderBy lastOrderBy = orderBys.get(orderBys.size() - 1);
            for (OrderBy orderBy : orderBys) {
                orderBuffer.append(SQLUtil.escapeSQL(orderBy.getColumn()));
                if (orderBy.isAscending()) {
                    orderBuffer.append(" ASC");
                } else {
                    orderBuffer.append(" DESC");
                }
                if (orderBy != lastOrderBy) {
                    orderBuffer.append(", ");
                }
            }
        }
        return orderBuffer.toString();
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate#getCountStatement()
     */
    @Override
    public StatementHelper getCountStatement() throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(count_query);
        if (filters != null) {
            String filterString = QueryBuilder.getWhereStringForFilters(filters, sh);
            query.append(filterString);
        }
        sh.setQueryString(query.toString());
        return sh;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.sqlcontainer.query.FreeformStatementDelegate#getContainsRowQueryStatement(java.lang.Object[])
     */
    @Override
    public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException {
        StatementHelper sh = new StatementHelper();
        StringBuffer query = new StringBuffer(contains_query);
        sh.addParameterValue(keys[0]);
        sh.setQueryString(query.toString());
        return sh;
    }

//    public static String getWhereStringForFilters(List<Filter> filters,
//            StatementHelper sh) {
//        if (filters == null || filters.isEmpty()) {
//            return "";
//        }
//        StringBuilder where = new StringBuilder(" WHERE ");
//        where.append(getJoinedFilterString(filters, "AND", sh));
//        return where.toString();
//    }
//
//    public static String getJoinedFilterString(Collection<Filter> filters,
//            String joinString, StatementHelper sh) {
//        StringBuilder result = new StringBuilder();
//        for (Filter f : filters) {
//            result.append(getWhereStringForFilter(f, sh));
//            result.append(" ").append(joinString).append(" ");
//        }
//        // Remove the last instance of joinString
//        result.delete(result.length() - joinString.length() - 2,
//                result.length());
//        return result.toString();
//    }
//
//    public static String getWhereStringForFilter(Filter filter, StatementHelper sh) {
//        Compare compare = (Compare) filter;
//        sh.addParameterValue(compare.getValue());
//        String prop = compare.getPropertyId().toString();
//        switch (compare.getOperation()) {
//        case EQUAL:
//            return prop + " = ?";
//        case GREATER:
//            return prop + " > ?";
//        case GREATER_OR_EQUAL:
//            return prop + " >= ?";
//        case LESS:
//            return prop + " < ?";
//        case LESS_OR_EQUAL:
//            return prop + " <= ?";
//        default:
//            return "";
//        }
//    }

}
