package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.security.authentication.BadCredentialsException;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinServlet;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.ui.AbstractAuthenticatedUI;

@SpringView
public class AuthenticationView extends CustomComponent implements IAuthenticationComponent, ClickListener , View {

	/*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */


	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	@AutoGenerated
	private VerticalLayout mainLayout;
	@AutoGenerated
	private Panel loginPanel;
	@AutoGenerated
	private VerticalLayout loginPanelLayout;
	@AutoGenerated
	private Button loginBtn;
	@AutoGenerated
	private PasswordField passwordField;
	@AutoGenerated
	private TextField userNameTF;
	private AuthenticationComponentListener authListener;

	/**
	 * The constructor should first build the main layout, set the
	 * composition root and then do any custom initialization.
	 *
	 * The constructor will not be automatically regenerated by the
	 * visual editor.
	 */
	public AuthenticationView() {
		buildMainLayout();
		setStyleName("login");
		setCompositionRoot(mainLayout);
		userNameTF.addValidator(new StringLengthValidator("It must be 3-25 characters", 3, 25, false));
		userNameTF.setNullRepresentation("");
		userNameTF.focus();

		authListener = new AuthenticationPresenter();

		loginBtn.addClickListener(this);
		loginBtn.setClickShortcut(KeyCode.ENTER, null);
	}

	@Override
	public void addListener(AuthenticationComponentListener listener) {
		this.authListener = listener;

	}

	@Override
	public void buttonClick(ClickEvent event) {
	    boolean isAuthenticated = false;
	    try {
	        isAuthenticated = authListener.login(Page.getCurrent().getLocation(),
	                VaadinServlet.getCurrent().getServletContext().getContextPath(),
	                userNameTF.getValue(),
	                passwordField.getValue());
	        if(isAuthenticated) {
	            // we are sure that since we are in the authentication view that the
	            // current ui should be of type AbstractAuthenticatedUI
	            AbstractAuthenticatedUI aaui = (AbstractAuthenticatedUI) UI.getCurrent();
	            UI.getCurrent().getNavigator().navigateTo(aaui.getFirstViewName());
	        }
	    } catch(BadCredentialsException e){
	        Notification.show("Bad credentials", Notification.Type.ERROR_MESSAGE);
	    }
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub

	}

	@AutoGenerated
	private VerticalLayout buildMainLayout() {
		// common part: create layout
		mainLayout = new VerticalLayout();
		mainLayout.setImmediate(false);
		mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setMargin(false);

		// top-level component properties
		setWidth("100.0%");
		setHeight("100.0%");

		// loginPanel
		loginPanel = buildLoginPanel();
		mainLayout.addComponent(loginPanel);
		mainLayout.setExpandRatio(loginPanel, 1.0f);
		mainLayout.setComponentAlignment(loginPanel, new Alignment(48));

		return mainLayout;
	}

	@AutoGenerated
	private Panel buildLoginPanel() {
		// common part: create layout
		loginPanel = new Panel();
		loginPanel.setImmediate(false);
		loginPanel.setWidth("-1px");
		loginPanel.setHeight("-1px");

		// loginPanelLayout
		loginPanelLayout = buildLoginPanelLayout();
		loginPanel.setContent(loginPanelLayout);

		return loginPanel;
	}

	@AutoGenerated
	private VerticalLayout buildLoginPanelLayout() {
		// common part: create layout
		loginPanelLayout = new VerticalLayout();
		loginPanelLayout.setImmediate(false);
		loginPanelLayout.setWidth("-1px");
		loginPanelLayout.setHeight("-1px");
		loginPanelLayout.setMargin(true);
		loginPanelLayout.setSpacing(true);

		// userNameTF
		userNameTF = new TextFieldNFix();
		userNameTF.setCaption("User Name");
		userNameTF.setImmediate(false);
		userNameTF.setWidth("-1px");
		userNameTF.setHeight("-1px");
		userNameTF.setInvalidAllowed(false);
		userNameTF.setRequired(true);
		loginPanelLayout.addComponent(userNameTF);
		loginPanelLayout.setComponentAlignment(userNameTF, new Alignment(48));

		// passwordField
		passwordField = new PasswordField();
		passwordField.setCaption("Password");
		passwordField.setImmediate(false);
		passwordField.setWidth("-1px");
		passwordField.setHeight("-1px");
		passwordField.setInvalidAllowed(false);
		passwordField.setRequired(true);
		loginPanelLayout.addComponent(passwordField);
		loginPanelLayout
				.setComponentAlignment(passwordField, new Alignment(48));

		// loginBtn
		loginBtn = new Button();
		loginBtn.setCaption("Login");
		loginBtn.setImmediate(true);
		loginBtn.setWidth("-1px");
		loginBtn.setHeight("-1px");
		loginPanelLayout.addComponent(loginBtn);
		loginPanelLayout.setComponentAlignment(loginBtn, new Alignment(48));

		return loginPanelLayout;
	}
}
