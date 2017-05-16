package eu.etaxonomy.vaadin.ui.navigation;

import com.vaadin.navigator.View;

import eu.etaxonomy.vaadin.ui.view.PopupView;

public interface NavigationManager {

	View getCurrentView();

	<T extends PopupView> T showInPopup(Class<T> popupType);

	void reloadCurrentView();
}
