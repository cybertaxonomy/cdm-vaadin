package eu.etaxonomy.vaadin.ui.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.toolbar.Toolbar;
import eu.etaxonomy.vaadin.ui.MainMenu;

/**
 * ViewAreaBean is the default implementation of ViewArea that is used to
 * position the fundamental UI elements on screen. ViewAreaBean supports dynamic
 * main menu assignment as well as allows easier handling of popup windows.
 *
 * @author Peter / Vaadin
 * @author Andreas Kohlbecker - ported to Spring
 */

@SpringComponent("viewAreaBean")
@UIScope
class ViewAreaBean extends HorizontalLayout implements ViewDisplay, ToolbarDisplay {

    private static final long serialVersionUID = -3763800167385449693L;

	private MainMenu mainMenu;

	private Component toolbar = null;

	// private VerticalLayout contentArea;

	private VerticalLayout mainArea;

	private Component currentViewComponent = null;

    public ViewAreaBean() {

        setSizeFull();

        mainArea = new VerticalLayout();
        mainArea.setPrimaryStyleName("valo-toolbar");
        mainArea.setSizeFull();
        mainArea.setMargin(new MarginInfo(false, false, true, false));

        addComponent(mainArea);
        setExpandRatio(mainArea, 1);
    }

	@Autowired
	public void setMainMenu(MainMenu mainMenu) {
	    this.mainMenu = mainMenu;
	    addComponentAsFirst(this.mainMenu.asComponent());
	}


    @Override
    public void setToolbar(Toolbar toolbar) {
        toolbar.initialize();
        this.toolbar = toolbar.asComponent();
        this.toolbar.setPrimaryStyleName("valo-navigation-bar");
        mainArea.addComponentAsFirst(this.toolbar);
    }

	@Override
	public void showView(View view) {
	    if(currentViewComponent != null){
	        mainArea.removeComponent(currentViewComponent);
	    }
	    currentViewComponent = Component.class.cast(view);
	    mainArea.addComponent(currentViewComponent);
	    mainArea.setExpandRatio(Component.class.cast(view), 1);
	}

}
