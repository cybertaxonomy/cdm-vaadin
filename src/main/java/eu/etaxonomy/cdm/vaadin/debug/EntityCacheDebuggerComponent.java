/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.debug;

import java.util.List;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

import eu.etaxonomy.cdm.cache.CdmTransientEntityCacher;
import eu.etaxonomy.cdm.cache.EntityCacherDebugResult;
import eu.etaxonomy.cdm.cache.EntityCacherDebugResult.CdmEntityInfo;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

/**
 * @author a.kohlbecker
 * @since Jan 22, 2018
 *
 */
public class EntityCacheDebuggerComponent extends CustomComponent {

    private static final long serialVersionUID = 993360128659237814L;

    CachingPresenter presenter;
    CdmTransientEntityCacher cacher;
    EntityCacherDebugResult debugResults;

    VerticalLayout layout = new VerticalLayout();
    VerticalSplitPanel splitPanel = new VerticalSplitPanel();

    private Tree entityTree;

    public EntityCacheDebuggerComponent(CachingPresenter presenter){

        this.presenter = presenter;
        this.cacher = (CdmTransientEntityCacher) presenter.getCache();
        // TODO implement checkbox for includeIgnored
        boolean includeIgnored = true;
        debugResults = new EntityCacherDebugResult(cacher, presenter.getRootEntities(), includeIgnored);
        initContent();
    }

    private void initContent() {


        TextField filterField = new TextFieldNFix();
        filterField.setInputPrompt("Enter filter text");
        filterField.addTextChangeListener(e -> filterTree(e.getText()));
        filterField.setWidth("100%");

        entityTree = new Tree("Cache Content");
        buildTree(entityTree, debugResults.getRootElements());

        Label debugInformation = new Label(); // );
        debugInformation.setCaption("Debug Information");
        debugInformation.setValue(debugResults.toString());
        debugInformation.setContentMode(ContentMode.PREFORMATTED);

        setCompositionRoot(layout);

        entityTree.setSizeUndefined();
        debugInformation.setSizeUndefined();
        debugInformation.setReadOnly(true);
        splitPanel.setFirstComponent(entityTree);
        splitPanel.setSecondComponent(debugInformation);
        splitPanel.setSplitPosition(50, Unit.PERCENTAGE);
        splitPanel.setSizeFull();

        layout.addComponents(filterField, splitPanel);
        layout.setSizeFull();
        layout.setExpandRatio(splitPanel, 1.0f);


    }

    /**
     * @param value
     * @return
     */
    private void filterTree(String text) {
        IndexedContainer indexedContainer = (IndexedContainer)entityTree.getContainerDataSource();
        indexedContainer.removeAllContainerFilters();

        if(!text.isEmpty()){
            SimpleStringFilter filter = new SimpleStringFilter("label", text, true, false);
            indexedContainer.addContainerFilter(filter);
            ((HierarchicalContainer)entityTree.getContainerDataSource()).rootItemIds().forEach(rid -> entityTree.expandItemsRecursively(rid));
        }
    }

    /**
     * @param entityTree
     * @param rootElements
     */
    private void buildTree(Tree tree, List<CdmEntityInfo> childElements) {

        HierarchicalContainer container = new HierarchicalContainer();

        container.addContainerProperty("label", String.class, "");
        tree.setItemCaptionPropertyId("label");
        buildTree(container, childElements, null);

        tree.setContainerDataSource(container);

        container.setItemSorter(new CdmEntityInfoSorter());

    }

    private void buildTree(HierarchicalContainer container, List<CdmEntityInfo> childElements, Object parentItemId) {
        for(CdmEntityInfo cei : childElements){
            String itemId = cei.getLabel();
            container.addItem(itemId);
            container.getItem(itemId).getItemProperty("label").setValue(itemId);
            if(parentItemId != null){
                container.setParent(itemId, parentItemId);
            }
            if(cei.getChildren().isEmpty()){
                container.setChildrenAllowed(itemId, false);
            } else {
                buildTree(container, cei.getChildren(), itemId);
            }
        }
    }


}
