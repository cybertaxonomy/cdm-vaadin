package eu.etaxonomy.vaadin.ui.navigation;

public class NavigationEvent {

	public static final char SEPARATOR = '/';

    private final String viewName;

	public NavigationEvent(String viewName, String ... parameters) {
	    StringBuilder sb = new StringBuilder(viewName);
        for(String p : parameters){
            if(p != null){
                sb.append(SEPARATOR).append(p);
            }
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
