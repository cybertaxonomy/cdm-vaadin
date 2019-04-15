/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm;

import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.jetty.JettyEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author a.kohlbecker
 * @since Mar 15, 2019
 *
 */
@Configuration
// @ServletComponentScan will help find the CdmVaadinConfiguration.CdmVaadinServlet
// this scan is only performed when using an embedded Servlet container, such as in the
// integration test case.
@ServletComponentScan
public class WebAppIntegrationTestConfig {

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        JettyEmbeddedServletContainerFactory factory =
                      new JettyEmbeddedServletContainerFactory();
        return factory;
     }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        WebAppIntegrationTestContextInitializer servletContextInitializer =
                      new WebAppIntegrationTestContextInitializer();
        return servletContextInitializer;
     }

}
