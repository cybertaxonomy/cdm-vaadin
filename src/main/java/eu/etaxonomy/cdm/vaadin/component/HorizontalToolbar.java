package eu.etaxonomy.cdm.vaadin.component;

import java.io.Serializable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.server.Page;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.util.CdmVaadinAuthentication;

public class HorizontalToolbar extends HorizontalLayout implements Serializable{


	/**
	 * automatically generated ID
	 */
	private static final long serialVersionUID = 5344340511582993289L;


	private final Button editButton = new Button("Edit");

	private final Button saveButton = new Button("Save");

	private final Button detailButton = new Button("Detail");

	private final Button settingsButton =  new Button("Settings");

	private final Button logoutButton= new Button("Logout");

	private final Authentication authentication;
//	private ExcelExporter exporter = new ExcelExporter();

	public HorizontalToolbar() {
//		authentication = (Authentication) VaadinSession.getCurrent().getAttribute("authentication");
		CdmVaadinAuthentication authentication = (CdmVaadinAuthentication) VaadinSession.getCurrent().getAttribute(CdmVaadinAuthentication.KEY);
		this.authentication = authentication.getAuthentication(Page.getCurrent().getLocation(), VaadinServlet.getCurrent().getServletContext().getContextPath());
		init();
	}

    public void init() {
		if(authentication != null && authentication.isAuthenticated()){
			setMargin(true);
			setSpacing(true);
			setStyleName("toolbar");
			setWidth("100%");
			setHeight("75px");

//			addComponent(editButton);
			addComponent(saveButton);
			addComponent(detailButton);
//			addComponent(exporter);

//			exporter.setCaption("Export");
//			exporter.setIcon(new ThemeResource("icons/32/document-xsl.png"));

			saveButton.setIcon(new ThemeResource("icons/32/document-save.png"));
			editButton.setIcon(new ThemeResource("icons/32/document-edit.png"));
			detailButton.setIcon(new ThemeResource("icons/32/document-txt.png"));
			settingsButton.setIcon(new ThemeResource("icons/32/settings_1.png"));
			logoutButton.setIcon(new ThemeResource("icons/32/cancel.png"));

			//		SecurityContext context = (SecurityContext)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("context");
			SecurityContext context = SecurityContextHolder.getContext();
			Label loginName = new Label(authentication.getName().toString());
			loginName.setIcon(new ThemeResource("icons/32/user.png"));

			HorizontalLayout rightLayout = new HorizontalLayout();
			Image image = new Image(null, new ThemeResource("icons/32/vseparator1.png"));
			rightLayout.addComponent(settingsButton);
			rightLayout.addComponent(logoutButton);
			rightLayout.addComponent(image);
			rightLayout.addComponent(loginName);

			addComponent(rightLayout);
			setComponentAlignment(rightLayout, Alignment.MIDDLE_RIGHT);
			setExpandRatio(rightLayout, 1);

			logoutButton.addClickListener(new ClickListener() {

				/**
				 *  automatically generated ID
				 */
				private static final long serialVersionUID = 8380401487511285303L;

				@Override
                public void buttonClick(ClickEvent event) {

					VaadinSession.getCurrent().close();
					authentication.setAuthenticated(false);
					UI.getCurrent().getNavigator().navigateTo("abstractAuthenticatedUI");
				}
			});
		}
    }

    public Button getSettingsButton(){
        return settingsButton;
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