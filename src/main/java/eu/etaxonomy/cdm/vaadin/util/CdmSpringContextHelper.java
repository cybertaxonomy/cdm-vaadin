package eu.etaxonomy.cdm.vaadin.util;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;

public class CdmSpringContextHelper {

    private ApplicationContext context;
    
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
}
