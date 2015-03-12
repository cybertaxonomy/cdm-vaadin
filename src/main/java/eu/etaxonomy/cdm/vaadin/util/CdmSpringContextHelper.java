package eu.etaxonomy.cdm.vaadin.util;

import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.VaadinServlet;

import eu.etaxonomy.cdm.vaadin.container.CdmSpringConnectionPool;

public class CdmSpringContextHelper {

    private final ApplicationContext context;

    private static CdmSpringContextHelper contextHelper;

    private CdmSpringContextHelper(ServletContext servletContext) {
        context = WebApplicationContextUtils.
                getRequiredWebApplicationContext(servletContext);

    }


    public static CdmSpringContextHelper newInstance() {
    	if(VaadinServlet.getCurrent() == null || VaadinServlet.getCurrent().getServletContext() == null) {
    		throw new RuntimeException("Vaadin Servlet or Vaadin Servlet Context not initialized");
    	}

    	if(contextHelper == null) {
    		ServletContext sc = VaadinServlet.getCurrent().getServletContext();
    		contextHelper = new CdmSpringContextHelper(sc);
    		return contextHelper;
    	} else {
    		return contextHelper;
    	}
    }

    public Object getBean(final String beanRef) {
        return context.getBean(beanRef);
    }

    public static JDBCConnectionPool getConnectionPool() throws SQLException {
        DataSource bean = (DataSource) newInstance().getBean("dataSource");
        JDBCConnectionPool connectionPool = new CdmSpringConnectionPool(bean.getConnection());
        return connectionPool;
    }
}
