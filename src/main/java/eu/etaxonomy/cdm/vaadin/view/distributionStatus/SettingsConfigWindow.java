/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
public class SettingsConfigWindow extends AbstractSettingsDialogWindow implements ValueChangeListener, ClickListener{

	private static final long serialVersionUID = -8220442386869594032L;
    private TwinColSelect distStatusSelect;
    private CheckBox boxToggleAbbreviatedLabels;
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
    	super();
    	this.distributionTableView = distributionTableView;
    }

    @Override
    protected void init() {
        boxToggleAbbreviatedLabels.addValueChangeListener(this);
        distStatusSelect.setContainerDataSource(presenter.getDistributionStatusContainer());

        okButton.addClickListener(this);
        cancelButton.addClickListener(this);
        updateButtons();
    }

    @Override
    protected AbstractLayout buildMainLayout() {

    	mainLayout = new VerticalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setSizeFull();
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);

        //distribution status
        distStatusSelect = new TwinColSelect("Distribution Status:");
        distStatusSelect.setImmediate(false);
        distStatusSelect.setSizeFull();
        distStatusSelect.setWidth("100%");

        //toggle abbreviated labels
        boxToggleAbbreviatedLabels = new CheckBox("Show abbreviated labels", DistributionEditorUtil.isAbbreviatedLabels());
        boxToggleAbbreviatedLabels.setImmediate(true);

        mainLayout.addComponent(boxToggleAbbreviatedLabels);
        mainLayout.addComponent(distStatusSelect);
        mainLayout.setExpandRatio(distStatusSelect, 1);
        mainLayout.setSizeFull();

        //button toolbar
        HorizontalLayout buttonContainer = createOkCancelButtons();

        mainLayout.addComponent(buttonContainer);
        mainLayout.setComponentAlignment(buttonContainer, Alignment.BOTTOM_RIGHT);

        return mainLayout;
    }

    @Override
    protected boolean isValid() {
    	return true;
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
			VaadinSession.getCurrent().setAttribute(DistributionEditorUtil.SATTR_DISTRIBUTION_STATUS, distStatusSelect.getValue());
			distributionTableView.enter(null);
			window.close();
		}
		else if(source==cancelButton){
			window.close();
		}
	}

}
