/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.navigation;

import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.ui.Field;

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
	 * @param targetField
	 *         The field which will needs to be updated after the popup view has been closed. Can be <code>null</code>.
	 * @return
	 */
	<T extends PopupView> T showInPopup(Class<T> popupType, ApplicationView parentView, Field<?> targetField);

	public List<AbstractEditorPresenter<?,?,?>> getPopupEditorPresenters();

	void reloadCurrentView();

	public String getCurrentViewName();

	/**
	 *
	 * @return
	 */
	public List<String> getCurrentViewParameters();

    Field<?> targetFieldOf(PopupView popupView);
}
