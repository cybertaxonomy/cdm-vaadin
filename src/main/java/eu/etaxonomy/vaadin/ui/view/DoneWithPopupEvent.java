package eu.etaxonomy.vaadin.ui.view;

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
