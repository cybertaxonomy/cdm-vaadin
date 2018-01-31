/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;

import com.flowingcode.vaadin.addons.errorwindow.WindowErrorHandler;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Resource;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.debug.EntityCacheDebugger;
import eu.etaxonomy.cdm.vaadin.toolbar.Toolbar;
import eu.etaxonomy.cdm.vaadin.view.RedirectToLoginView;
import eu.etaxonomy.cdm.vaadin.view.registration.DashBoardView;
import eu.etaxonomy.cdm.vaadin.view.registration.ListView;
import eu.etaxonomy.cdm.vaadin.view.registration.ListViewBean;
import eu.etaxonomy.cdm.vaadin.view.registration.StartRegistrationViewBean;
import eu.etaxonomy.vaadin.ui.MainMenu;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;
import eu.etaxonomy.vaadin.ui.view.ToolbarDisplay;

/**
 * @author a.kohlbecker
 * @since Feb 24, 2017
 *
 */
@Theme("edit-valo")
@Title("Registration")
@SpringUI(path="registration")
@Viewport("width=device-width, initial-scale=1")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
// @PreserveOnRefresh
// @Push
public class RegistrationUI extends UI {

    private static final long serialVersionUID = -8626236111335323691L;

    @Autowired
    @Qualifier("viewAreaBean")
    private ViewDisplay viewDisplay;

    //---- pull into abstract super class ? ---------
    @Autowired
    SpringViewProvider viewProvider;

    @Autowired
    NavigationManagerBean navigator;

    @Autowired(required = false)
    EntityCacheDebugger entityCacheDebugger = null;

    protected void configureAccessDeniedView() {
        viewProvider.setAccessDeniedViewClass(RedirectToLoginView.class);
    }

    /**
     * @return
     */
    private String pageFragmentAsState() {
        Page page = Page.getCurrent();
        String fragment = page.getUriFragment();
        String state = null;
        if(fragment != null && fragment.startsWith("!")){
            state = fragment.substring(1, fragment.length());
        }
        return state;
    }

    //---------------------------------------------

    public static final String INITIAL_VIEW =  DashBoardView.NAME;


    /*
     * this HACKY solution forces the bean to be instantiated, TODO do it properly
     */
//    @Autowired
//    MenuBeanDiscoveryBean bean;

    @Autowired
    private MainMenu mainMenu;

    @Autowired
    @Qualifier("registrationToolbar")
    private Toolbar toolbar;

    @Autowired
    ApplicationEventPublisher eventBus;

    public RegistrationUI() {

    }

    @Override
    protected void init(VaadinRequest request) {

        setErrorHandler(new WindowErrorHandler(this, "Please contact the editsupport@bgbm.org for more information.</br></br>"
                + "<i>To help analyzing the problem please describe your actions that lead to this error and provide the error details from below in your email. "
                + "You also might want to add a sreenshot of the browser page in error.</i>"));

        navigator.setViewDisplay(viewDisplay);
        configureAccessDeniedView();

        addStyleName(ValoTheme.UI_WITH_MENU);
        Responsive.makeResponsive(this);

        setContent((Component) viewDisplay);
        Label phycoBankLogo = new Label("PhycoBank");
        phycoBankLogo.addStyleName("phycobank-green");
        phycoBankLogo.addStyleName(ValoTheme.LABEL_HUGE);
        mainMenu.addMenuComponent(phycoBankLogo);

        mainMenu.addMenuItem("New", FontAwesome.EDIT, StartRegistrationViewBean.NAME );
        mainMenu.addMenuItem("Continue", FontAwesome.ARROW_RIGHT, ListViewBean.NAME + "/" + ListView.Mode.inProgress.name());
        mainMenu.addMenuItem("List", FontAwesome.TASKS, ListViewBean.NAME + "/" + ListView.Mode.all.name());

        if(ToolbarDisplay.class.isAssignableFrom(viewDisplay.getClass())){
            ((ToolbarDisplay)viewDisplay).setToolbar(toolbar);
        }


        eventBus.publishEvent(new UIInitializedEvent());

        String brand = "phycobank";
        //TODO create annotation:
        // @Styles(files={""}, branding="brand")
        //
        // the branding can either be specified or can be read from the properties file in .cdmLibrary/remote-webapp/{instance-name}-app.properties
        // See CdmUtils for appropriate methods to access this folder
        // the 'vaadin://' protocol refers to the VAADIN folder
        Resource registryCssFile = new ExternalResource("vaadin://branding/" + brand + "/css/branding.css");
        Page.getCurrent().getStyles().add(registryCssFile);

        navigator.setDefaultViewName(INITIAL_VIEW);

        if(entityCacheDebugger != null){
            addShortcutListener(entityCacheDebugger.getShortcutListener());
        }
        //navigate to initial view
//        String state = pageFragmentAsState();


//        if(state == null){
//            // the case when state != null is handled in the UI base class
//            eventBus.publishEvent(new NavigationEvent(INITIAL_VIEW));
//        }
    }
}
