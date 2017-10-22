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

import eu.etaxonomy.cdm.api.application.ICdmRepository;
import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.ICommonService;
import eu.etaxonomy.cdm.api.service.IDescriptionService;
import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.IPreferenceService;
import eu.etaxonomy.cdm.api.service.IReferenceService;
import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.api.service.IVocabularyService;

/**
 * This helper relates to the problem that in the Vaadin framework it is
 * not possible to autowire beans from the underlying application context
 * as Vaadin prevents this possibility. To overcome this problem, this singleton
 * helper class has
 * been written to retrieve the beans given the bean name.
 *
 * @author c.mathew
 *
 * TODO This class may no longer needed in a couple of cases since vaadin-spring
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

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }

    public Object getBean(Class clazz) {
        return context.getBean(clazz);
    }

    public <T extends IService> T getService(Class<T> clazz) {
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

    public static ICdmRepository getApplicationConfiguration() {
        return (ICdmRepository) getCurrent().getBean("cdmRepository");
    }
    public static ITaxonService getTaxonService() {
        return (ITaxonService)getCurrent().getBean(ITaxonService.class);
    }

    public static ITaxonNodeService getTaxonNodeService() {
        return (ITaxonNodeService)getCurrent().getBean(ITaxonNodeService.class);
    }

    public static IReferenceService getReferenceService() {
        return (IReferenceService)getCurrent().getBean(IReferenceService.class);
    }

    public static INameService getNameService() {
        return (INameService)getCurrent().getBean(INameService.class);
    }

    public static ICommonService getCommonService() {
        return (ICommonService)getCurrent().getBean(ICommonService.class);
    }

    public static IClassificationService getClassificationService() {
        return (IClassificationService)getCurrent().getBean(IClassificationService.class);
    }

    public static IVocabularyService getVocabularyService() {
        return (IVocabularyService)getCurrent().getBean(IVocabularyService.class);
    }

    public static ITermService getTermService() {
        return (ITermService)getCurrent().getBean(ITermService.class);
    }

    public static IDescriptionService getDescriptionService() {
        return (IDescriptionService)getCurrent().getBean(IDescriptionService.class);
    }

    public static IPreferenceService getPreferenceService() {
        return (IPreferenceService)getCurrent().getBean(IPreferenceService.class);
    }


}
