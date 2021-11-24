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

import javax.servlet.annotation.WebServlet;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.DeploymentConfiguration.LegacyProperyToStringMode;
import com.vaadin.spring.annotation.EnableVaadin;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.application.AbstractDataInserter;
import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.application.DummyDataInserter;
import eu.etaxonomy.cdm.api.application.IRunAs;
import eu.etaxonomy.cdm.api.application.RunAsAdmin;
import eu.etaxonomy.cdm.api.cache.CdmCacherBase;
import eu.etaxonomy.cdm.api.config.ApplicationConfiguration;
import eu.etaxonomy.cdm.api.config.ApplicationConfigurationFile;
import eu.etaxonomy.cdm.api.service.idminter.RegistrationIdentifierMinter;
import eu.etaxonomy.cdm.api.service.taxonGraph.TaxonGraphBeforeTransactionCompleteProcess;
import eu.etaxonomy.cdm.cache.CdmTransientEntityCacher;
import eu.etaxonomy.cdm.config.CdmHibernateListener;
import eu.etaxonomy.cdm.dataInserter.RegistrationRequiredDataInserter;
import eu.etaxonomy.cdm.persistence.hibernate.GrantedAuthorityRevokingRegistrationUpdateLister;
import eu.etaxonomy.cdm.persistence.hibernate.ITaxonGraphHibernateListener;
import eu.etaxonomy.cdm.vaadin.permission.annotation.EnableAnnotationBasedAccessControl;
import eu.etaxonomy.cdm.vaadin.ui.CdmBaseUI;
import eu.etaxonomy.cdm.vaadin.ui.ConceptRelationshipUI;
import eu.etaxonomy.cdm.vaadin.ui.DistributionStatusUI;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUI;
import eu.etaxonomy.cdm.vaadin.ui.StatusEditorUI;
import eu.etaxonomy.cdm.vaadin.ui.UserAccountSelfManagementUI;
import eu.etaxonomy.vaadin.ui.annotation.EnableVaadinSpringNavigation;

/**
 * @author a.kohlbecker
 * @since Feb 8, 2017
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
@CdmHibernateListener // enable the configuration which activates the TaxonGraphHibernateListener bean
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
    @Qualifier("runAsAuthenticationProvider")
    private AuthenticationProvider runAsAuthenticationProvider;

    @Autowired
    private ITaxonGraphHibernateListener taxonGraphHibernateListener;

    @Autowired
    private void  setTermCacher(CdmCacherBase termCacher){
        CdmTransientEntityCacher.setPermanentCacher(termCacher);
    }

    private boolean registrationUiHibernateEventListenersDone = false;


    ApplicationConfigurationFile configFile = new ApplicationConfigurationFile(PROPERTIES_FILE_NAME, APP_FILE_CONTENT);

    /*
     * NOTES:
     *
     * (1) It is necessary to map the URLs starting with /VAADIN/* since none of the
     * @WebServlets is mapped to the root path. It is sufficient to configure one of the
     * servlets with this path see BookOfVaadin 5.9.5. Servlet Mapping with URL Patterns
     */
    @VaadinServletConfiguration(
            productionMode = false,    // FIXME get value from application.properties or
                                       // use two different CdmVaadinServlets for
                                       // different spring profiles?
            ui = CdmBaseUI.class,      // TODO better default UI to use?
            // If the closeIdleSessions parameter for the servlet is
            // enabled (disabled by default), Vaadin closes the UIs and the session after the time specified
            // in the session-timeout parameter expires after the last non-heartbeat request.
            // For session-timeout see <session-config>
            // Legacy mode to return the value of the property as a string from AbstractProperty.toString()
            closeIdleSessions=true,
            legacyPropertyToStringMode=LegacyProperyToStringMode.ENABLED
            )
    @WebServlet(name="CdmVaadinServlet", value = {"/app/*", "/VAADIN/*"}, asyncSupported = true)
    public static class CdmVaadinServlet extends SpringVaadinServlet {

        private static final long serialVersionUID = 1L;

        /*
        @Override
        protected void servletInitialized() throws ServletException {

            super.servletInitialized();

            getService().addSessionInitListener(new SessionInitListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void sessionInit(SessionInitEvent sessionInitEvent) throws ServiceException {
                    VaadinSession session = sessionInitEvent.getSession();
                    session.setErrorHandler(new DefaultErrorHandler(){

                        private static final long serialVersionUID = 1L;

                        @Override
                        public void error(ErrorEvent errorEvent) {
                            UIDisabledException uiDisbledException = findUIDisabledException(errorEvent.getThrowable());
                            if(uiDisbledException != null) {
                                logger.error("################## > UIDisabledException");
                                //throw uiDisbledException ;
                                doDefault(errorEvent);
                            } else {
                                doDefault(errorEvent);
                            }
                        }

                        private UIDisabledException findUIDisabledException(Throwable throwable) {
                            if(throwable instanceof UIDisabledException) {
                                return (UIDisabledException)throwable;
                            } else {
                                if(throwable.getCause() == null) {
                                    return null;
                                } else {
                                    return findUIDisabledException(throwable.getCause());
                                }
                            }
                        }

                    });

                }});

                logger.debug("SpringVaadinServlet initialized");
        }
        */
    }

    public CdmVaadinConfiguration() {
        logger.debug("CdmVaadinConfiguration enabled");
    }

    @Bean
    @UIScope
    public ConceptRelationshipUI conceptRelationshipUI() {
        return applyEnableConfig(new ConceptRelationshipUI());
    }

    @Bean
    @UIScope
    public RegistrationUI registrationUI() {
        return applyEnableConfig(new RegistrationUI());
    }

    @Bean
    @UIScope
    public UserAccountSelfManagementUI userAccountSelfManagementUI() {
        return applyEnableConfig(new UserAccountSelfManagementUI());
    }

    @Bean
    @UIScope
    public DistributionStatusUI distributionStatusUI() {
        return applyEnableConfig( new DistributionStatusUI());
    }

    @Bean
    @UIScope
    public StatusEditorUI statusEditorUI() {
        return applyEnableConfig(new StatusEditorUI());
    }

    /**
     * this is only a quick implementation for testing,
     * TODO see also the NOTE on CdmListenerIntegrator class declaration for a prospective better solution
     */
    protected void registerRegistrationUiHibernateEventListeners() {
        if(!registrationUiHibernateEventListenersDone){
            EventListenerRegistry listenerRegistry = ((SessionFactoryImpl) sessionFactory).getServiceRegistry().getService(
                    EventListenerRegistry.class);

            listenerRegistry.appendListeners(EventType.POST_UPDATE, new GrantedAuthorityRevokingRegistrationUpdateLister());
            // TODO also POST_DELETE needed for GrantedAuthorityRevokingRegistrationUpdateLister?

            try {
                taxonGraphHibernateListener.registerProcessClass(TaxonGraphBeforeTransactionCompleteProcess.class, new Object[]{new RunAsAdmin(runAsAuthenticationProvider)}, new Class[]{IRunAs.class});
            } catch (NoSuchMethodException | SecurityException e) {
                // re-throw as RuntimeException as the context can not be created correctly
                throw new RuntimeException(e);
            }

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

    static final String PROPERTIES_FILE_NAME = "vaadin-apps";

    private ApplicationContext applicationContext;

    private List<String> activeUIpaths;

    //@formatter:off
    private static final String APP_FILE_CONTENT=
            "################################################################\n"+
            "#                                                               \n"+
            "# Vaadin application specific configurations                    \n"+
            "#                                                               \n"+
            "################################################################\n"+
            "                                                                \n"+
            "# Enablement of vaadin uis.                                     \n"+
            "#                                                               \n"+
            "# Multiple uis can be defined as comma separated list.          \n"+
            "# Whitespace before and after the comma will be ignored.        \n"+
            "# Valid values are the path properties of the @SpringUI         \n"+
            "# annotation which is used for UI classes.                      \n"+
            "cdm-vaadin.ui.activated=account,concept,distribution,editstatus \n";
    //@formatter:on

    /**
     * Checks if the ui class supplied is activated by listing it in the properties by its {@link SpringUI#path()} value.
     */
    private boolean isUIEnabled(Class<? extends UI>uiClass) {

        String path = uiClass.getAnnotation(SpringUI.class).path().trim();

        if(activeUIpaths == null){
            String activatedVaadinUIs = env.getProperty(CDM_VAADIN_UI_ACTIVATED);
            if(activatedVaadinUIs == null){
                // not in environment? Read it from the config file!
                activatedVaadinUIs = appConfig.getProperty(configFile , CDM_VAADIN_UI_ACTIVATED);
            } else {
                logger.warn("Active UIs are defined via system properties -D" + CDM_VAADIN_UI_ACTIVATED + "=" +  activatedVaadinUIs + " ignoring config file." );
            }

            if(activatedVaadinUIs != null) {
                String[] uiPaths = activatedVaadinUIs.split("\\s*,\\s*");
                this.activeUIpaths = Arrays.asList(uiPaths);
            }
        }
        if(activeUIpaths.stream().anyMatch(p -> p.trim().equals(path))){
            return true;
        }
        logger.warn(" UI " + path + " not enabled in " + configFile.getFileName() + ".properties" );
        return false;

    }

    private <T extends UI> T applyEnableConfig(T ui) {
        ui.setEnabled(isUIEnabled(ui.getClass()));
        return ui;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
