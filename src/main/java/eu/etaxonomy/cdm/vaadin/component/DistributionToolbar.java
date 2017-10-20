package eu.etaxonomy.cdm.vaadin.component;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.ThemeResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.service.CdmUserHelper;
import eu.etaxonomy.cdm.vaadin.event.AuthenticationSuccessEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

@SpringComponent("distributionToolbar")
@UIScope
public class DistributionToolbar extends HorizontalLayout implements Serializable{

	/**
	 * automatically generated ID
	 */
	private static final long serialVersionUID = 5344340511582993289L;

    @Autowired
    protected ApplicationEventPublisher eventBus;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    private NavigationManager navigationManager;

    @Autowired
    private CdmUserHelper userHelper;

    private final Button loginButton = new Button("Login");

    private final Button logoutButton = new Button("Logout");

    private final Button userButton = new Button(FontAwesome.USER);

	private final Button editButton = new Button("Edit");

	private final Button saveButton = new Button("Save");

	private final Button detailButton = new Button("Detail");

	private final Button distributionSettingsButton =  new Button("Areas and Taxa");

	private final Button settingsButton =  new Button("Status");

//	private final Authentication authentication;
//	private ExcelExporter exporter = new ExcelExporter();

	@PostConstruct
    public void init() {
		setMargin(true);
		setSpacing(true);
		setStyleName("toolbar");
		setWidth("100%");
		setHeight("75px");

//		exporter.setCaption("Export");
//		exporter.setIcon(new ThemeResource("icons/32/document-xsl.png"));
		loginButton.addClickListener(e -> performLogin());
		logoutButton.addClickListener(e -> performLogout());
		saveButton.setIcon(new ThemeResource("icons/32/document-save.png"));
		editButton.setIcon(new ThemeResource("icons/32/document-edit.png"));
		detailButton.setIcon(new ThemeResource("icons/32/document-txt.png"));
		settingsButton.setIcon(new ThemeResource("icons/32/settings_1.png"));
		distributionSettingsButton.setIcon(new ThemeResource("icons/32/settings_1.png"));

        HorizontalLayout leftLayout = new HorizontalLayout();
        leftLayout.addComponent(detailButton);
        leftLayout.addComponent(settingsButton);
        leftLayout.addComponent(distributionSettingsButton);

		HorizontalLayout rightLayout = new HorizontalLayout();
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

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent event){
        boolean isInitialized = userButton != null;
        // The RegistrationToolbar is being initialize even if not needed only because it is a EventListener
        // which causes Spring to initialize it.
        // TODO After switching to an other event bus this check can be removed
        if(isInitialized){
            updateAuthenticationButtons();
        }
    }

    /**
     * @param event
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
        eventBus.publishEvent(new NavigationEvent("login", navigationManager.getCurrentViewName()));
    }


    private void performLogout() {
        userHelper.logout();
        updateAuthenticationButtons();
        navigationManager.reloadCurrentView();
    }

    public Button getSettingsButton(){
        return settingsButton;
    }

    public Button getDistributionSettingsButton() {
		return distributionSettingsButton;
	}

	public Button getEditButton() {
		return editButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getDetailButton() {
		return detailButton;
	}
}
