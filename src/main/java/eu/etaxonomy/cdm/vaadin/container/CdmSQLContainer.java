package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;
import com.vaadin.data.util.sqlcontainer.query.generator.DefaultSQLGenerator;

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class CdmSQLContainer extends SQLContainer {

    JDBCConnectionPool pool;


    public CdmSQLContainer(QueryDelegate delegate) throws SQLException {
        super(delegate);
    }

    public static CdmSQLContainer newInstance(String tableName) throws SQLException {
        // TODO : currently the sql generator is for h2, need to make this compatible for all flavours
        TableQuery tq = new TableQuery(tableName,CdmSpringContextHelper.getConnectionPool(), new DefaultSQLGenerator());
        tq.setVersionColumn("updated");

        return new CdmSQLContainer(tq);

    }

}
