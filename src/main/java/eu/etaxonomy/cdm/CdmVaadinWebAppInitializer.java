/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Servlet 3.0+ environment WebApplicationInitializer implementation to replace the WEB-INF/web.xml
 *
 * Once this initializer is {@ #ENABLED} you need to delete or rename the WEB-INF/web.xml.
 *
 * For end to end integration tests another setup is needed. See {@link eu.etaxonomy.cdm.vaadin.WebAppIntegrationTestContextInitializer
 * WebAppIntegrationTestContextInitializer}
 *
 * @author a.kohlbecker
 * @since Mar 18, 2019
 *
 */
public class CdmVaadinWebAppInitializer implements WebApplicationInitializer {

    // Once this initializer is enabled you need to delete or rename the WEB-INF/web.xml.
    static final boolean ENABLED = false;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        if(!ENABLED){
            return;
        }

        XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
        rootContext.setConfigLocation("file:./src/main/webapp/WEB-INF/applicationContext.xml");

        // Manage the lifecycle of the root application context
        servletContext.addListener(new ContextLoaderListener(rootContext));

        // Register Filters
        servletContext.addFilter("charsetFilter#1",
                new org.springframework.web.filter.CharacterEncodingFilter("URF-8",  true));
//        servletContext.addFilter("springSecurityFilterChain#1",
//                new org.springframework.web.filter.DelegatingFilterProxy());
        // TODO servletContext.getFilterRegistration("charsetFilter#1").addMappingForUrlPatterns(arg0, arg1, "/*");
        // TODO servletContext.getFilterRegistration("springSecurityFilterChain#1").addMappingForUrlPatterns(arg0, arg1, "/*");

        // Register and map the servlets
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
                "cdm-vaadin",
                new DispatcherServlet(rootContext)
                );
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");


    }
}
