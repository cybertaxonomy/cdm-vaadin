package eu.etaxonomy.cdm.vaadin.ui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.RedirectToLoginView;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.DistributionTableViewBean;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;

@Theme("macosx")
@Title("Distribution Editor")
@SpringUI(path="distribution")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DistributionStatusUI extends UI{

    private final static Logger logger = Logger.getLogger(DistributionStatusUI.class);

    private ViewDisplay viewDisplay;

    //---- pull into abstract super class ? ---------
    @Autowired
    SpringViewProvider viewProvider;

    @Autowired
    NavigationManagerBean navigator;

    protected void configureAccessDeniedView() {
        viewProvider.setAccessDeniedViewClass(RedirectToLoginView.class);
    }

    /**
     * @return
     */
    private String pageFragmentAsState() {
        Page page = Page.getCurrent();
        String fragment = page.getUriFragment();
        String state = null;
        if(fragment != null && fragment.startsWith("!")){
            state = fragment.substring(1, fragment.length());
        }
        return state;
    }
    //---------------------------------------------

    public static final String INITIAL_VIEW =  DistributionTableViewBean.NAME;

//    @Autowired
//    @Qualifier("registrationToolbar")
//    private Toolbar toolbar;

    @Autowired
    ApplicationEventPublisher eventBus;

    public DistributionStatusUI() {

    }

	@Override
	protected void init(VaadinRequest request) {
        configureAccessDeniedView();

        Responsive.makeResponsive(this);

        viewDisplay = new Navigator.SingleComponentContainerViewDisplay(this);
        navigator.setViewDisplay(viewDisplay);
        //setContent((Component) viewDisplay);

//        if(ToolbarDisplay.class.isAssignableFrom(viewDisplay.getClass())){
//            ((ToolbarDisplay)viewDisplay).setToolbar(toolbar);
//        }

        eventBus.publishEvent(new UIInitializedEvent());

        navigator.setDefaultViewName(INITIAL_VIEW);

	}
}
