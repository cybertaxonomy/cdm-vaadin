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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;

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

import eu.etaxonomy.cdm.dataInserter.RegistrationRequiredDataInserter;
import eu.etaxonomy.cdm.vaadin.view.RedirectToLoginView;
import eu.etaxonomy.cdm.vaadin.view.registration.DashBoardView;
import eu.etaxonomy.cdm.vaadin.view.registration.ListViewBean;
import eu.etaxonomy.cdm.vaadin.view.registration.StartRegistrationViewBean;
import eu.etaxonomy.vaadin.ui.MainMenu;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

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
    private ViewDisplay viewDisplay;

    /**
     * The RegistrationDefaultDataInserter is not used in the ui directly
     * but will as a ApplicationListener for ContextRefreshedEvents insert
     * data required for the registration application into the database.
     */
    @SuppressWarnings("unused")
    @Autowired
    @Lazy
    private RegistrationRequiredDataInserter dataInserter;

    //---- pull into abstract super class ? ---------
    @Autowired
    SpringViewProvider viewProvider;

    protected void configureAccessDeniedView() {
        viewProvider.setAccessDeniedViewClass(RedirectToLoginView.class);
    }
    //---------------------------------------------

    // public static final String INITIAL_VIEW = "workflow/edit/10";
    public static final String INITIAL_VIEW =  DashBoardView.NAME;
    static boolean debugMode = true;

    /*
     * this HACKY solution forces the bean to be instantiated, TODO do it properly
     */
//    @Autowired
//    MenuBeanDiscoveryBean bean;

    @Autowired
    private MainMenu mainMenu;

    @Autowired
    ApplicationEventPublisher eventBus;

    public RegistrationUI() {

    }

    @Override
    protected void init(VaadinRequest request) {

        configureAccessDeniedView();

        addStyleName(ValoTheme.UI_WITH_MENU);
        Responsive.makeResponsive(this);

        setContent((Component) viewDisplay);
        Label phycoBankLogo = new Label("PhycoBank");
        phycoBankLogo.addStyleName("phycobank-green");
        phycoBankLogo.addStyleName(ValoTheme.LABEL_HUGE);
        mainMenu.addMenuComponent(phycoBankLogo);

        mainMenu.addMenuItem("New", FontAwesome.EDIT, StartRegistrationViewBean.NAME );
        mainMenu.addMenuItem("Continue", FontAwesome.ARROW_RIGHT, ListViewBean.NAME + "/" + ListViewBean.OPTION_IN_PROGRESS);
        mainMenu.addMenuItem("List", FontAwesome.TASKS, ListViewBean.NAME + "/" + ListViewBean.OPTION_ALL);

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

        //navigate to initial view
        String state = pageFragmentAsState();

        if(debugMode && state != null){
            eventBus.publishEvent(new NavigationEvent(state));
        } else {
            eventBus.publishEvent(new NavigationEvent(INITIAL_VIEW));
        }
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
}
