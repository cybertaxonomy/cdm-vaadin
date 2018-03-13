package eu.etaxonomy.cdm.vaadin.component.distributionStatus;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.settings.SettingsPresenterBase;

@SuppressWarnings("serial")
public abstract class SettingsDialogWindowBase<P extends SettingsPresenterBase> extends CustomComponent {

	protected Button okButton;
	protected Button cancelButton;
	protected final P presenter;
	protected Window window;
	protected AbstractOrderedLayout mainLayout;

	public SettingsDialogWindowBase() {
        buildMainLayout();
        presenter = getPresenter();
        init();
	}

    protected abstract P getPresenter();

    protected abstract AbstractLayout buildMainLayout();

	protected abstract void init();

	protected HorizontalLayout createOkCancelButtons() {
		HorizontalLayout buttonToolBar = new HorizontalLayout();
	    // cancelButton
	    cancelButton = new Button();
	    cancelButton.setCaption(Messages.getLocalizedString(Messages.SettingsDialogWindowBase_CANCEL));
	    cancelButton.setImmediate(true);
	    cancelButton.addStyleName("dialogButton"); //$NON-NLS-1$
	    buttonToolBar.addComponent(cancelButton);

	    // okButton
	    okButton = new Button();
	    okButton.setCaption(Messages.getLocalizedString(Messages.SettingsDialogWindowBase_OK));
	    okButton.setImmediate(true);
	    okButton.addStyleName("dialogButton"); //$NON-NLS-1$
	    buttonToolBar.addComponent(okButton);
		return buttonToolBar;
	}

	public Window createWindow(String caption) {
	    window = new Window();
	    window.setModal(true);
	    window.setWidth("60%"); //$NON-NLS-1$
	    window.setHeight("80%"); //$NON-NLS-1$
	    window.setCaption(caption);
	    window.setContent(mainLayout);
	    return window;
	}

	/**
	 * Update OK/Cancel button depending on {@link #isValid()}
	 */
	protected void updateButtons(){
		okButton.setEnabled(isValid());
	}

	/**
	 * Evaluates if this dialog has all necessary values set in
	 * a correct state
	 * @return <code>true</code> if the status of this dialog is valid
	 */
	protected abstract boolean isValid();

}
