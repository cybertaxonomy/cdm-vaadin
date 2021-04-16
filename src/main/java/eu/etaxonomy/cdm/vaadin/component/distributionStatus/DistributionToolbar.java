package eu.etaxonomy.cdm.vaadin.component.distributionStatus;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.util.UserHelper;
import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 * A toolbar for the distribution app. Displayed at the top of the screen.
 */
@SpringComponent("distributionToolbar")
@ViewScope
public class DistributionToolbar extends HorizontalLayout implements Serializable, DisposableBean{

	/**
	 * automatically generated ID
	 */
	private static final long serialVersionUID = 5344340511582993289L;

    private EventBus.UIEventBus uiEventBus;

    @Autowired
    private final void setViewEventBus(EventBus.UIEventBus viewEventBus){
        this.uiEventBus = viewEventBus;
        viewEventBus.subscribe(AuthenticationSuccessEvent.class); // FIXME shouldn't this be .subscribe(this) ??
    }

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    private NavigationManager navigationManager;

    @Autowired
    private UserHelper userHelper;

    private final Button loginButton = new Button(Messages.getLocalizedString(Messages.DistributionToolbar_LOGIN));

    private final Button logoutButton = new Button(Messages.getLocalizedString(Messages.DistributionToolbar_LOGOUT));

    private final Button userButton = new Button(FontAwesome.USER);

	private final Button editButton = new Button(Messages.getLocalizedString(Messages.DistributionToolbar_EDIT));

	private final Button saveButton = new Button(Messages.getLocalizedString(Messages.DistributionToolbar_SAVE));

	private final Button detailButton = new Button(Messages.getLocalizedString(Messages.DistributionToolbar_DETAIL));

	private final Button distributionSettingsButton =  new Button(Messages.getLocalizedString(Messages.DistributionToolbar_AREAS_AND_TAXA));

	private final Button settingsButton =  new Button(Messages.getLocalizedString(Messages.DistributionToolbar_STATUS));

	private final Button helpButton =  new Button(Messages.getLocalizedString(Messages.DistributionToolbar_HELP));

//	private final Authentication authentication;
//	private ExcelExporter exporter = new ExcelExporter();

	/**
	 * Constructs the toolbar.
	 */
	@PostConstruct
    public void init() {
		setMargin(true);
		setSpacing(true);
		setStyleName("toolbar"); //$NON-NLS-1$
		setWidth("100%"); //$NON-NLS-1$
		setHeight("75px"); //$NON-NLS-1$

//		exporter.setCaption("Export");
//		exporter.setIcon(new ThemeResource("icons/32/document-xsl.png"));
		loginButton.addClickListener(e -> performLogin());
		logoutButton.addClickListener(e -> performLogout());
		saveButton.setIcon(new ThemeResource("icons/32/document-save.png")); //$NON-NLS-1$
		editButton.setIcon(new ThemeResource("icons/32/document-edit.png")); //$NON-NLS-1$
		detailButton.setIcon(new ThemeResource("icons/32/document-txt.png")); //$NON-NLS-1$
		settingsButton.setIcon(new ThemeResource("icons/32/settings_1.png")); //$NON-NLS-1$
		distributionSettingsButton.setIcon(new ThemeResource("icons/32/settings_1.png")); //$NON-NLS-1$

        HorizontalLayout leftLayout = new HorizontalLayout();
        leftLayout.addComponent(distributionSettingsButton);
        leftLayout.addComponent(settingsButton);
        leftLayout.addComponent(detailButton);

		HorizontalLayout rightLayout = new HorizontalLayout();
		rightLayout.addComponent(helpButton);
		rightLayout.addComponent(loginButton);
		rightLayout.addComponent(logoutButton);
        rightLayout.addComponent(userButton);

        addComponent(leftLayout);
        setComponentAlignment(leftLayout, Alignment.MIDDLE_LEFT);
		addComponent(rightLayout);
		setComponentAlignment(rightLayout, Alignment.MIDDLE_RIGHT);
		setExpandRatio(rightLayout, 1);
		updateAuthenticationButtons();
    }

	/**
	 * Called when authentication was successful. Updates the user menu buttons.
	 * @param event
	 */
	@EventBusListenerMethod
    public void onAuthenticationSuccessEvent(org.vaadin.spring.events.Event<AuthenticationSuccessEvent> event){
        boolean isInitialized = userButton != null;
        // The RegistrationToolbar is being initialize even if not needed only because it is a EventListener
        // which causes Spring to initialize it.
        // TODO After switching to an other event bus this check can be removed
        if(isInitialized){
            updateAuthenticationButtons();
        }
    }

	/**
	 * Updates the user menu buttons on login and logout.
	 */
    protected void updateAuthenticationButtons() {
        if(userHelper.userIsAutheticated() && !userHelper.userIsAnnonymous()){
            userButton.setCaption(userHelper.userName());
            userButton.setVisible(true);
            logoutButton.setVisible(true);
            loginButton.setVisible(false);
            saveButton.setVisible(true);
            editButton.setVisible(true);
            detailButton.setVisible(true);
            settingsButton.setVisible(true);
            distributionSettingsButton.setVisible(true);
        } else {
            userButton.setCaption(null);
            userButton.setVisible(false);
            logoutButton.setVisible(false);
            loginButton.setVisible(true);
            saveButton.setVisible(false);
            editButton.setVisible(false);
            detailButton.setVisible(false);
            settingsButton.setVisible(false);
            distributionSettingsButton.setVisible(false);
        }
    }

    /**
     * @return
     */
    private void performLogin() {
        uiEventBus.publish(this, new NavigationEvent("login", navigationManager.getCurrentViewName())); //$NON-NLS-1$
    }

    /**
     * Logs the user out and closes the session.
     */
    private void performLogout() {
        userHelper.logout();
        updateAuthenticationButtons();
        navigationManager.reloadCurrentView();
    }

    /**
     * The settings button.
     * @return
     */
    public Button getSettingsButton(){
        return settingsButton;
    }

    /**
     * Button to open the {@link DistributionStatusSettingsConfigWindow}.
     * @return Button to display the {@link DistributionStatusSettingsConfigWindow}.
     */
    public Button getDistributionSettingsButton() {
		return distributionSettingsButton;
	}

    /**
     * Unused.
     * @return
     */
	public Button getEditButton() {
		return editButton;
	}

	/**
	 * Unused.
	 * @return
	 */
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * Button to open the {@link DetailWindow}.
	 * @return  Button to open the {@link DetailWindow}.
	 */
	public Button getDetailButton() {
		return detailButton;
	}

	/**
	 * Button to open the {@link HelpWindow}.
	 * @return Button to open the {@link HelpWindow}.
	 */
	public Button getHelpButton() {
		return helpButton;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        uiEventBus.unsubscribe(this);
    }
}
