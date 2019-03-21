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

import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Servlet 3.0+ environment WebApplicationInitializer implementation for end to end integration tests.
 *
 * The root application context is defined via the {@link org.springframework.boot.test.SpringApplicationConfiguration @SpringApplicationConfiguration} annotation in the
 * {@link SelenideTestBase SelenideTestBase} test base class.
 *
 * For the servlet context to be correctly initialized the integration test requires a test configuration like {@link eu.etaxonomy.cdm.WebAppIntegrationTestConfig
 * WebAppIntegrationTestConfig} which is annotated with {@link ServletComponentScan @ServletComponentScan}, so that the {@link CdmVaadinConfiguration.CdmVaadinServlet
 * CdmVaadinConfiguration.CdmVaadinServlet} is found and added to the servlet context.
 *
 * Production mode contexts need a different setup, see {@link eu.etaxonomy.cdm.vaadin.CdmVaadinWebAppInitializer
 * CdmVaadinWebAppInitializer}
 *
 * @author a.kohlbecker
 * @since Mar 18, 2019
 *
 */
public class WebAppIntegrationTestContextInitializer implements ServletContextInitializer {

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        // Register Filters
        servletContext.addFilter("charsetFilter#1",
                new org.springframework.web.filter.CharacterEncodingFilter("URF-8",  true));
//        servletContext.addFilter("springSecurityFilterChain#1",
//                new org.springframework.web.filter.DelegatingFilterProxy());
        // TODO servletContext.getFilterRegistration("charsetFilter#1").addMappingForUrlPatterns(arg0, arg1, "/*");
        // TODO servletContext.getFilterRegistration("springSecurityFilterChain#1").addMappingForUrlPatterns(arg0, arg1, "/*");

        // the below lines will cause the cdm-remote-webapp context to be loaded from /WEB-INF/cdm-vaadin-servlet.xml
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("cdm-vaadin",
                //new DispatcherServlet(webAppContext)
                new DispatcherServlet()
                );
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");
    }

}
