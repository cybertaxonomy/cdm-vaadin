package eu.etaxonomy.vaadin.ui.navigation;

import java.util.List;

import com.vaadin.navigator.View;

import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.ApplicationView;
import eu.etaxonomy.vaadin.ui.view.PopupView;

public interface NavigationManager {

	View getCurrentView();

	/**
	 *
	 * @param popupType
	 *         Type of the popup to open
	 * @param parentView
	 *         The view from where the request to open the popup is being triggered
	 * @return
	 */
	<T extends PopupView> T showInPopup(Class<T> popupType, ApplicationView parentView);

	public List<AbstractEditorPresenter<?,?>> getPopupEditorPresenters();

	void reloadCurrentView();

	public String getCurrentViewName();

	/**
	 *
	 * @return
	 */
	public List<String> getCurrentViewParameters();
}
