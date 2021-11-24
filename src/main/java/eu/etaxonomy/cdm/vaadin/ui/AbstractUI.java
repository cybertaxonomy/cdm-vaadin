/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.flowingcode.vaadin.addons.errorwindow.WindowErrorHandler;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.addon.config.UIDisabledException;
import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.cdm.vaadin.event.error.ErrorTypeErrorHandlerWrapper;
import eu.etaxonomy.cdm.vaadin.event.error.PermissionDeniedErrorHandler;
import eu.etaxonomy.cdm.vaadin.event.error.UIDisabledErrorHandler;
import eu.etaxonomy.cdm.vaadin.permission.ReleasableResourcesView;
import eu.etaxonomy.cdm.vaadin.view.RedirectToLoginView;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
public abstract class AbstractUI extends UI {

    private static final long serialVersionUID = 7430086500775997281L;

    private static Logger logger = Logger.getLogger(AbstractUI.class);

    abstract protected ViewDisplay getViewDisplay();

    @Autowired
    protected SpringViewProvider viewProvider;

    @Autowired
    final private void setNavigationManagerBean(NavigationManagerBean navigatorBean) {
        // logger.debug("setNavigationManagerBean()" + navigatorBean.toString());
        setNavigator(navigatorBean);
    }

    final private NavigationManagerBean getNavigationManagerBean() {
        if(getNavigator() != null) {
            return (NavigationManagerBean) getNavigator();
        }
        return null;
    }

    @Autowired
    protected UIEventBus uiEventBus;

//    private boolean enabled;

    public AbstractUI() {
        super();
    }

    public AbstractUI(Component content) {
        super(content);
    }

    @Override
    protected void init(VaadinRequest request) {

        logger.debug(this.getClass().getSimpleName() + ".init()");
        registerErrorHandlers();

        configureAccessDeniedView();

        assert getInitialViewName() != null;
        assert getViewDisplay() != null;

        logger.debug(this.getClass().getSimpleName() + ".init() ViewDisplay: " + getViewDisplay().getClass().getSimpleName() + ", initialViewName: " + getInitialViewName());

        if(!isEnabled()) {
            throw new UIDisabledException(getClass().getSimpleName());
        }

        initAdditionalContent();

        getNavigationManagerBean().setViewDisplay(getViewDisplay());
        getNavigationManagerBean().setDefaultViewName(getInitialViewName());

        Responsive.makeResponsive(this);

        addDetachListener(e -> {
            if(getNavigator() != null) {
                // no point using viewProvider.getView() without the navigator
                for(String viewName : viewProvider.getViewNamesForCurrentUI()){
                    View view = viewProvider.getView(viewName);
                    if(view != null && view instanceof ReleasableResourcesView) {
                        ((ReleasableResourcesView)view).releaseResourcesOnAccessDenied();
                    }
                }
            }
        });

        if(getBrandName() != null) {
            //TODO create annotation:
            // @Styles(files={""}, branding="brand")
            //
            // the branding can either be specified or can be read from the properties file in .cdmLibrary/remote-webapp/{instance-name}-app.properties
            // See CdmUtils for appropriate methods to access this folder
            // the 'vaadin://' protocol refers to the VAADIN folder
            Resource registryCssFile = new ExternalResource("vaadin://branding/" + getBrandName() + "/css/branding.css");
            Page.getCurrent().getStyles().add(registryCssFile);
        }

        uiEventBus.publish(this, new UIInitializedEvent());
    }

//    /**
//     * @return
//     */
//    @Override
//    protected abstract String getUIName();

    /**
     * @return The name of the initial view to show
     */
    abstract protected String getInitialViewName();

    /**
     * Branding can either be specified or can be read from the properties file
     * in <code>.cdmLibrary/remote-webapp/{instance-name}-app.properties</code>
     * See CdmUtils for appropriate methods to access this folder the
     * <code>'vaadin://'</code> protocol refers to the VAADIN folder.
     * <p>
     * Can be overridden by implementing classes to set a brand.
     *
     * @return <code>NULL</code> for no branding or
     */
    protected String getBrandName() {
        return null;
    }

    /**
     * Implementing classes may add additional content to the UI. This
     * will for example be interesting when using the {@link ValoTheme.UI_WITH_MENU}
     * style.
     */
    abstract protected void initAdditionalContent();

    protected void registerErrorHandlers() {
        DelegatingErrorHandler delegatingErrorHander = new DelegatingErrorHandler();
        WindowErrorHandler errorHandler = new WindowErrorHandler(
                this,
                RegistrationUIDefaults.ERROR_CONTACT_MESSAGE_LINE + "</br></br>"
                + "<i>To help analyzing the problem please describe your actions that lead to this error and provide the error details from below in your email. "
                + "You also might want to add a sreenshot of the browser page in error.</i>");
        delegatingErrorHander.registerHandler(
                new ErrorTypeErrorHandlerWrapper<PermissionDeniedException>(PermissionDeniedException.class, new PermissionDeniedErrorHandler(this))
                );
        delegatingErrorHander.registerHandler(
                new ErrorTypeErrorHandlerWrapper<UIDisabledException>(UIDisabledException.class, new UIDisabledErrorHandler(this))
                );
        delegatingErrorHander.registerHandler(
                new ErrorTypeErrorHandlerWrapper<Exception>(Exception.class, errorHandler)
                );
        setErrorHandler(delegatingErrorHander);
        VaadinSession.getCurrent().setErrorHandler(delegatingErrorHander);
    }

    protected void configureAccessDeniedView() {
        viewProvider.setAccessDeniedViewClass(RedirectToLoginView.class);
    }

    private String pageFragmentAsState() {
        Page page = Page.getCurrent();
        String fragment = page.getUriFragment();
        String state = null;
        if(fragment != null && fragment.startsWith("!")){
            state = fragment.substring(1, fragment.length());
        }
        return state;
    }

//    @Override
//   public void setEnabled(boolean state) {
//        this.enabled = state;
//    }
//
//    @Override
//   public boolean isEnabled() {
//        return enabled;
//    }


}