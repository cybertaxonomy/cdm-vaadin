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
import java.util.List;
import java.util.Properties;

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationProvider;
import org.vaadin.spring.events.annotation.EnableEventBus;

import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.application.AbstractDataInserter;
import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.application.DummyDataInserter;
import eu.etaxonomy.cdm.api.cache.CdmCacher;
import eu.etaxonomy.cdm.api.config.ApplicationConfiguration;
import eu.etaxonomy.cdm.api.config.ApplicationConfigurationFile;
import eu.etaxonomy.cdm.api.service.idminter.RegistrationIdentifierMinter;
import eu.etaxonomy.cdm.cache.CdmTransientEntityCacher;
import eu.etaxonomy.cdm.dataInserter.RegistrationRequiredDataInserter;
import eu.etaxonomy.cdm.persistence.hibernate.GrantedAuthorityRevokingRegistrationUpdateLister;
import eu.etaxonomy.cdm.vaadin.security.annotation.EnableAnnotationBasedAccessControl;
import eu.etaxonomy.cdm.vaadin.ui.ConceptRelationshipUI;
import eu.etaxonomy.cdm.vaadin.ui.DistributionStatusUI;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUI;
import eu.etaxonomy.cdm.vaadin.ui.StatusEditorUI;
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
        "eu.etaxonomy.cdm.service",
        "org.springframework.context.event"
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
@EnableEventBus // enable the vaadin spring event bus
public class CdmVaadinConfiguration implements ApplicationContextAware  {

    public static final String CDM_VAADIN_UI_ACTIVATED = "cdm-vaadin.ui.activated";
    public static final String CDM_SERVICE_MINTER_REGSTRATION_MINID = "cdm.service.minter.registration.minLocalId";
    public static final String CDM_SERVICE_MINTER_REGSTRATION_MAXID = "cdm.service.minter.registration.maxLocalId";
    public static final String CDM_SERVICE_MINTER_REGSTRATION_IDFORMAT = "cdm.service.minter.registration.identifierFormatString";

    public static final Logger logger = Logger.getLogger(CdmVaadinConfiguration.class);

    @Autowired
    Environment env;

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private ApplicationConfiguration appConfig;

    @Autowired
    private void  setTermCacher(CdmCacher termCacher){
        CdmTransientEntityCacher.setDefaultCacher(termCacher);
    }

    private boolean registrationUiHibernateEventListenersDone = false;


    ApplicationConfigurationFile configFile = new ApplicationConfigurationFile(PROPERTIES_FILE_NAME, APP_FILE_CONTENT);

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
                            // ...
                        }

                    });
                    ).getServiceRegistry().getService

                }});

        }
         */

    }

    public CdmVaadinConfiguration() {
        logger.debug("CdmVaadinConfiguration enabled");
    }

    @Bean
    @UIScope
    public ConceptRelationshipUI conceptRelationshipUI() {
        if(isUIEnabled(ConceptRelationshipUI.class)){
            return new ConceptRelationshipUI();
        }
        return null;
    }

    @Bean
    @UIScope
    public RegistrationUI registrationUI() {
        if(isUIEnabled(RegistrationUI.class)){
            registerRegistrationUiHibernateEventListeners();

            return new RegistrationUI();
        }
        return null;
    }

    /**
     * this is only a quick implementation for testing,
     * TODO see also the NOTE on CdmListenerIntegrator class declaration for a prospective better solution
     */
    protected void registerRegistrationUiHibernateEventListeners() {
        if(!registrationUiHibernateEventListenersDone){
            EventListenerRegistry listenerRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(
                    EventListenerRegistry.class);
            GrantedAuthorityRevokingRegistrationUpdateLister listener = new GrantedAuthorityRevokingRegistrationUpdateLister();
            listenerRegistry.appendListeners(EventType.POST_UPDATE, listener);
            registrationUiHibernateEventListenersDone = true;
        }
    }

    @Bean
    public AbstractDataInserter registrationRequiredDataInserter() throws BeansException{
        if(isUIEnabled(RegistrationUI.class)){
            RegistrationRequiredDataInserter inserter = new RegistrationRequiredDataInserter();

            inserter.setRunAsAuthenticationProvider((AuthenticationProvider) applicationContext.getBean("runAsAuthenticationProvider"));
            inserter.setCdmRepository((CdmRepository) applicationContext.getBean("cdmRepository"));
            return inserter;
        } else {
            // the return type implements ApplicationListener and thus must not be null,
            // therefore we return a empty dummy implementation.
            return new DummyDataInserter();
        }
    }

    @Bean
    public RegistrationIdentifierMinter registrationIdentifierMinter() throws IOException {
        RegistrationIdentifierMinter minter = new RegistrationIdentifierMinter();

        minter.setMinLocalId(appConfig.getProperty(configFile , CDM_SERVICE_MINTER_REGSTRATION_MINID));
        minter.setMaxLocalId(appConfig.getProperty(configFile , CDM_SERVICE_MINTER_REGSTRATION_MAXID));
        minter.setIdentifierFormatString(appConfig.getProperty(configFile , CDM_SERVICE_MINTER_REGSTRATION_IDFORMAT));
        return minter;
    }

    @Bean
    @UIScope
    public DistributionStatusUI distributionStatusUI() {
        if(isUIEnabled(DistributionStatusUI.class)){
            return new DistributionStatusUI();
        }
        return null;
    }

    @Bean
    @UIScope
    public StatusEditorUI statusEditorUI() {
        if(isUIEnabled(StatusEditorUI.class)){
            return new StatusEditorUI();
        }
        return null;
    }




    static final String PROPERTIES_FILE_NAME = "vaadin-apps";

    private Properties appProps = null;

    private ApplicationContext applicationContext;

    private List<String> activeUIpaths;

    //@formatter:off
    private static final String APP_FILE_CONTENT=
            "########################################################\n"+
            "#                                                       \n"+
            "# Vaadin application specific configurations            \n"+
            "#                                                       \n"+
            "########################################################\n"+
            "                                                        \n"+
            "# Enablement of vaadin uis.                             \n"+
            "#                                                       \n"+
            "# Multiple uis can be defined as comma separated list.  \n"+
            "# Whitespace before and after the comma will be ignored.\n"+
            "# Valid values are the path properties of the @SpringUI \n"+
            "# annotation which is used for UI classes.              \n"+
            "cdm-vaadin.ui.activated=concept,distribution,editstatus \n";
    //@formatter:on

    /**
     * Checks if the ui class supplied is activated by listing it in the properties by its {@link SpringUI#path()} value.
     *
     * TODO see https://dev.e-taxonomy.eu/redmine/issues/7139 (consider using spring profiles to enable vaadin UI contexts)
     *
     * @param type
     * @return
     */
    private boolean isUIEnabled(Class<? extends UI>uiClass) {

        String path = uiClass.getAnnotation(SpringUI.class).path().trim();

        if(activeUIpaths == null){
            String activatedVaadinUIs = env.getProperty(CDM_VAADIN_UI_ACTIVATED);
            if(activatedVaadinUIs == null){
                // not in environment? Read it from the config file!
                activatedVaadinUIs = appConfig.getProperty(configFile , CDM_VAADIN_UI_ACTIVATED);
            }

            if(activatedVaadinUIs != null) {
                String[] uiPaths = activatedVaadinUIs.split("\\s*,\\s*");
                this.activeUIpaths = Arrays.asList(uiPaths);
            }
        }
        if(activeUIpaths.stream().anyMatch(p -> p.trim().equals(path))){
            return true;
        }
        return false;

    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
