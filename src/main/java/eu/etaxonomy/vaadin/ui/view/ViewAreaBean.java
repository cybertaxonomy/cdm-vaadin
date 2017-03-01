package eu.etaxonomy.vaadin.ui.view;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;

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
class ViewAreaBean extends HorizontalLayout implements ViewDisplay {

    private static final long serialVersionUID = -3763800167385449693L;

	private MainMenu mainMenu;

	private CssLayout contentArea;

	public ViewAreaBean() {
		setSizeFull();

		contentArea = new CssLayout();
		contentArea.setPrimaryStyleName("valo-content");
		contentArea.addStyleName("v-scrollable");
		contentArea.setSizeFull();

		addComponent(contentArea);
		setExpandRatio(contentArea, 1);
	}

	@Autowired
	public void setMainMenu(MainMenu mainMenu) {
	    this.mainMenu = mainMenu;
	    addComponentAsFirst(this.mainMenu.asComponent());
	}

// TODO was this needed to avoid bean loading probelms? Otherwise remove it
//	private MainMenu mainMenuInstantiator;
//	@PostConstruct
//	protected void initialize() {
//		if (mainMenuInstantiator.isAmbiguous()) {
//			throw new RuntimeException("Ambiguous main menu implementations available, please refine your deployment");
//		}
//
//		if (!mainMenuInstantiator.isUnsatisfied()) {
//			mainMenu = mainMenuInstantiator.get();
//			addComponentAsFirst(mainMenu.asComponent());
//		}
//	}

	@Override
	public void showView(View view) {
		contentArea.removeAllComponents();
		contentArea.addComponent(Component.class.cast(view));
	}

}
