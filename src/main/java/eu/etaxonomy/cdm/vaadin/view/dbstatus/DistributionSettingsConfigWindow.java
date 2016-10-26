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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.model.location.NamedArea;
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
        //init classification and taxon selection
        TaxonNode chosenTaxonNode = presenter.getChosenTaxonNode();

        try {
            classificationBox.setContainerDataSource(new CdmSQLContainer(CdmQueryFactory.generateTableQuery("Classification")));
        } catch (SQLException e) {
            DistributionEditorUtil.showSqlError(e);
        }
        Object classificationSelection = null;
        if(classificationBox.getItemIds().size()==1){
            //only one classification exists
            classificationSelection = classificationBox.getItemIds().iterator().next();
        }
        else if(chosenTaxonNode!=null){
            //get the classification from the selected taxon node
            classificationSelection = chosenTaxonNode.getClassification().getRootNode();
        }
        if(classificationSelection!=null){
            classificationBox.setValue(classificationSelection);
            taxonTree.addExpandListener(this);

            String uuidString = (String) classificationBox.getContainerProperty(classificationSelection, "uuid").getValue();
            int id = (int) classificationBox.getContainerProperty(classificationSelection, "id").getValue();
            String titleCache = (String) classificationBox.getContainerProperty(classificationSelection, "titleCache").getValue();
            UUID uuid = UUID.fromString(uuidString);
            UuidAndTitleCache<TaxonNode> parent = new UuidAndTitleCache<>(uuid, id, titleCache);
            taxonTree.setContainerDataSource(new TaxonNodeContainer(parent));
            addChildItems(parent);
            if(chosenTaxonNode!=null){
                taxonTree.select(new RowId(chosenTaxonNode.getId()));
            }
        }
        classificationBox.addValueChangeListener(this);

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

        //classification and term
        classificationBox = new ComboBox("Classification");
        classificationBox.setItemCaptionPropertyId(TaxonNodeContainer.LABEL);
        classificationBox.setInputPrompt("Please select a classification...");
        classificationBox.setImmediate(true);
        classificationBox.setNewItemsAllowed(false);
        classificationBox.setNullSelectionAllowed(false);
        classificationBox.setSizeFull();
        classificationBox.setWidth("100%");

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

        leftContainer.addComponent(distAreaBox);
        leftContainer.addComponent(namedAreaList);
        leftContainer.setExpandRatio(distAreaBox, 0.1f);
        leftContainer.setExpandRatio(namedAreaList, 0.9f);
        leftContainer.setSizeFull();

        rightContainer.addComponent(classificationBox);
        rightContainer.addComponent(taxonTree);
        rightContainer.setExpandRatio(classificationBox, 0.1f);
        rightContainer.setExpandRatio(taxonTree, 0.9f);

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
            TaxonNode parentNode = (TaxonNode) event.getProperty().getValue();
            if(parentNode!=null){
                UUID uuid = parentNode.getUuid();
                int id = parentNode.getId();
                String titleCache = parentNode.getClassification().getTitleCache();
                UuidAndTitleCache<TaxonNode> parent = new UuidAndTitleCache<>(uuid, id, titleCache);
                taxonTree.setContainerDataSource(new TaxonNodeContainer(parent));
                addChildItems(parent);
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
            TaxonNode taxonNode = null;
            TermVocabulary<NamedArea> term = null;
            //TODO use field converter
            if(taxonTree.getValue()!=null){
                taxonNode = CdmSpringContextHelper.getTaxonNodeService().load(((UuidAndTitleCache<TaxonNode>)taxonTree.getValue()).getUuid());
            }
            if(taxonNode==null){
                String uuidString = (String) classificationBox.getContainerProperty(classificationBox.getValue(),"uuid").getValue();
                UUID uuid = UUID.fromString(uuidString);
                taxonNode = CdmSpringContextHelper.getClassificationService().load(uuid).getRootNode();
            }
            term = (TermVocabulary<NamedArea>) distAreaBox.getValue();
            Set<NamedArea> selectedAreas = (Set<NamedArea>) namedAreaList.getValue();
            DistributionEditorUtil.openDistributionView(taxonNode, term, selectedAreas);
            window.close();
        }
        else if(source==cancelButton){
            window.close();
        }
    }

    @Override
    public void nodeExpand(ExpandEvent event) {
        UuidAndTitleCache<TaxonNode> parent = (UuidAndTitleCache<TaxonNode>) event.getItemId();
        addChildItems(parent);
    }

    /**
     * @param parent
     */
    private void addChildItems(UuidAndTitleCache<TaxonNode> parent) {
        Collection<UuidAndTitleCache<TaxonNode>> children = CdmSpringContextHelper.getTaxonNodeService().listChildNodesAsUuidAndTitleCache(parent);
        taxonTree.setChildrenAllowed(parent, !children.isEmpty());
        for (UuidAndTitleCache<TaxonNode> child : children) {
            Item childItem = taxonTree.addItem(child);
            if(childItem!=null){
                taxonTree.setParent(child, parent);
            }
            Collection<UuidAndTitleCache<TaxonNode>> grandChildren = CdmSpringContextHelper.getTaxonNodeService().listChildNodesAsUuidAndTitleCache(child);
            taxonTree.setChildrenAllowed(child, !grandChildren.isEmpty());
        }
    }

}
