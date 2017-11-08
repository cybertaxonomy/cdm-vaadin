package eu.etaxonomy.vaadin.ui.navigation;

import java.util.List;

import com.vaadin.navigator.View;

import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.ui.view.PopupView;

public interface NavigationManager {

	View getCurrentView();

	<T extends PopupView> T showInPopup(Class<T> popupType);

	public List<AbstractEditorPresenter<?,?>> getPopupEditorPresenters();

	void reloadCurrentView();

	public String getCurrentViewName();

	/**
	 *
	 * @return
	 */
	public List<String> getCurrentViewParameters();
}
