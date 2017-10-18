package eu.etaxonomy.cdm.vaadin.ui;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringViewProvider;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.RedirectToLoginView;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.DistributionTableViewBean;
import eu.etaxonomy.vaadin.ui.UIInitializedEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

@Theme("macosx")
@Title("Distribution Editor")
@SpringUI(path="distribution")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
@SuppressWarnings("serial")
public class DistributionStatusUI extends UI{

    private final static Logger logger = Logger.getLogger(DistributionStatusUI.class);
    
    @Autowired
    private ViewDisplay viewDisplay;


    //---- pull into abstract super class ? ---------
    @Autowired
    SpringViewProvider viewProvider;

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

    @Autowired
    ApplicationEventPublisher eventBus;
    
    public DistributionStatusUI() {

    }

	@Override
	protected void init(VaadinRequest request) {
        configureAccessDeniedView();

        Responsive.makeResponsive(this);

        setContent((Component) viewDisplay);
       
        eventBus.publishEvent(new UIInitializedEvent());

        //navigate to initial view
        String state = pageFragmentAsState();

        if(state != null){
            eventBus.publishEvent(new NavigationEvent(state));
        } else {
            eventBus.publishEvent(new NavigationEvent(INITIAL_VIEW));
        }
	}
}
