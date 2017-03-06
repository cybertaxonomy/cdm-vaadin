package eu.etaxonomy.vaadin.ui.navigation;

import com.vaadin.client.renderers.ClickableRenderer.RendererClickEvent;

public class NavigationEvent {

	public static final char SEPARATOR = '/';

    private final String viewName;

	public NavigationEvent(String viewName, RendererClickEvent event) {
		this.viewName = viewName;
	}

	public NavigationEvent(String viewName, String ... parameters) {
	    StringBuilder sb = new StringBuilder(viewName);
        for(String p : parameters){
            sb.append(SEPARATOR).append(p);
        }
        this.viewName = sb.toString();
    }

	public NavigationEvent(String viewName) {
        this.viewName = viewName;
    }

	public String getViewName() {
		return viewName;
	}

}
