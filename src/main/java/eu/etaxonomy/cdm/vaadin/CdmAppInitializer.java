package eu.etaxonomy.cdm.vaadin;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class CdmAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {      
    	//FIXME : This config seems to be identical to the web.xml config, but it does not work
    	
//    	XmlWebApplicationContext appContext = new XmlWebApplicationContext();
//        appContext.setConfigLocation("/WEB-INF/applicationContext.xml");        
//        servletContext.addListener(new ContextLoaderListener(appContext));        
//              
//        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("cdm-vaadin", DispatcherServlet.class);        
//        dispatcher.setLoadOnStartup(1);
//        dispatcher.addMapping("/");
    }

}
