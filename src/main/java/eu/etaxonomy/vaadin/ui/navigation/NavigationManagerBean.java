package eu.etaxonomy.vaadin.ui.navigation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.PojoEventListenerManager;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.security.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.PopEditorOpenedEvent;
import eu.etaxonomy.vaadin.ui.view.PopupEditorFactory;
import eu.etaxonomy.vaadin.ui.view.PopupView;

@UIScope
public class NavigationManagerBean extends SpringNavigator implements NavigationManager {

	private static final long serialVersionUID = 6599898650948333853L;

	private final static Logger logger = Logger.getLogger(NavigationManagerBean.class);

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
	private PojoEventListenerManager eventListenerManager;

	@Autowired
	private PopupEditorFactory popupEditorFactory;

	/**
	 * This reference will cause the scoped UserHelper being initialized
	 * It is not used in this class but attaches itself to the vaadin session
	 * from where it will be accessible via UserHelper.fromSession()
	 */
	@Autowired
    private UserHelper userHelper;

    /**
     * This reference will cause the scoped PermissionDebugUtils being initialized.
     * It is not used in this class but attaches itself to the vaadin session
     * from where it will be accessible via UserHelper.fromSession()
     *
     * <b>NOTE:</b> PermissionDebugUtils is only available if the spring profile "debug" is active,
     * See
     */
    @Autowired(required=false)
    private PermissionDebugUtils permissionDebugUtils;

	private Map<PopupView, Window> popupMap;

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

	@Autowired
	ApplicationEventPublisher eventBus;


	public NavigationManagerBean() {
	    popupMap = new HashMap<>();
	}


	private <P extends PopupView> PopupView findPopupView(Class<P> popupViewClass){
	    return popupEditorFactory.newPopupView(popupViewClass);
	}

	@EventListener
	protected void onUIInitialized(UIInitializedEvent e) {
		init(UI.getCurrent(), uriFragmentManager, viewDisplay);
		viewChangeListeners.forEach(vcl -> addViewChangeListener(vcl));
	}

	public void navigateTo(String navigationState, boolean fireNavigationEvent) {
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
	    if(StringUtils.isEmpty(navigationState)){
	        navigationState = defaultViewName;
	    }
		super.navigateTo(navigationState);
		//eventBus.publishEvent(new NavigationEvent(navigationState));
	}

	@EventListener
	protected void onNavigationEvent(NavigationEvent e) {
		navigateTo(e.getViewName(), false);
	}

	@Override
	public <T extends PopupView> T showInPopup(Class<T> popupType) {

	    PopupView popupView =  findPopupView(popupType); // TODO make better use of Optional

	    if(AbstractPopupEditor.class.isAssignableFrom(popupView.getClass())){
	        AbstractEditorPresenter presenter = ((AbstractPopupEditor)popupView).presenter();
	        eventListenerManager.addEventListeners(presenter);
	    }

		Window window = new Window();
		window.setCaption(popupView.getWindowCaption());
		window.center();
		window.setResizable(popupView.isResizable());
		// due to issue #6673 (https://dev.e-taxonomy.eu/redmine/issues/6673) popup editors must be modal!
		//window.setModal(popupView.isModal());
		window.setModal(true);
		window.setCaptionAsHtml(popupView.isWindowCaptionAsHtml());
		window.setWidth(popupView.getWindowPixelWidth(), Unit.PIXELS);
		// setting 100% as default height. If the height
		// would be undefined the window, will fit the size of
		// the content and will sometimes exceed the height of the
		// main window and will not get a scroll bar in this situation.
		// see #6843
		window.setHeight("100%");
		window.setContent(popupView.asComponent());
		// window.addCloseListener(e -> popupView.cancel());
		UI.getCurrent().addWindow(window);
		popupView.viewEntered();
		popupView.focusFirst();
		eventBus.publishEvent(new PopEditorOpenedEvent(this, popupView));

		popupMap.put(popupView, window);

		return (T) popupView;
	}

    @EventListener
	protected void onDoneWithTheEditor(DoneWithPopupEvent e) {

		PopupView popup = e.getPopup();
        Window window = popupMap.get(popup);
		if (window != null) {
			window.close();
			popupMap.remove(popup);
		}
		if(AbstractPopupEditor.class.isAssignableFrom(popup.getClass())){
		    AbstractEditorPresenter presenter = ((AbstractPopupEditor)popup).presenter();
		    eventListenerManager.removeEventListeners(presenter);
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
     * This method requires that the {@SpringView} annotation is used to ser the name of the <code>View</code>.
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
}
