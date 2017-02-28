/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.addon.config;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.vaadin.devday.ui.annotation.EnableVaadinSpringNavigation;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.server.SpringVaadinServlet;

/**
 *
 * @author a.kohlbecker
 * @since Feb 8, 2017
 *
 */
@Configuration
@ComponentScan(basePackages={
        "eu.etaxonomy.cdm.vaadin",
        "com.vaadin.devday.ui"
        })
@EnableVaadin   // this imports VaadinConfiguration
@EnableVaadinSpringNavigation // activate the NavigationManagerBean
public class CdmVaadinConfiguration {

    public static final Logger logger = Logger.getLogger(CdmVaadinConfiguration.class);

    /*
     * NOTE: It is necessary to map the URLs starting with /VAADIN/* since none of the
     * @WebServlets is mapped to the root path. It is sufficient to configure one of the
     * servlets with this path see BookOfVaadin 5.9.5. Servlet Mapping with URL Patterns
     */
    @WebServlet(value = {"/app/*", "/VAADIN/*"}, asyncSupported = true)
    public static class Servlet extends SpringVaadinServlet {
    }

    public CdmVaadinConfiguration() {
        logger.debug("CdmVaadinConfiguration enabled");
    }


}
