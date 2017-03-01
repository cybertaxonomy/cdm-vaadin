package eu.etaxonomy.vaadin.ui.view;

import eu.etaxonomy.vaadin.ui.CanCastComponent;

public interface PopupView extends CanCastComponent {

	String getWindowCaption();

	void focusFirst();
}
