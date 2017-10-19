package eu.etaxonomy.cdm.vaadin.component;

import java.io.Serializable;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;

import eu.etaxonomy.cdm.vaadin.security.UserHelper;

public class HorizontalToolbar extends HorizontalLayout implements Serializable{


	/**
	 * automatically generated ID
	 */
	private static final long serialVersionUID = 5344340511582993289L;


	private final Button editButton = new Button("Edit");

	private final Button saveButton = new Button("Save");

	private final Button detailButton = new Button("Detail");

	private final Button distributionSettingsButton =  new Button("Distribution Settings");

	private final Button settingsButton =  new Button("Settings");

//	private final Authentication authentication;
//	private ExcelExporter exporter = new ExcelExporter();

	public HorizontalToolbar() {
//		authentication = (Authentication) VaadinSession.getCurrent().getAttribute("authentication");
//		CdmVaadinAuthentication authentication = (CdmVaadinAuthentication) VaadinSession.getCurrent().getAttribute(CdmVaadinAuthentication.KEY);
//		this.authentication = authentication.getAuthentication(Page.getCurrent().getLocation(), VaadinServlet.getCurrent().getServletContext().getContextPath());
		init();
	}

    public void init() {
		if(UserHelper.fromSession().userIsAutheticated()){
			setMargin(true);
			setSpacing(true);
			setStyleName("toolbar");
			setWidth("100%");
			setHeight("75px");

//			addComponent(editButton);
//			addComponent(saveButton);
			addComponent(detailButton);
//			addComponent(exporter);

//			exporter.setCaption("Export");
//			exporter.setIcon(new ThemeResource("icons/32/document-xsl.png"));

			saveButton.setIcon(new ThemeResource("icons/32/document-save.png"));
			editButton.setIcon(new ThemeResource("icons/32/document-edit.png"));
			detailButton.setIcon(new ThemeResource("icons/32/document-txt.png"));
			settingsButton.setIcon(new ThemeResource("icons/32/settings_1.png"));
			distributionSettingsButton.setIcon(new ThemeResource("icons/32/settings_1.png"));

//          SecurityContext context = (SecurityContext)VaadinService.getCurrentRequest().getWrappedSession().getAttribute("context");
//			SecurityContext context = SecurityContextHolder.getContext();

			HorizontalLayout rightLayout = new HorizontalLayout();
			rightLayout.addComponent(settingsButton);
			rightLayout.addComponent(distributionSettingsButton);

			addComponent(rightLayout);
			setComponentAlignment(rightLayout, Alignment.MIDDLE_RIGHT);
			setExpandRatio(rightLayout, 1);
		}
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
