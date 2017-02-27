package com.vaadin.devday.ui.navigation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.vaadin.devday.ui.NavigationManager;
import com.vaadin.devday.ui.UIInitializedEvent;
import com.vaadin.devday.ui.view.DoneWithPopupEvent;
import com.vaadin.devday.ui.view.PopupView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SpringComponent
@UIScope
class NavigationManagerBean extends SpringNavigator implements NavigationManager {

	private static final long serialVersionUID = 6599898650948333853L;

	@Autowired
	private ViewDisplay viewDisplay;

	@Autowired
	private SpringViewProvider viewProvider;

	@Autowired
	private ViewChangeListener viewChangeListener;

	private Map<PopupView, Window> popupMap;

	public NavigationManagerBean() {
		popupMap = new HashMap<>();
	}

	//@Autowired
    private Map<String, PopupView> popupViews = null;

	@Autowired
	private UriFragmentManager uriFragmentManager;

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
		// Instance<T> instanceSelection = popupViewInstantiator.select(popupType);
		PopupView popupContent =  popupViews.get(popupType.getSimpleName()); // FIXME better scan the collection for the correct bean class

		Window window = new Window();
		window.setCaption(popupContent.getWindowCaption());
		window.center();
		window.setResizable(false);

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
}
