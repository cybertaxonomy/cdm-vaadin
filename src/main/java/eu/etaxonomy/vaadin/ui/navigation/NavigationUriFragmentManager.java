package eu.etaxonomy.vaadin.ui.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SpringComponent
@UIScope
public class NavigationUriFragmentManager extends UriFragmentManager {

    private static final long serialVersionUID = -2033745435437337863L;

    @Autowired
    UIEventBus uiEventBus;

	public NavigationUriFragmentManager() {
		super(Page.getCurrent());
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {

	    if(!isModalWindowShown()){
	        uiEventBus.publish(this, new NavigationEvent(getState()));
	    } else {
	        ConfirmDialog confirm = ConfirmDialog.getFactory().create(
	                "Notification",
	                "You can't leave this page as long as modal popup editors are open.",
	                "Ok", null, null);
	        confirm.getCancelButton().setVisible(false);
	        confirm.show(UI.getCurrent(), null, true);
	    }

	}

    /**
     *
     */
    protected boolean isModalWindowShown() {
        return UI.getCurrent().getWindows().stream().anyMatch(Window::isModal);
    }
}
