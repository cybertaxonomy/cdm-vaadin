package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.SimpleJDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;
import com.vaadin.data.util.sqlcontainer.query.TableQuery;

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

public class CdmSQLContainer extends SQLContainer {
	
	JDBCConnectionPool pool;
	
	
	public CdmSQLContainer(QueryDelegate delegate) throws SQLException {
		super(delegate);		
	}
	
	public static CdmSQLContainer newInstance(String tableName, String user, String password) {
		// TODO Auto-generated constructor stub
		JDBCConnectionPool pool;
		try {
			pool = new SimpleJDBCConnectionPool(
			        "org.h2.Driver",
			        "jdbc:h2:mem:cdm;MVCC=TRUE;IGNORECASE=TRUE", user, password, 2, 5);
			
//			pool = new SimpleJDBCConnectionPool(
//			        "com.mysql.jdbc.Driver",
//			        "jdbc:mysql://127.0.0.1/local-cyprus", user, password, 2, 5);
			
			TableQuery tq = new TableQuery(tableName, pool);
			tq.setVersionColumn("OPTLOCK");
			return new CdmSQLContainer(tq);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
