package com.vaadin.devday.ui.navigation;

import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.spring.events.EventBus;

import com.vaadin.navigator.Navigator.UriFragmentManager;
import com.vaadin.server.Page;
import com.vaadin.server.Page.UriFragmentChangedEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

@SpringComponent
@UIScope
public class NavigationUriFragmentManager extends UriFragmentManager {

    private static final long serialVersionUID = -2033745435437337863L;

    @Autowired
    EventBus.UIEventBus eventBus;

	public NavigationUriFragmentManager() {
		super(Page.getCurrent());
	}

	@Override
	public void uriFragmentChanged(UriFragmentChangedEvent event) {
	    eventBus.publish(this, new NavigationEvent(getState()));
	}
}
