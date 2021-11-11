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

import com.flowingcode.vaadin.addons.errorwindow.WindowErrorHandler;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.vaadin.debug.EntityCacheDebugger;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.cdm.vaadin.event.error.ErrorTypeErrorHandlerWrapper;
import eu.etaxonomy.cdm.vaadin.event.error.PermissionDeniedErrorHandler;
import eu.etaxonomy.cdm.vaadin.toolbar.Toolbar;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditor;
import eu.etaxonomy.cdm.vaadin.view.registration.DashBoardView;
import eu.etaxonomy.cdm.vaadin.view.registration.ListView;
import eu.etaxonomy.cdm.vaadin.view.registration.ListViewBean;
import eu.etaxonomy.cdm.vaadin.view.registration.StartRegistrationViewBean;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.ui.MainMenu;
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
public class RegistrationUI extends AbstractUI implements PopupEditorDefaultStatusMessageSource {

    private static final long serialVersionUID = -8626236111335323691L;

    @Autowired
    @Qualifier("viewAreaBean")
    private ViewDisplay viewDisplay;

    //---- pull into abstract super class ? ---------

    @Autowired
    private MainMenu mainMenu;

    @Autowired
    @Qualifier("registrationToolbar")
    private Toolbar toolbar;

    @Autowired(required = false)
    EntityCacheDebugger entityCacheDebugger = null;

    //---------------------------------------------

    public static final String BRAND_NAME = "phycobank";

    public static final String INITIAL_VIEW =  DashBoardView.NAME;


    //---------------------------------------------

    public RegistrationUI() {

    }

    @Override
    protected void init(VaadinRequest request) {

        super.init(request);

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

    @Override
    protected ViewDisplay getViewDisplay() {
        return viewDisplay;
    }

    @Override
    protected void initContent() {
        Label phycoBankLogo = new Label("PhycoBank");
        phycoBankLogo.addStyleName("phycobank-green");
        phycoBankLogo.addStyleName(ValoTheme.LABEL_HUGE);
        mainMenu.addMenuComponent(phycoBankLogo);

        mainMenu.addMenuItem("New", FontAwesome.EDIT, StartRegistrationViewBean.NAME );
        mainMenu.addMenuItem("Continue", FontAwesome.ARROW_RIGHT, ListViewBean.NAME + "/" + ListView.Mode.inProgress.name());
        mainMenu.addMenuItem("List", FontAwesome.TASKS, ListViewBean.NAME + "/" + ListView.Mode.all.name());

        if(ToolbarDisplay.class.isAssignableFrom(getViewDisplay().getClass())){
            ((ToolbarDisplay)getViewDisplay()).setToolbar(toolbar);
        }
    }

    @Override
    protected void registerErrorHandlers() {
        DelegatingErrorHandler delegatingErrorHander = new DelegatingErrorHandler();
        WindowErrorHandler errorHandler = new WindowErrorHandler(this, RegistrationUIDefaults.ERROR_CONTACT_MESSAGE_LINE + "</br></br>"
                + "<i>To help analyzing the problem please describe your actions that lead to this error and provide the error details from below in your email. "
                + "You also might want to add a sreenshot of the browser page in error.</i>");
        delegatingErrorHander.registerHandler(new ErrorTypeErrorHandlerWrapper<PermissionDeniedException>(PermissionDeniedException.class, new PermissionDeniedErrorHandler(this)));
        delegatingErrorHander.registerHandler(new ErrorTypeErrorHandlerWrapper<Exception>(Exception.class, errorHandler));
        setErrorHandler(delegatingErrorHander);
        VaadinSession.getCurrent().setErrorHandler(delegatingErrorHander);
    }

    @Override
    public <T extends AbstractPopupEditor> String defaultStatusMarkup(Class<T> popupEditorClass){
        if(popupEditorClass.equals(TaxonNamePopupEditor.class)){
            return "<strong>Check if this name already occurs in the <a href=\"https://www.phycobank.org/index-search\" target=\"index-search\">PhycoBank Index</a> (The link will open in a new window.)</strong>";
        }
        return null;
    }

}
