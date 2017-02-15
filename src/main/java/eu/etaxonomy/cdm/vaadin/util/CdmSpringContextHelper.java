package eu.etaxonomy.cdm.vaadin.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.VaadinServlet;

import eu.etaxonomy.cdm.api.service.ITaxonNodeService;

/**
 * This helper was related to the problem that in the Vaadin framework it was
 * not possible to autowire beans from the underlying application context
 * as Vaadin preventes this possibility. To overcome this problem, this singleton
 * helper class has been written to retrieve the beans given the bean name.
 * <p>
 * Now that <code>vaadin-spring</code> is being used this class is in principle
 * no longer needed. It never the less been kept for some time to continue
 * provide the J2EEConnectionPool. Once a clear concept exist about to handle and
 * inject it in a proper way this class can be removed completely.
 *
 * @author c.mathew
 *
 * TODO This class may no longer needed in a couple of cases since <code>vaadin-spring</code>
 * is being used and spring beans can be injected now.
 *
 */
public class CdmSpringContextHelper {

    private final ApplicationContext context;
    private final DataSource dataSource;

    private final JDBCConnectionPool connPool;
    private static CdmSpringContextHelper contextHelper;

    private static DatabaseMetaData databaseMetaData;

    private CdmSpringContextHelper(ServletContext servletContext) throws SQLException {
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);
        dataSource = (DataSource)getBean("dataSource");
        connPool = new J2EEConnectionPool(dataSource);

    }


    public static CdmSpringContextHelper getCurrent() {
        if(VaadinServlet.getCurrent() == null || VaadinServlet.getCurrent().getServletContext() == null) {
            throw new RuntimeException("Vaadin Servlet or Vaadin Servlet Context not initialized");
        }

        if(contextHelper == null) {
            ServletContext sc = VaadinServlet.getCurrent().getServletContext();
            try {
                contextHelper = new CdmSpringContextHelper(sc);
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return contextHelper;

    }

    private Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }
    
    private Object getBean(Class clazz) {
        return context.getBean(clazz);
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public JDBCConnectionPool getConnectionPool() {
        return connPool;
    }

//    public static JDBCConnectionPool createConnectionPool() {
//        return new J2EEConnectionPool(getCurrent().getDataSource());
//    }

//    public static JDBCConnectionPool getConnectionPool() {
//        return new J2EEConnectionPool(getCurrent().getDataSource());
//    }

    public static Connection getConnection() throws SQLException {
        return getCurrent().getDataSource().getConnection();
    }

    public static DatabaseMetaData getDatabaseMetaData() throws SQLException {
        if(databaseMetaData == null) {
            Connection conn = getConnection();
            databaseMetaData = conn.getMetaData();
            conn.close();
        }
        return databaseMetaData;
    }

    @Deprecated
    public static ITaxonNodeService getTaxonNodeService() {
        return (ITaxonNodeService)getCurrent().getBean(ITaxonNodeService.class);
    }
}
