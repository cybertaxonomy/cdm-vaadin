package com.vaadin.devday.ui.mainmenu;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.devday.ui.MainMenu;
import com.vaadin.devday.ui.navigation.AfterViewChangeEvent;
import com.vaadin.devday.ui.navigation.NavigationEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * ValoMainMenuBean is the implementation of responsive Vaadin Valo theme's menu
 *
 * @author Peter / Vaadin
 */

@SpringComponent
@UIScope
class ValoMainMenuBean extends CssLayout implements MainMenu {

	private static final long serialVersionUID = -993548217313748689L;

	private final static String MENU_VISIBLE = "valo-menu-visible";

	private CssLayout menuArea;

	private Button menuToggle;

	@Autowired
    EventBus.UIEventBus eventBus;

	public ValoMainMenuBean() {
		setPrimaryStyleName(ValoTheme.MENU_ROOT);

		CssLayout menuPart = new CssLayout();
		menuPart.addStyleName(ValoTheme.MENU_PART);

		menuToggle = new Button("Menu", FontAwesome.LIST);
		menuToggle.addClickListener(e -> onToggleMenu());
		menuToggle.addStyleName("valo-menu-toggle");
		menuToggle.addStyleName(ValoTheme.BUTTON_SMALL);

		menuPart.addComponent(menuToggle);

		menuArea = new CssLayout();
		menuArea.setPrimaryStyleName("valo-menuitems");
		menuArea.setWidth(100, Unit.PERCENTAGE);

		menuPart.addComponent(menuArea);

		addComponent(menuPart);
	}

	private void onToggleMenu() {
		if (getStyleName().contains(MENU_VISIBLE)) {
			removeStyleName(MENU_VISIBLE);
		} else {
			addStyleName(MENU_VISIBLE);
		}
	}

	@Override
	public MainMenuItem addMenuItem(String caption, Resource icon, String navigationResource) {
		MainMenuItemBean mainMenuItem = new MainMenuItemBean(caption, icon, navigationResource,
				e -> onMenuItemClicked(navigationResource));
		menuArea.addComponent(mainMenuItem);
		return mainMenuItem;
	}

	private void onMenuItemClicked(String navigationResource) {
	    eventBus.publish(this, new NavigationEvent(navigationResource));
	}

	@EventBusListenerMethod()
	protected void onNavigationEvent(NavigationEvent e) {
		List<MainMenuItemBean> menuItems = StreamSupport.stream(menuArea.spliterator(), false)
				.filter(component -> MainMenuItemBean.class.isAssignableFrom(component.getClass()))
				.map(component -> MainMenuItemBean.class.cast(component)).collect(Collectors.toList());

		menuItems.forEach(menuItem -> menuItem.setSelected(false));
		Optional<MainMenuItemBean> selectedMenuItem = menuItems.stream()
				.filter(menuItem -> menuItem.getViewName().equals(e.getViewName())).findFirst();
		if (selectedMenuItem.isPresent()) {
			selectedMenuItem.get().setSelected(true);
		}
	}

	@EventBusListenerMethod()
	protected void afterViewChange(AfterViewChangeEvent event) {
		removeStyleName(MENU_VISIBLE);
	}
}
