package eu.etaxonomy.vaadin.ui.navigation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.view.DoneWithPopupEvent;
import eu.etaxonomy.vaadin.ui.view.PopupView;

@UIScope
public class NavigationManagerBean extends SpringNavigator implements NavigationManager {

	private static final long serialVersionUID = 6599898650948333853L;

	private final static Logger logger = Logger.getLogger(NavigationManagerBean.class);

	@Autowired
	private ViewDisplay viewDisplay;

	@Autowired
	private SpringViewProvider viewProvider;

	@Autowired
	private ViewChangeListener viewChangeListener;

	@Autowired
    private UserHelper userHelper;

	private Map<PopupView, Window> popupMap;

	public NavigationManagerBean() {
		popupMap = new HashMap<>();
	}

    private Collection<PopupView> popupViews = new HashSet<>();

    @Lazy
    @Autowired(required=false)
	private void popUpViews(Collection<PopupView> popupViews){
        this.popupViews = popupViews;
        // popupViews.forEach(view -> this.popupViews.put(view.getClass(), view));
	}

    private <P extends PopupView> Optional<PopupView> findPopupView(Class<P> type){
        return popupViews.stream().filter(p -> p.getClass().equals(type)).findFirst();
    }

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

	@EventListener
	protected void onUIInitialized(UIInitializedEvent e) {
		init(UI.getCurrent(), uriFragmentManager, viewDisplay);
		addProvider(viewProvider);
		addViewChangeListener(viewChangeListener);
	}

	public void navigateTo(String navigationState, boolean fireNavigationEvent) {
		if (fireNavigationEvent) {
			navigateTo(navigationState);
		} else {
			super.navigateTo(navigationState);
		}
	}

	@Override
	public void navigateTo(String navigationState) {
		super.navigateTo(navigationState);
		eventBus.publishEvent(new NavigationEvent(navigationState));
	}

	@EventListener
	protected void onNavigationEvent(NavigationEvent e) {
		navigateTo(e.getViewName(), false);
	}

	@Override
	public <T extends PopupView> T showInPopup(Class<T> popupType) {

	    PopupView popupContent =  findPopupView(popupType).get(); // TODO make better use of Optional

		Window window = new Window();
		window.setCaption(popupContent.getWindowCaption());
		window.center();
		window.setResizable(popupContent.isResizable());
		window.setModal(popupContent.isModal());
		window.setCaptionAsHtml(popupContent.isWindowCaptionAsHtml());
		window.setWidth(popupContent.getWindowPixelWidth(), Unit.PIXELS);
		window.setContent(popupContent.asComponent());
		UI.getCurrent().addWindow(window);
		popupContent.focusFirst();

		popupMap.put(popupContent, window);

		return (T) popupContent;
	}

    @EventListener
	protected void onDoneWithTheEditor(DoneWithPopupEvent e) {
		Window window = popupMap.get(e.getPopup());
		if (window != null) {
			window.close();
			popupMap.remove(e.getPopup());
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
    public String getCurrentViewName() {
        SpringView springViewAnnotation = getCurrentView().getClass().getAnnotation(SpringView.class);
        if(springViewAnnotation != null){
            return springViewAnnotation.name();
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
}
