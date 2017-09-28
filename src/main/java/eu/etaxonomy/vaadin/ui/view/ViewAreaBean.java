package eu.etaxonomy.vaadin.ui.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

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

@SpringComponent
@UIScope
class ViewAreaBean extends HorizontalLayout implements ViewDisplay, ToolbarDisplay {

    private static final long serialVersionUID = -3763800167385449693L;

	private MainMenu mainMenu;

	private Component toolbar = null;

	private CssLayout contentArea;

	private CssLayout mainArea;

    public ViewAreaBean() {

        setSizeFull();

        mainArea = new CssLayout();
        mainArea.setPrimaryStyleName("valo-toolbar");
        mainArea.setSizeFull();
        contentArea = new CssLayout();
        contentArea.setPrimaryStyleName("valo-content");
        contentArea.addStyleName("v-scrollable");
        contentArea.setSizeFull();

        mainArea.addComponent(contentArea);
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
		contentArea.removeAllComponents();
		contentArea.addComponent(Component.class.cast(view));
	}

}
