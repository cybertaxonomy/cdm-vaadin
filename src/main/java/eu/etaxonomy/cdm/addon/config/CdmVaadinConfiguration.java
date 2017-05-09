/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.addon.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Lazy;

import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.opt.config.DataSourceConfigurer;
import eu.etaxonomy.cdm.vaadin.security.annotation.EnableAnnotationBasedAccessControl;
import eu.etaxonomy.cdm.vaadin.ui.ConceptRelationshipUI;
import eu.etaxonomy.cdm.vaadin.ui.DistributionStatusUI;
import eu.etaxonomy.cdm.vaadin.ui.InactiveUIException;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUI;
import eu.etaxonomy.cdm.vaadin.ui.StatusEditorUI;
import eu.etaxonomy.cdm.vaadin.util.ConfigFileUtils;
import eu.etaxonomy.vaadin.ui.annotation.EnableVaadinSpringNavigation;

/**
 *
 * @author a.kohlbecker
 * @since Feb 8, 2017
 *
 */
@Configuration
@ComponentScan(basePackages={
        "eu.etaxonomy.vaadin.ui",
        "eu.etaxonomy.cdm.vaadin",
        "eu.etaxonomy.cdm.dataInserter",
        "eu.etaxonomy.cdm.service",
        "eu.etaxonomy.cdm.mock" // FIXME remove once mocks are no longer needed
        },
        // exclude UI classes, these are provided via the @Bean annotated methods below
        excludeFilters={@Filter(
                pattern="eu\\.etaxonomy\\.cdm\\.vaadin\\.ui\\..*",
                type=FilterType.REGEX
                )
            })
@EnableVaadin   // this imports VaadinConfiguration
@EnableVaadinSpringNavigation // activate the NavigationManagerBean
@EnableAnnotationBasedAccessControl // enable annotation based per view access control
public class CdmVaadinConfiguration {

    /**
     *
     */
    private static final String CDM_VAADIN_UI_ACTIVATED = "cdm-vaadin.ui.activated";

    public static final Logger logger = Logger.getLogger(CdmVaadinConfiguration.class);

    @Autowired
    @Lazy
    //FIXME consider to set the instanceName (instanceID) in the spring environment to avoid a bean reference here
    private DataSourceConfigurer dataSourceConfigurer;

    /*
     * NOTE: It is necessary to map the URLs starting with /VAADIN/* since none of the
     * @WebServlets is mapped to the root path. It is sufficient to configure one of the
     * servlets with this path see BookOfVaadin 5.9.5. Servlet Mapping with URL Patterns
     */
    @WebServlet(value = {"/app/*", "/VAADIN/*"}, asyncSupported = true)
    public static class Servlet extends SpringVaadinServlet {

        private static final long serialVersionUID = -2615042297393028775L;

        /**
         *
        @SuppressWarnings("serial")
        @Override
        protected void servletInitialized() throws ServletException {
            getService().addSessionInitListener(new SessionInitListener() {

                @Override
                public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {
                    VaadinSession session = sessionInitEvent.getSession();
                    session.setErrorHandler(new DefaultErrorHandler(){

                        @Override
                        public void error(ErrorEvent errorEvent) {
                            if(errorEvent.getThrowable() instanceof InactiveUIException){
                                //TODO redirect to an ErrorUI or show and error Page
                                // better use Spring MVC Error handlers instead?
                            } else {
                                doDefault(errorEvent);
                            }
                        }

                    });

                }});

        }
         */

    }

    public CdmVaadinConfiguration() {
        logger.debug("CdmVaadinConfiguration enabled");
    }

    @Bean
    @UIScope
    public ConceptRelationshipUI conceptRelationshipUI() throws InactiveUIException {
        if(isUIEnabled(ConceptRelationshipUI.class)){
            return new ConceptRelationshipUI();
        }
        return null;
    }

    @Bean
    @UIScope
    public RegistrationUI registrationUI() throws InactiveUIException {
        if(isUIEnabled(RegistrationUI.class)){
            return new RegistrationUI();
        }
        return null;
    }

    @Bean
    @UIScope
    public DistributionStatusUI distributionStatusUI() throws InactiveUIException {
        if(isUIEnabled(DistributionStatusUI.class)){
            return new DistributionStatusUI();
        }
        return null;
    }

    @Bean
    @UIScope
    public StatusEditorUI statusEditorUI() throws InactiveUIException {
        if(isUIEnabled(StatusEditorUI.class)){
            return new StatusEditorUI();
        }
        return null;
    }


    /**
     * Checks if the ui class supplied is activated by listing it in the properties by its {@link SpringUI#path()} value.
     *
     * @param type
     * @return
     * @throws InactiveUIException
     */
    private boolean isUIEnabled(Class<? extends UI>uiClass) throws InactiveUIException {
        String path = uiClass.getAnnotation(SpringUI.class).path();

        try {
            Properties appProps = ConfigFileUtils.getApplicationProperties(dataSourceConfigurer.dataSourceProperties().getCurrentDataSourceId());
            if(appProps.get(CDM_VAADIN_UI_ACTIVATED) != null){
                String[] uiPaths = appProps.get(CDM_VAADIN_UI_ACTIVATED).toString().split("\\s*,\\s*");
                return Arrays.asList(uiPaths).stream().anyMatch(p -> p.equals(path));
            }
            throw new InactiveUIException(path);
        } catch (IOException e) {
            logger.error("Error reading the vaadin ui properties file. File corrupted?. Stopping instance ...");
            throw new RuntimeException(e);
        }
    }


}
