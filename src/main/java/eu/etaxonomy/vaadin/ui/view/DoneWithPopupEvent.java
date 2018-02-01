package eu.etaxonomy.vaadin.ui.view;

/**
 * Event indicating the end of a popup view which is send via the uiEventBus to the NavigationManagerBean.
 * +
 * @author a.kohlbecker
 * @since Feb 1, 2018
 *
 */
public class DoneWithPopupEvent {


	public enum Reason {
		CANCEL, SAVE, DELETE;
	}

	private final Reason reason;
	private final PopupView popup;

	public DoneWithPopupEvent(PopupView popup, Reason reason) {
		this.popup = popup;
		this.reason = reason;
	}


	public Reason getReason() {
		return reason;
	}

	public PopupView getPopup() {
		return popup;
	}
}
