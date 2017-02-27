package com.vaadin.devday.ui.navigation;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.vaadin.devday.ui.NavigationManager;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
class ViewChangeListenerBean implements ViewChangeListener {
	private static final long serialVersionUID = 1913421359807383L;

	@Autowired
	private Collection<ViewChangeAllowedVerifier> verifiers;

	@Autowired
	private NavigationManager navigationManager;

	@Autowired
    EventBus.UIEventBus eventBus;

	@Override
	public boolean beforeViewChange(ViewChangeEvent event) {
		View currentView = navigationManager.getCurrentView();
		if (currentView != null) {
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
	    eventBus.publish(this, new AfterViewChangeEvent());
	}

}
