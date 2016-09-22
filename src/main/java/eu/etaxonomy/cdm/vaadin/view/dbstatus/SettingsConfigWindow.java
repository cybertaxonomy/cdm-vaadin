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

import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Tree;
import com.vaadin.ui.TwinColSelect;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.vaadin.container.TaxonNodeContainer;
import eu.etaxonomy.cdm.vaadin.presenter.dbstatus.settings.SettingsPresenter;

/**
 * @author alex
 * @date 22.04.2015
 *
 */
public class SettingsConfigWindow extends CustomComponent {

	private static final long serialVersionUID = -8220442386869594032L;
    private VerticalLayout mainLayout;
    private TwinColSelect distStatusSelect;
    private ComboBox classificationBox;
    private ComboBox distAreaBox;
    private Tree taxonTree;
    private Button okButton;
    private Button cancelButton;
    private final SettingsPresenter presenter;
	private Window window;
    
    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public SettingsConfigWindow() {
        buildMainLayout();
        presenter = new SettingsPresenter();
        init();
    }

    private void init() {
        Container taxonNodeContainer = new TaxonNodeContainer(null);
        Container distributionContainer = presenter.getDistributionContainer();
        TermVocabulary<?> chosenArea = presenter.getChosenArea();
        classificationBox.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
        classificationBox.setContainerDataSource(taxonNodeContainer);
        classificationBox.setValue(presenter.getChosenTaxonNode().getClassification().getRootNode());
        classificationBox.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				TaxonNode parentNode = (TaxonNode) event.getProperty().getValue();
				taxonTree.setContainerDataSource(new TaxonNodeContainer(parentNode));
			}
		});
        taxonTree.setContainerDataSource(new TaxonNodeContainer((TaxonNode) classificationBox.getValue()));
        taxonTree.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
        taxonTree.setValue(presenter.getChosenTaxonNode());
        distAreaBox.setContainerDataSource(distributionContainer);
        distAreaBox.setValue(chosenArea);
        distStatusSelect.setContainerDataSource(presenter.getDistributionStatusContainer());
        
        okButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				TaxonNode taxonNode;
				TermVocabulary<DefinedTermBase> term = null;
				taxonNode = (TaxonNode) taxonTree.getValue();
				if(taxonNode==null){
					taxonNode = (TaxonNode) classificationBox.getValue();
				}
				term = (TermVocabulary<DefinedTermBase>) distAreaBox.getValue();
				if(taxonNode==null){
					Notification.show("Please choose a classification and/or taxon", Notification.Type.HUMANIZED_MESSAGE);
					return;
				}
				if(term==null){
					Notification.show("Please choose a distribution area", Notification.Type.HUMANIZED_MESSAGE);
					return;
				}

			    VaadinSession.getCurrent().setAttribute("taxonNodeUUID", taxonNode.getUuid());
			    VaadinSession.getCurrent().setAttribute("selectedTerm", term.getUuid());

			    window.close();
		        UI.getCurrent().getNavigator().navigateTo("table");				
			}
		});
        cancelButton.addClickListener(new ClickListener() {
			
			@Override
			public void buttonClick(ClickEvent event) {
				window.close();
			}
		});
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
        
        HorizontalLayout topContainer = new HorizontalLayout();
        topContainer.setImmediate(false);
        topContainer.setSizeFull();
        topContainer.setSpacing(true);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setImmediate(false);
        
        //classification and term
        classificationBox = new ComboBox("Classification");
        classificationBox.setImmediate(true);
        classificationBox.setWidth("100%");

        distAreaBox = new ComboBox("Distribution Area:");
        distAreaBox.setImmediate(false);
        distAreaBox.setWidth("100%");
        
        //distribution status
        distStatusSelect = new TwinColSelect("Distribution Status:");
        distStatusSelect.setImmediate(false);
        distStatusSelect.setWidth("100%");

        //taxonomy
        taxonTree = new Tree("Taxonomy");
        taxonTree.setImmediate(false);
        
        verticalLayout.addComponent(classificationBox);
        verticalLayout.addComponent(distAreaBox);
        verticalLayout.addComponent(distStatusSelect);
        verticalLayout.setExpandRatio(distStatusSelect, 1);
        verticalLayout.setSizeFull();
        
        topContainer.addComponent(verticalLayout);
        topContainer.addComponent(taxonTree);
        topContainer.setExpandRatio(taxonTree, 1);
        topContainer.setExpandRatio(verticalLayout, 1);

        //button toolbar
        HorizontalLayout buttonToolBar = new HorizontalLayout();
        // cancelButton
        cancelButton = new Button();
        cancelButton.setCaption("Cancel");
        cancelButton.setImmediate(true);
        buttonToolBar.addComponent(cancelButton);

        // okButton
        okButton = new Button();
        okButton.setCaption("OK");
        okButton.setImmediate(true);
        buttonToolBar.addComponent(okButton);

        mainLayout.addComponent(topContainer);
        mainLayout.addComponent(buttonToolBar);
        mainLayout.setComponentAlignment(buttonToolBar, Alignment.BOTTOM_RIGHT);

        return mainLayout;
    }

}
