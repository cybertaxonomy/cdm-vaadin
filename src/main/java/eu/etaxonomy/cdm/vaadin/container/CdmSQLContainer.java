package eu.etaxonomy.cdm.vaadin.container;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.vaadin.data.Property;
import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.SQLUtils;

public class CdmSQLContainer extends SQLContainer {

    private static final long serialVersionUID = 2828992538007070061L;

    private static final Logger logger = Logger.getLogger(CdmSQLContainer.class);

    JDBCConnectionPool pool;

    DatabaseMetaData databaseMetaData;

    public static final int DEFAULT_LIMIT = 5000;
    boolean checkPropertyIdCase = false;

    public CdmSQLContainer(QueryDelegate delegate) throws SQLException {
        super(delegate);
        databaseMetaData = CdmSpringContextHelper.getCurrent().getDatabaseMetaData();
        setPageLength(DEFAULT_LIMIT);
    }

    public static CdmSQLContainer newInstance(String tableName) throws SQLException {
        // TODO : currently the sql generator is for h2, need to make this compatible for all flavours
        //TableQuery tq = new TableQuery(tableName, CdmSpringContextHelper.getCurrent().getConnectionPool(), new DefaultSQLGenerator());
        //tq.setVersionColumn("updated");

        CdmSQLContainer container = new CdmSQLContainer(CdmQueryFactory.generateTableQuery(tableName));
        container.checkPropertyIdCase = true;
        return container;
    }



    public UUID getUuid(Object itemId) {
        return UUID.fromString((String)getProperty(itemId,CdmQueryFactory.UUID_ID).getValue());
    }

    public Property<?> getProperty(Object itemId, Object propertyId) {
        if(checkPropertyIdCase) {
            try {
                return getItem(itemId).getItemProperty(SQLUtils.correctCase(propertyId.toString(), databaseMetaData));
            } catch (SQLException e) {
                logger.warn("Error getting property", e);
                return null;
            }
        } else {
            return getItem(itemId).getItemProperty(propertyId);
        }
    }

}
