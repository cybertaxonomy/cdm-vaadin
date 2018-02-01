package eu.etaxonomy.vaadin.ui.navigation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.vaadin.spring.events.EventBus.UIEventBus;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent("viewChangeListenerBean")
@UIScope
class ViewChangeListenerBean implements ViewChangeListener {
	private static final long serialVersionUID = 1913421359807383L;

	@Autowired(required=false)
	private Collection<ViewChangeAllowedVerifier> verifiers = null;

	@Autowired
	@Lazy
	private NavigationManager navigationManager;

	@Autowired
    UIEventBus uiEventBus;

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		View currentView = navigationManager.getCurrentView();
		if (currentView != null && verifiers != null) {
			for (ViewChangeAllowedVerifier verifier : verifiers) {
				if (currentView.equals(verifier)) {
					if (!verifier.isViewChangeAllowed()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public void afterViewChange(ViewChangeEvent event) {
	    uiEventBus.publish(this, new AfterViewChangeEvent());
	}

}
