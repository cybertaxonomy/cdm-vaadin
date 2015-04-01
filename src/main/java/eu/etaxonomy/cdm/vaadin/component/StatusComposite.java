// $Id$
/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.component;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.event.Action;
import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnHeaderMode;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Tree.ExpandEvent;
import com.vaadin.ui.Tree.ExpandListener;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.view.IStatusComposite;

/**
 * @author cmathew
 * @date 11 Mar 2015
 *
 */
public class StatusComposite extends CustomComponent implements IStatusComposite {

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private GridLayout mainLayout;
    @AutoGenerated
    private Label inViewLabel;
    @AutoGenerated
    private TreeTable taxaTreeTable;
    @AutoGenerated
    private HorizontalLayout searchHorizontalLayout;
    @AutoGenerated
    private Button clearSearchButton;
    @AutoGenerated
    private TextField searchTextField;
    @AutoGenerated
    private HorizontalLayout addRemovehorizontalLayout;
    @AutoGenerated
    private Button removeButton;
    @AutoGenerated
    private ComboBox addComboBox;
    @AutoGenerated
    private VerticalLayout filterVerticalLayout;
    @AutoGenerated
    private Table filterTable;
    @AutoGenerated
    private Label filterLabel;
    @AutoGenerated
    private ComboBox classificationComboBox;
    private static final Logger logger = Logger.getLogger(StatusComposite.class);
    private StatusComponentListener listener;

    private static final String SELECT_FILTER = "Select filter ...";
    private static final String SELECT_CLASSIFICATION = "Select classification ...";
    private static final String ADD_TAXON_SYNONYM = "Add ...";
    private static final String PROPERTY_FILTER_ID = "filter";
    private static final String PROPERTY_SELECTED_ID = "selected";

    private static final String FILTER_NOT_RESOLVED = "not resolved";
    private static final String FILTER_UNPLACED = "unplaced";
    private static final String FILTER_UNFINISHED = "unfinished";
    private static final String FILTER_UNPUBLISHED = "unpublished";


    private static final String FILTER_TAXA_INPUT = "Filter Taxa ...";
    private static final String IN_VIEW_PREFIX = "in view : ";

    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public StatusComposite() {
        buildMainLayout();
        setCompositionRoot(mainLayout);

        searchHorizontalLayout.addLayoutClickListener(new LayoutClickListener() {

            @Override
            public void layoutClick(LayoutClickEvent event) {
                if (event.getChildComponent() == searchTextField && searchTextField.getValue().equals(FILTER_TAXA_INPUT)) {
                   searchTextField.setValue("");
                }
            }
        });
        addUIListeners();
        initAddComboBox();
        initSearchTextField();
        initClearSearchButton();
        setEnabledAll(false);
    }

    public void init() {
        initClassificationComboBox();

    }

    public void setEnabledAll(boolean enable) {
        filterLabel.setEnabled(enable);
        filterTable.setEnabled(enable);
        taxaTreeTable.setEnabled(enable);
        addComboBox.setEnabled(enable);
        removeButton.setEnabled(enable);
        searchTextField.setEnabled(enable);
        clearSearchButton.setEnabled(enable);
    }

    private void initTaxaTable(int classificationId) {

        taxaTreeTable.setSelectable(true);
        taxaTreeTable.setImmediate(false);

        if(listener != null) {
            List<String> columnIds = new ArrayList<String>();
            columnIds.add(LeafNodeTaxonContainer.NAME_ID);
            taxaTreeTable.setColumnExpandRatio(LeafNodeTaxonContainer.NAME_ID, 1);
            columnIds.add(LeafNodeTaxonContainer.PB_ID);
            taxaTreeTable.setColumnWidth(LeafNodeTaxonContainer.PB_ID, 25);

            ValueChangeListener pbListener = new ValueChangeListener() {
                @Override
                public void valueChange(ValueChangeEvent event) {
                    boolean value = (Boolean) event.getProperty().getValue();
                    Notification.show("Changing Published Flag", "Implement me", Type.WARNING_MESSAGE);
                }

            };
            taxaTreeTable.addGeneratedColumn(LeafNodeTaxonContainer.PB_ID, new BooleanCheckBoxGenerator(pbListener));
            try {
                taxaTreeTable.setContainerDataSource(listener.loadTaxa(classificationId), columnIds);
            } catch (SQLException e) {
              //TODO : throw up warning dialog
                e.printStackTrace();
            }

            taxaTreeTable.addExpandListener(new ExpandListener() {

                @Override
                public void nodeExpand(ExpandEvent event) {
                    //listener.addChildren(event.getItemId());
                }

            });

            taxaTreeTable.setCellStyleGenerator(new Table.CellStyleGenerator() {

                @Override
                public String getStyle(Table source, Object itemId, Object propertyId) {
                    Property hasSynProperty = source.getItem(itemId).getItemProperty(LeafNodeTaxonContainer.HAS_SYN_ID);
                    if(hasSynProperty == null) {
                        // this is a synonym
                        return "synonym";
                    }
                    return null;
                }
            });

            // NOTE : Not really sure why we need to refresh the container here.
            // in the case of 'Table' this is not required
            listener.refresh();
            updateInViewLabel();
        }


        final Action changeAcceptedTaxonToSynonymAction = new Action("Change Accepted Taxon To Synonym");


        taxaTreeTable.addActionHandler(new Action.Handler() {
            @Override
            public Action[] getActions(final Object target, final Object sender) {
                    return new Action[] { changeAcceptedTaxonToSynonymAction };
            }

            @Override
            public void handleAction(final Action action, final Object sender,
                    final Object target) {
                Notification.show("action for Change Accepted Taxon To Synonym", "Implement me", Type.WARNING_MESSAGE);
            }
        });

    }

    private void initClassificationComboBox() {

        classificationComboBox.setNewItemsAllowed(false);
        classificationComboBox.setNullSelectionAllowed(false);
        classificationComboBox.setImmediate(true);
        classificationComboBox.setItemCaptionPropertyId("titleCache");
        classificationComboBox.setInputPrompt(SELECT_CLASSIFICATION);
        if(listener != null) {
            try {
                classificationComboBox.setContainerDataSource(listener.loadClassifications());
            } catch (SQLException e) {
                //TODO : throw up warning dialog
                e.printStackTrace();
            }
        }
    }



    private void initFilterTable() {
        filterTable.setNullSelectionAllowed(false);
        final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("filter", String.class, "");
        container.addContainerProperty("selected", Boolean.class, "");

        Item item = container.addItem(FILTER_NOT_RESOLVED);
        item.getItemProperty(PROPERTY_FILTER_ID).setValue(FILTER_NOT_RESOLVED);
        item.getItemProperty(PROPERTY_SELECTED_ID).setValue(false);

        item = container.addItem(FILTER_UNPLACED);
        item.getItemProperty(PROPERTY_FILTER_ID).setValue(FILTER_UNPLACED);
        item.getItemProperty(PROPERTY_SELECTED_ID).setValue(false);

        item = container.addItem(FILTER_UNFINISHED);
        item.getItemProperty(PROPERTY_FILTER_ID).setValue(FILTER_UNFINISHED);
        item.getItemProperty(PROPERTY_SELECTED_ID).setValue(false);

        item = container.addItem(FILTER_UNPUBLISHED);
        item.getItemProperty(PROPERTY_FILTER_ID).setValue(FILTER_UNPUBLISHED);
        item.getItemProperty(PROPERTY_SELECTED_ID).setValue(false);


        filterTable.setContainerDataSource(container);
        filterTable.setColumnHeaderMode(ColumnHeaderMode.HIDDEN);


        ValueChangeListener selectedListener = new ValueChangeListener() {

            private static final long serialVersionUID = -5551250788805117454L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                boolean value = (Boolean) event.getProperty().getValue();
                String selectedFilter = (String)filterTable.getItem(((CheckBox)event.getProperty()).getData()).getItemProperty(PROPERTY_FILTER_ID).getValue();
                if(selectedFilter != null) {
                    if(value) {
                        if(selectedFilter.equals(FILTER_UNPLACED)) {
                            listener.setUnplacedFilter();
                        }
                        if(selectedFilter.equals(FILTER_UNPUBLISHED)) {
                            listener.setUnpublishedFilter();
                        }
                    } else {
                        if(selectedFilter.equals(FILTER_UNPLACED)) {
                            listener.removeUnplacedFilter();
                        }
                        if(selectedFilter.equals(FILTER_UNPUBLISHED)) {
                            listener.removeUnpublishedFilter();
                        }
                    }
                    updateInViewLabel();
                }
            }

        };
        filterTable.addGeneratedColumn(PROPERTY_SELECTED_ID, new BooleanCheckBoxGenerator(selectedListener));

    }

    private void updateInViewLabel() {
        inViewLabel.setValue(IN_VIEW_PREFIX + String.valueOf(listener.getCurrentNoOfTaxa()) + " / " + String.valueOf(listener.getTotalNoOfTaxa()) + " taxa");
    }

    private void initSearchTextField() {
        searchTextField.setInputPrompt(FILTER_TAXA_INPUT);
    }


    private void initAddComboBox() {
        addComboBox.setNullSelectionAllowed(false);
        addComboBox.setImmediate(true);
        addComboBox.addItem(ADD_TAXON_SYNONYM);
        addComboBox.addItem("New Accepted Taxon");
        addComboBox.addItem("New Synonym");
        addComboBox.setValue(ADD_TAXON_SYNONYM);

    }

    private void initClearSearchButton() {
        //ThemeResource resource = new ThemeResource("icons/32/cancel.png");
        clearSearchButton.setIcon(FontAwesome.REFRESH);
        clearSearchButton.setCaption("");
    }

    private void addUIListeners() {
        addClassificationComboBoxListener();
        addAddComboBoxListener();
        addSearchTextFieldListener();
        addClearSearchButtonListener();
    }

    private void addClassificationComboBoxListener() {

        classificationComboBox.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = 4196786323147791606L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (classificationComboBox.getValue() != null) {
                    Object selected = classificationComboBox.getValue();
                    logger.info("selected : " + selected);
                    int classificationId = (Integer)((RowId)selected).getId()[0];
                    initTaxaTable(classificationId);
                    initFilterTable();
                    setEnabledAll(true);
                }
            }
        });
    }


    private void addAddComboBoxListener() {
        addComboBox.addValueChangeListener(new Property.ValueChangeListener() {

            private static final long serialVersionUID = -6235423275051269517L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                if (addComboBox.getValue() != null) {
                    String selected = (String)addComboBox.getValue();
                    if(!selected.equals(ADD_TAXON_SYNONYM)) {
                        Notification.show(selected, "Implement me", Type.WARNING_MESSAGE);
                        addComboBox.setValue(ADD_TAXON_SYNONYM);
                    }
                }
            }
        });
    }

    private void addSearchTextFieldListener() {
        searchTextField.addTextChangeListener(new FieldEvents.TextChangeListener() {

            private static final long serialVersionUID = -7376538870420619534L;

            @Override
            public void textChange(TextChangeEvent event) {
               listener.setNameFilter(event.getText());
               updateInViewLabel();
            }

        });

    }

    private void addClearSearchButtonListener() {
        clearSearchButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
               listener.removeNameFilter();
               searchTextField.setValue(FILTER_TAXA_INPUT);
               updateInViewLabel();
            }

        });
    }



    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.IStatusComponent#setListener(eu.etaxonomy.cdm.vaadin.view.IStatusComponent.StatusComponentListener)
     */
    @Override
    public void setListener(StatusComponentListener listener) {
        this.listener = listener;
    }


    class BooleanCheckBoxGenerator implements Table.ColumnGenerator {

        private final ValueChangeListener listener;

        public BooleanCheckBoxGenerator(ValueChangeListener listener) {
            this.listener = listener;
        }

        /**
         * Generates the cell containing an open image when boolean is true
         */
        @Override
        public Component generateCell(Table source, Object itemId, Object columnId) {
            if(source.getItem(itemId) != null) {
                Property prop = source.getItem(itemId).getItemProperty(columnId);
                if(prop == null) {
                    return null;
                }
                CheckBox cb = new CheckBox(null, prop);
                cb.addValueChangeListener(listener);
                cb.setData(itemId);
                return cb;
            } else {
                return null;
            }

        }
    }


    @AutoGenerated
    private GridLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new GridLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("340px");
        mainLayout.setHeight("840px");
        mainLayout.setMargin(true);
        mainLayout.setSpacing(true);
        mainLayout.setRows(6);

        // top-level component properties
        setWidth("340px");
        setHeight("840px");

        // classificationComboBox
        classificationComboBox = new ComboBox();
        classificationComboBox.setImmediate(false);
        classificationComboBox.setWidth("100.0%");
        classificationComboBox.setHeight("-1px");
        mainLayout.addComponent(classificationComboBox, 0, 0);

        // filterVerticalLayout
        filterVerticalLayout = buildFilterVerticalLayout();
        mainLayout.addComponent(filterVerticalLayout, 0, 1);
        mainLayout.setComponentAlignment(filterVerticalLayout, new Alignment(20));

        // addRemovehorizontalLayout
        addRemovehorizontalLayout = buildAddRemovehorizontalLayout();
        mainLayout.addComponent(addRemovehorizontalLayout, 0, 2);
        mainLayout.setComponentAlignment(addRemovehorizontalLayout, new Alignment(48));

        // searchHorizontalLayout
        searchHorizontalLayout = buildSearchHorizontalLayout();
        mainLayout.addComponent(searchHorizontalLayout, 0, 3);
        mainLayout.setComponentAlignment(searchHorizontalLayout, new Alignment(48));

        // taxaTreeTable
        taxaTreeTable = new TreeTable();
        taxaTreeTable.setImmediate(false);
        taxaTreeTable.setWidth("100.0%");
        taxaTreeTable.setHeight("534px");
        mainLayout.addComponent(taxaTreeTable, 0, 4);
        mainLayout.setComponentAlignment(taxaTreeTable, new Alignment(20));

        // inViewLabel
        inViewLabel = new Label();
        inViewLabel.setImmediate(false);
        inViewLabel.setWidth("100.0%");
        inViewLabel.setHeight("-1px");
        inViewLabel.setValue("in view : ");
        mainLayout.addComponent(inViewLabel, 0, 5);

        return mainLayout;
    }

    @AutoGenerated
    private VerticalLayout buildFilterVerticalLayout() {
        // common part: create layout
        filterVerticalLayout = new VerticalLayout();
        filterVerticalLayout.setImmediate(false);
        filterVerticalLayout.setWidth("100.0%");
        filterVerticalLayout.setHeight("-1px");
        filterVerticalLayout.setMargin(false);

        // filterLabel
        filterLabel = new Label();
        filterLabel.setImmediate(false);
        filterLabel.setWidth("100.0%");
        filterLabel.setHeight("-1px");
        filterLabel.setValue("Filter by :");
        filterVerticalLayout.addComponent(filterLabel);

        // filterTable
        filterTable = new Table();
        filterTable.setImmediate(false);
        filterTable.setWidth("100.0%");
        filterTable.setHeight("86px");
        filterVerticalLayout.addComponent(filterTable);
        filterVerticalLayout.setComponentAlignment(filterTable, new Alignment(48));

        return filterVerticalLayout;
    }

    @AutoGenerated
    private HorizontalLayout buildAddRemovehorizontalLayout() {
        // common part: create layout
        addRemovehorizontalLayout = new HorizontalLayout();
        addRemovehorizontalLayout.setImmediate(false);
        addRemovehorizontalLayout.setWidth("100.0%");
        addRemovehorizontalLayout.setHeight("-1px");
        addRemovehorizontalLayout.setMargin(false);
        addRemovehorizontalLayout.setSpacing(true);

        // addComboBox
        addComboBox = new ComboBox();
        addComboBox.setImmediate(false);
        addComboBox.setWidth("100.0%");
        addComboBox.setHeight("-1px");
        addRemovehorizontalLayout.addComponent(addComboBox);
        addRemovehorizontalLayout.setExpandRatio(addComboBox, 3.0f);

        // removeButton
        removeButton = new Button();
        removeButton.setCaption("Remove");
        removeButton.setImmediate(true);
        removeButton.setWidth("100.0%");
        removeButton.setHeight("-1px");
        addRemovehorizontalLayout.addComponent(removeButton);
        addRemovehorizontalLayout.setExpandRatio(removeButton, 2.0f);

        return addRemovehorizontalLayout;
    }

    @AutoGenerated
    private HorizontalLayout buildSearchHorizontalLayout() {
        // common part: create layout
        searchHorizontalLayout = new HorizontalLayout();
        searchHorizontalLayout.setImmediate(false);
        searchHorizontalLayout.setWidth("100.0%");
        searchHorizontalLayout.setHeight("-1px");
        searchHorizontalLayout.setMargin(false);
        searchHorizontalLayout.setSpacing(true);

        // searchTextField
        searchTextField = new TextField();
        searchTextField.setImmediate(false);
        searchTextField.setWidth("100.0%");
        searchTextField.setHeight("-1px");
        searchHorizontalLayout.addComponent(searchTextField);
        searchHorizontalLayout.setExpandRatio(searchTextField, 4.0f);
        searchHorizontalLayout.setComponentAlignment(searchTextField, new Alignment(48));

        // clearSearchButton
        clearSearchButton = new Button();
        clearSearchButton.setCaption("Button");
        clearSearchButton.setImmediate(true);
        clearSearchButton.setWidth("100.0%");
        clearSearchButton.setHeight("-1px");
        searchHorizontalLayout.addComponent(clearSearchButton);
        searchHorizontalLayout.setExpandRatio(clearSearchButton, 1.0f);
        searchHorizontalLayout.setComponentAlignment(clearSearchButton, new Alignment(48));

        return searchHorizontalLayout;
    }

}
