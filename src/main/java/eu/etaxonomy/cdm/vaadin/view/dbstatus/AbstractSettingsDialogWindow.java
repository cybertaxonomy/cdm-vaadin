package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.settings.SettingsPresenter;

@SuppressWarnings("serial")
public abstract class AbstractSettingsDialogWindow extends CustomComponent {

	protected Button okButton;
	protected Button cancelButton;
	protected final SettingsPresenter presenter;
	protected Window window;
	protected AbstractOrderedLayout mainLayout;

	public AbstractSettingsDialogWindow() {
        buildMainLayout();
        presenter = new SettingsPresenter();
        init();
	}

	protected abstract AbstractLayout buildMainLayout();

	protected abstract void init();

	protected HorizontalLayout createOkCancelButtons() {
		HorizontalLayout buttonToolBar = new HorizontalLayout();
	    // cancelButton
	    cancelButton = new Button();
	    cancelButton.setCaption("Cancel");
	    cancelButton.setImmediate(true);
	    cancelButton.addStyleName("dialogButton");
	    buttonToolBar.addComponent(cancelButton);
	
	    // okButton
	    okButton = new Button();
	    okButton.setCaption("OK");
	    okButton.setImmediate(true);
	    okButton.addStyleName("dialogButton");
	    buttonToolBar.addComponent(okButton);
		return buttonToolBar;
	}

	public Window createWindow() {
	    window = new Window();
	    window.setModal(true);
	    window.setWidth("60%");
	    window.setHeight("80%");
	    window.setCaption("Settings");
	    window.setContent(mainLayout);
	    return window;
	}

}