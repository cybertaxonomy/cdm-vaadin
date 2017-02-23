/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.view.distributionStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.common.CdmUtils;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.persistence.dto.UuidAndTitleCache;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.NamedAreaContainer;
import eu.etaxonomy.cdm.vaadin.container.TaxonNodeContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmQueryFactory;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 *
 * @author pplitzner
 *
 */
public class DistributionSettingsConfigWindow extends AbstractSettingsDialogWindow implements ValueChangeListener, ClickListener, ExpandListener{

    private static final long serialVersionUID = 1439411115014088780L;
    private ComboBox classificationBox;
    private TextField taxonFilter;
    private ComboBox distAreaBox;
    private ListSelect namedAreaList;
    private TreeTable taxonTree;

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     * @param distributionTableView
     */
    public DistributionSettingsConfigWindow(DistributionTableView distributionTableView) {
        super();
    }

    @Override
    protected void init() {
        //init classification
        Classification classification = presenter.getChosenClassification();
        try {
            classificationBox.setContainerDataSource(new CdmSQLContainer(CdmQueryFactory.generateTableQuery("Classification")));
        } catch (SQLException e) {
            DistributionEditorUtil.showSqlError(e);
        }
        RowId parent = null;
        if(classification!=null){
        	parent = new RowId(classification.getRootNode().getId());
        }
        else if(classificationBox.getItemIds().size()==1){
            //only one classification exists
            parent = (RowId) classificationBox.getItemIds().iterator().next();
        }
        if(parent!=null){
            classificationBox.setValue(new RowId(parent.getId()));
            showClassificationTaxa(getUuidAndTitleCacheFromRowId(parent));
        }

        classificationBox.addValueChangeListener(this);
        taxonFilter.addValueChangeListener(this);
        taxonTree.addExpandListener(this);

        TermVocabulary<NamedArea> chosenArea = presenter.getChosenArea();
        distAreaBox.setContainerDataSource(presenter.getDistributionContainer());
        distAreaBox.setValue(chosenArea);
        distAreaBox.addValueChangeListener(this);

        if(chosenArea!=null){
            NamedAreaContainer container = new NamedAreaContainer(chosenArea);
            namedAreaList.setContainerDataSource(container);
        }
        Object selectedAreas = VaadinSession.getCurrent().getAttribute(DistributionEditorUtil.SATTR_SELECTED_AREAS);
        namedAreaList.setValue(selectedAreas);

        okButton.addClickListener(this);
        cancelButton.addClickListener(this);
        updateButtons();
    }

    @Override
    protected AbstractLayout buildMainLayout() {

        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();

        HorizontalLayout leftAndRightContainer = new HorizontalLayout();
        leftAndRightContainer.setImmediate(false);
        leftAndRightContainer.setSizeFull();
        leftAndRightContainer.setMargin(true);
        leftAndRightContainer.setSpacing(true);

        VerticalLayout leftContainer = new VerticalLayout();
        leftContainer.setImmediate(false);
        leftContainer.setSpacing(true);
        leftContainer.setSizeFull();

        VerticalLayout rightContainer = new VerticalLayout();
        rightContainer.setImmediate(false);
        rightContainer.setSpacing(true);
        rightContainer.setSizeFull();

        //classification
        classificationBox = new ComboBox("Classification");
        classificationBox.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
        classificationBox.setInputPrompt("Please select a classification...");
        classificationBox.setImmediate(true);
        classificationBox.setNewItemsAllowed(false);
        classificationBox.setNullSelectionAllowed(false);
        classificationBox.setSizeFull();
        classificationBox.setWidth("100%");

        //taxonFilter
        taxonFilter = new TextField("Filter");
        taxonFilter.setInputPrompt("Filter taxa by name...");
        taxonFilter.setSizeFull();
        taxonFilter.setImmediate(true);

        //distribution area box
        distAreaBox = new ComboBox("Distribution Area:");
        distAreaBox.setInputPrompt("Please select a distribution area...");
        distAreaBox.setImmediate(true);
        distAreaBox.setNullSelectionAllowed(false);
        distAreaBox.setNewItemsAllowed(false);
        distAreaBox.setSizeFull();
        distAreaBox.setWidth("100%");

        // named areas
        namedAreaList = new ListSelect();
        namedAreaList.setCaption("Areas");
        namedAreaList.setSizeFull();
        namedAreaList.setMultiSelect(true);

        //taxonomy
        taxonTree = new TreeTable("Taxonomy");
        taxonTree.setSelectable(true);
        taxonTree.setSizeFull();
        taxonTree.setImmediate(true);
        taxonTree.setCacheRate(20);
        taxonTree.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);
        taxonTree.setMultiSelect(true);

        leftContainer.addComponent(distAreaBox);
        leftContainer.addComponent(namedAreaList);
        leftContainer.setExpandRatio(distAreaBox, 0.1f);
        leftContainer.setExpandRatio(namedAreaList, 0.9f);
        leftContainer.setSizeFull();

        rightContainer.addComponent(classificationBox);
        rightContainer.addComponent(taxonFilter);
        rightContainer.addComponent(taxonTree);
        rightContainer.setExpandRatio(classificationBox, 0.1f);
        rightContainer.setExpandRatio(taxonFilter, 0.1f);
        rightContainer.setExpandRatio(taxonTree, 0.8f);

        leftAndRightContainer.addComponent(leftContainer);
        leftAndRightContainer.addComponent(rightContainer);

        //button toolbar
        HorizontalLayout buttonToolBar = createOkCancelButtons();

        mainLayout.addComponent(leftAndRightContainer);
        mainLayout.addComponent(buttonToolBar);
        mainLayout.setExpandRatio(leftAndRightContainer, 0.9f);
        mainLayout.setExpandRatio(buttonToolBar, 0.1f);
        mainLayout.setComponentAlignment(buttonToolBar, Alignment.BOTTOM_RIGHT);

        return leftAndRightContainer;
    }

    @Override
    public void valueChange(ValueChangeEvent event) {
        Property property = event.getProperty();
        if(property==classificationBox){
        	UuidAndTitleCache<TaxonNode> parent = getUuidAndTitleCacheFromRowId(classificationBox.getValue());
            showClassificationTaxa(parent);
        }
        else if(property==taxonFilter){
            String filterText = taxonFilter.getValue();
            Property uuidProperty = classificationBox.getContainerProperty(classificationBox.getValue(),"uuid");
            if(uuidProperty==null){
            	Notification.show("Please select a classification");
            }
            else{
            	if(CdmUtils.isNotBlank(filterText)){
            		UUID classificationUuid = UUID.fromString((String) uuidProperty.getValue());
            		List<UuidAndTitleCache<TaxonNode>> taxa = CdmSpringContextHelper.getTaxonNodeService().getUuidAndTitleCache(null, filterText, classificationUuid);
            		BeanItemContainer<UuidAndTitleCache<TaxonNode>> container = new BeanItemContainer<>(UuidAndTitleCache.class);
            		taxonTree.setContainerDataSource(container);
            		for (UuidAndTitleCache<TaxonNode> taxon : taxa) {
            			container.addItem(taxon);
            			taxonTree.setChildrenAllowed(taxon, false);
            		}
            		taxonTree.setVisibleColumns("titleCache");
            	}
            	else{
            		UuidAndTitleCache<TaxonNode> parent = getUuidAndTitleCacheFromRowId(classificationBox.getValue());
            		showClassificationTaxa(parent);
            	}
            }
        }
        else if(property==distAreaBox){
            TermVocabulary<NamedArea> vocabulary = (TermVocabulary<NamedArea>) event.getProperty().getValue();
            NamedAreaContainer container = new NamedAreaContainer(vocabulary);
            namedAreaList.setContainerDataSource(container);
        }
        updateButtons();
    }

    @Override
    protected boolean isValid() {
        return classificationBox.getValue()!=null && distAreaBox.getValue()!=null;
    }

    @Override
    public void buttonClick(ClickEvent event) {
        Object source = event.getSource();
        if(source==okButton){
            List<UUID> taxonNodes = new ArrayList<>();
            TermVocabulary<NamedArea> term = null;
            String uuidString = (String) classificationBox.getContainerProperty(classificationBox.getValue(),"uuid").getValue();
            UUID classificationUuid = UUID.fromString(uuidString);
            Set<UuidAndTitleCache<TaxonNode>> treeSelection = (Set<UuidAndTitleCache<TaxonNode>>) taxonTree.getValue();
			if(!treeSelection.isEmpty()){
				for (UuidAndTitleCache<TaxonNode> uuidAndTitleCache : treeSelection) {
					taxonNodes.add(uuidAndTitleCache.getUuid());
				}
            }
            term = (TermVocabulary<NamedArea>) distAreaBox.getValue();
            Set<NamedArea> selectedAreas = (Set<NamedArea>) namedAreaList.getValue();
            DistributionEditorUtil.openDistributionView(taxonNodes, term, selectedAreas, classificationUuid);
            window.close();
        }
        else if(source==cancelButton){
            window.close();
        }
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        UuidAndTitleCache<TaxonNode> parent = (UuidAndTitleCache<TaxonNode>) event.getItemId();
        ((TaxonNodeContainer) taxonTree.getContainerDataSource()).addChildItems(parent);
    }

    private void showClassificationTaxa(UuidAndTitleCache<TaxonNode> parent) {
        final Collection<UuidAndTitleCache<TaxonNode>> children = CdmSpringContextHelper.getTaxonNodeService().listChildNodesAsUuidAndTitleCache(parent);
        // Enable polling and set frequency to 0.5 seconds
        UI.getCurrent().setPollInterval(500);
        taxonTree.setEnabled(false);
        taxonTree.removeAllItems();
        Notification.show("Loading taxa...");

        new TreeUpdater(children).start();
    }

    private UuidAndTitleCache<TaxonNode> getUuidAndTitleCacheFromRowId(Object classificationSelection) {
        String uuidString = (String) classificationBox.getContainerProperty(classificationSelection, "uuid").getValue();
        Property rootNodeContainerProperty = null;
        
        Collection<?> ids = classificationBox.getContainerPropertyIds();
        //use for loop here because the case of the root node id columns differs between some DBs
        for (Object id : ids) {
			if(id instanceof String && ((String) id).toLowerCase().equals("rootnode_id")){
				rootNodeContainerProperty = classificationBox.getContainerProperty(classificationSelection, id);
				break;
			}
		}
		int id = (int) rootNodeContainerProperty.getValue();
        String titleCache = (String) classificationBox.getContainerProperty(classificationSelection, "titleCache").getValue();
        UUID uuid = UUID.fromString(uuidString);
        UuidAndTitleCache<TaxonNode> parent = new UuidAndTitleCache<>(uuid, id, titleCache);
        return parent;
    }

    private class TreeUpdater extends Thread{

    	private Collection<UuidAndTitleCache<TaxonNode>> children;


		public TreeUpdater(Collection<UuidAndTitleCache<TaxonNode>> children) {
			this.children = children;
		}

		@Override
    	public void run() {
			UI.getCurrent().access(new Runnable() {
				@Override
				public void run() {
					taxonTree.setContainerDataSource(new TaxonNodeContainer(children));

			        Notification notification = new Notification("Loading complete");
			        notification.setDelayMsec(500);
			        notification.show(Page.getCurrent());
			        taxonTree.setEnabled(true);

			        //disable polling when all taxa are loaded
			        UI.getCurrent().setPollInterval(-1);
				}
			});
    	}
    }
}