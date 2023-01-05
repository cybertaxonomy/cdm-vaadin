/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.navigation;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.event.EditorActionContext;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.ApplicationView;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.PopEditorOpenedEvent;
import eu.etaxonomy.vaadin.ui.view.PopupView;

@UIScope
public class NavigationManagerBean extends SpringNavigator implements NavigationManager, DisposableBean {

	private static final long serialVersionUID = 6599898650948333853L;

    private static final Logger logger = LogManager.getLogger();

	// injecting the viewDisplay as spring bean causes problems with older cdm vaadin code
	// SingleComponentContainerViewDisplay for example can't be used
	// the viewDisplay should be configurable per UI therefore it seems more elegant to
	// let the UI pass the viewDisplay to the Navigator
//	@Autowired
	private ViewDisplay viewDisplay;

	@Autowired
	private SpringViewProvider viewProvider;

	@Autowired
	private List<ViewChangeListener> viewChangeListeners;

	@Autowired
	protected ApplicationContext applicationContext;

	protected EventBus.UIEventBus uiEventBus;

	/**
	 * if set the navigator will block all other views
	 * and exclusively navigates to the error view to indicate that the
	 * UI is disabled.
	 */
	private String uiDisabledErrorViewName = null;

    @Autowired
    protected void setViewEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

//    /**
//     * This reference will cause the scoped PermissionDebugUtils being initialized.
//     * It is not used in this class but attaches itself to the vaadin session
//     * from where it will be accessible via VaadinUserHelper.fromSession()
//     *
//     * <b>NOTE:</b> PermissionDebugUtils is only available if the spring profile "debug" is active,
//     * See
//     */
//    @Autowired(required=false)
//    private PermissionDebugUtils permissionDebugUtils;

	private PopupViewRegistration popupViewRegistration;

	private String defaultViewName = null;

    /*
     * Why UriFragmentManager must be initialized lazily:
     *
     * when the SpringVaadinServlet usually is being instantiated the ServletUIInitHandler(UIInitHandler).getBrowserDetailsUI(VaadinRequest, VaadinSession) method is called which will
     * first cause the WebapplicationContext being created. Once this is done the initialization of the UI classes is completed. This means that the UI classes are not readily available
     * via Page.getCurrent() which is used in the UriFragmentManager constructor. The NavigationManagerBean is initialized with the WebapplicationContext, that is when the current ui is
     * not yet available, therefore the UriFragmentManager must be initialized lazily.
     */
    @Autowired
    @Lazy
	private UriFragmentManager uriFragmentManager;


//	public void setUriFragmentManager(UriFragmentManager uriFragmentManager) {
//	    this.uriFragmentManager = uriFragmentManager;
//	}


	public NavigationManagerBean() {
	    popupViewRegistration = new PopupViewRegistration();
	}

//	private Collection<PopupView> popupViews = new HashSet<>();
//	@Lazy
//    @Autowired(required=false)
//    private void popUpViews(Collection<PopupView> popupViews){
//        this.popupViews = popupViews;
//        // popupViews.forEach(view -> this.popupViews.put(view.getClass(), view));
//    }

    private <P extends PopupView> P findPopupView(Class<P> type){
        P viewBean = applicationContext.getBean(type);
        if(viewBean == null){
            throw new NullPointerException("no popup-view bean of type " + type.getName() + " found");
        }
        return viewBean;
        // return popupViews.stream().filter(p -> p.getClass().equals(type)).findFirst();
    }

	@EventBusListenerMethod
	protected void onUIInitialized(UIInitializedEvent e) {
		init(UI.getCurrent(), uriFragmentManager, viewDisplay);
		addProvider(viewProvider);
		viewChangeListeners.forEach(vcl -> addViewChangeListener(vcl));
	}

	public void navigateTo(String navigationState, boolean fireNavigationEvent) {
	    if(getUiDisabledErrorView() != null) {
	        super.navigateTo(getUiDisabledErrorView());
	    }
	    if(StringUtils.isEmpty(navigationState)){
            navigationState = defaultViewName;
        }
		if (fireNavigationEvent) {
			navigateTo(navigationState);
		} else {
			super.navigateTo(navigationState);
		}
	}

	@Override
	public void navigateTo(String navigationState) {
	    if(getUiDisabledErrorView() != null) {
            super.navigateTo(getUiDisabledErrorView());
        }
	    if(StringUtils.isEmpty(navigationState)){
	        navigationState = defaultViewName;
	    }
		super.navigateTo(navigationState);
		//eventBus.publishEvent(new NavigationEvent(navigationState));
	}

	@EventBusListenerMethod
	protected void onNavigationEvent(NavigationEvent e) {
		navigateTo(e.getViewName(), false);
	}

	@Override
	public <T extends PopupView> T showInPopup(Class<T> popupType, ApplicationView parentView, Field<?> targetField) {

	    PopupView popupView =  findPopupView(popupType);

	    if(AbstractPopupEditor.class.isAssignableFrom(popupView.getClass())){
	        if(parentView instanceof AbstractPopupEditor){
	            // retain the chain of EditorActionContexts when starting a new pupupEditor
	            Stack<EditorActionContext> parentEditorActionContext = ((AbstractPopupEditor)parentView).getEditorActionContext();
	            ((AbstractPopupEditor)popupView).setParentEditorActionContext(parentEditorActionContext, targetField);
	        }
	    }

		Window window = new Window();
		window.setCaption(popupView.getWindowCaption());
		window.center();
		window.setResizable(popupView.isResizable());
		// due to issue #6673 (https://dev.e-taxonomy.eu/redmine/issues/6673) popup editors must be modal!
		//window.setModal(popupView.isModal());
		window.setModal(true);
		window.setCaptionAsHtml(popupView.isWindowCaptionAsHtml());
		window.setWidth(popupView.getWindowWidth(), popupView.getWindowWidthUnit());
		window.setHeight(popupView.getWindowHeight(), popupView.getWindowHeightUnit());
		window.setContent(popupView.asComponent());
		// TODO need to disallow pressing the close [x] button:
		// since window.addCloseListener(e -> popupView.cancel()); will
		// cause sending cancel events even if save has been clicked
		window.setClosable(popupView.isClosable());
		UI.getCurrent().addWindow(window);
		popupView.viewEntered();
		popupView.focusFirst();
		uiEventBus.publish(this, new PopEditorOpenedEvent(this, popupView));

		popupViewRegistration.put(window, parentView, popupView, targetField);

		return (T) popupView;
	}

	@Override
    public Field<?> targetFieldOf(PopupView popupView){
	    return popupViewRegistration.get(popupView);
	}

    @EventBusListenerMethod
	protected void onDoneWithTheEditor(DoneWithPopupEvent event) {

		PopupView popup = event.getPopup();
		if(DisposableBean.class.isAssignableFrom(popup.getClass())){
		    try {
                ((DisposableBean)popup).destroy();
            } catch (Exception e) {
                logger.error(e);
            }
		}
        Window window = popupViewRegistration.getWindow(popup);
		if (window != null) {
			window.close();
			popupViewRegistration.remove(popup);
		}
		if(AbstractPopupEditor.class.isAssignableFrom(popup.getClass())){
		    ((AbstractPopupEditor)popup).presenter().unsubscribeFromEventBuses();
		}

	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void reloadCurrentView() {
        if(logger.isTraceEnabled()){
            logger.trace("reloading " + getState());
        }
        navigateTo(getState(), false);
    }

    /**
     * This method requires that the {@SpringView} annotation is used to set the name of the <code>View</code>.
     *
     * @return the current view name or <code>null</code>
     */
    @Override
    public String getCurrentViewName() {
        if(getCurrentView() != null){
            SpringView springViewAnnotation = getCurrentView().getClass().getAnnotation(SpringView.class);
            if(springViewAnnotation != null){
                return springViewAnnotation.name();
            }
        }
        return null;
    }

    @Override
    public List<String> getCurrentViewParameters(){
        String substate = getState();
        String currentViewName = getCurrentViewName();
        if(currentViewName != null){
            substate = substate.replaceAll("^" + currentViewName + "/?", "");

        }
        return Arrays.asList(substate.split("/"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<AbstractEditorPresenter<?, ?>> getPopupEditorPresenters() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @return the defaultViewName
     */
    public String getDefaultViewName() {
        return defaultViewName;
    }

    /**
     * @param defaultViewName the defaultViewName to set
     */
    public void setDefaultViewName(String defaultViewName) {
        this.defaultViewName = defaultViewName;
    }

    public void setViewDisplay(ViewDisplay viewDisplay){
        this.viewDisplay = viewDisplay;
    }

    /**
     * implementation of the interface {@link DisposableBean} and overrides the
     * method in {@link Navigator} which is not an implementation of {@link DisposableBean}
     * in this class.
     */
    @Override
    public void destroy() {
        super.destroy();
        uiEventBus.unsubscribe(this);
        popupViewRegistration = null;
        // release the reference to the view kept in currentView
        // which could be expensive
        // by navigating to the default view
        navigateTo(defaultViewName);
    }

    public String getUiDisabledErrorView() {
        return uiDisabledErrorViewName;
    }

    public void setUiDisabledErrorView(String uiDisabledErrorViewName) {
        this.uiDisabledErrorViewName = uiDisabledErrorViewName;
    }
}
