/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.container;

import java.sql.Connection;
import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

/**
 * @author cmathew
 * @date 9 Mar 2015
 *
 * @deprecated unused! should be deleted
 */
@Deprecated
public class CdmSpringConnectionPool implements JDBCConnectionPool {

    private final Connection conn;

    public CdmSpringConnectionPool(Connection conn) {
        this.conn = conn;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void releaseConnection(Connection conn) {
    }

    @Override
    public Connection reserveConnection() throws SQLException {
        return conn;
    }
}
