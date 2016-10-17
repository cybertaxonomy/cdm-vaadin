// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.dbstatus;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.settings.SettingsPresenter;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
public class SettingsConfigWindow extends CustomComponent implements ValueChangeListener, ClickListener{

	private static final long serialVersionUID = -8220442386869594032L;
    private VerticalLayout mainLayout;
    private TwinColSelect distStatusSelect;
    private CheckBox boxToggleAbbreviatedLabels;
    private Button okButton;
    private Button cancelButton;
    private final SettingsPresenter presenter;
	private Window window;
	private DistributionTableView distributionTableView;

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     * @param distributionTableView 
     */
    public SettingsConfigWindow(DistributionTableView distributionTableView) {
    	this.distributionTableView = distributionTableView;
        buildMainLayout();
        presenter = new SettingsPresenter();
        init();
    }

    private void init() {
        boxToggleAbbreviatedLabels.addValueChangeListener(this);
        distStatusSelect.setContainerDataSource(presenter.getDistributionStatusContainer());

        okButton.addClickListener(this);
        cancelButton.addClickListener(this);
    }

    public Window createWindow(){
        window = new Window();
        window.setModal(true);
        window.setWidth("60%");
        window.setHeight("80%");
        window.setCaption("Settings");
        window.setContent(mainLayout);
        return window;
    }

    private AbstractLayout buildMainLayout() {

    	mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        //distribution status
        distStatusSelect = new TwinColSelect("Distribution Status:");
        distStatusSelect.setImmediate(false);
        distStatusSelect.setWidth("100%");
        
        //toggle abbreviated labels
        boxToggleAbbreviatedLabels = new CheckBox("Show abbreviated labels", DistributionEditorUtil.isAbbreviatedLabels());
        boxToggleAbbreviatedLabels.setImmediate(true);

        mainLayout.addComponent(boxToggleAbbreviatedLabels);
        mainLayout.addComponent(distStatusSelect);
        mainLayout.setExpandRatio(distStatusSelect, 1);
        mainLayout.setSizeFull();

        //button toolbar
        HorizontalLayout buttonContainer = new HorizontalLayout();
        // cancelButton
        cancelButton = new Button();
        cancelButton.setCaption("Cancel");
        cancelButton.setImmediate(true);
        buttonContainer.addComponent(cancelButton);

        // okButton
        okButton = new Button();
        okButton.setCaption("OK");
        okButton.setImmediate(true);
        buttonContainer.addComponent(okButton);

        mainLayout.addComponent(buttonContainer);
        mainLayout.setComponentAlignment(buttonContainer, Alignment.BOTTOM_RIGHT);

        return mainLayout;
    }

	@Override
	public void valueChange(ValueChangeEvent event) {
		Property property = event.getProperty();
		if(property==boxToggleAbbreviatedLabels){
			VaadinSession.getCurrent().setAttribute(DistributionEditorUtil.SATTR_ABBREVIATED_LABELS, event.getProperty().getValue());
		}
	}

	@Override
	public void buttonClick(ClickEvent event) {
		Object source = event.getSource();
		if(source==okButton){
			distributionTableView.enter(null);
			window.close();
		}
		else if(source==cancelButton){
			window.close();
		}
	}

}
