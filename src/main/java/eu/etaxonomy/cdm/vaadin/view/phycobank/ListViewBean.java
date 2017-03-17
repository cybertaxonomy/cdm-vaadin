/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.phycobank;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.lazyloadwrapper.LazyLoadWrapper;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.component.phycobank.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.ListPresenter;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.util.JodaDateTimeConverter;
import eu.etaxonomy.cdm.vaadin.util.UrlStringConverter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=ListViewBean.NAME)
public class ListViewBean extends AbstractView<ListPresenter> implements ListView, View {

    private static final long serialVersionUID = 3543300933072824713L;

    public static final String NAME = "list";

    private VerticalLayout layout;

    private Grid grid;

    private Panel panel;

    public ListViewBean() {
        layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setSizeFull();

        Label title = new Label("Registrations");
        title.setStyleName(ValoTheme.LABEL_HUGE);
        title.setWidthUndefined();
        layout.addComponent(title);
        layout.setComponentAlignment(title, Alignment.TOP_CENTER);

        Label hint = new Label("This is the list of all your registrations in progress.");
        hint.setWidthUndefined();
        layout.addComponent(hint);
        layout.setComponentAlignment(hint, Alignment.MIDDLE_CENTER);

//        grid = buildGrid();
//        layout.addComponent(grid);
//        layout.setExpandRatio(grid, 1);

        buildPanel();

        setCompositionRoot(layout);
        this.setSizeFull();
    }

    /**
     *
     */
    private void buildPanel() {
        panel = new Panel();
        panel.setSizeFull();
        panel.setId("registration-list");
        layout.addComponent(panel);
        layout.setExpandRatio(panel, 1);
    }

    private Grid buildGrid() {
        Grid grid = new Grid();
        grid.setSizeFull();
        grid.setEditorEnabled(false);
        grid.setId("registration-list");
        grid.setCellStyleGenerator(cellRef -> cellRef.getPropertyId().toString());
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setHeightMode(HeightMode.CSS);
        // add status as class  attribute to the rows to allow styling with css
        grid.setRowStyleGenerator(rowRef -> {return "status-" + rowRef.getItem().getItemProperty("status").getValue().toString();});
        return grid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        getPresenter().onViewEnter();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Autowired
    protected void injectPresenter(ListPresenter presenter) {
        setPresenter(presenter);

    }

    static final String[] visiblCols = new String[]{"status, summary", "registrationId"};
    /**
     * {@inheritDoc}
     */
    @Override
    public void populateTable(Collection<RegistrationDTO> registrations) {

        BeanContainer<String, RegistrationDTO> registrationItems = new BeanContainer<String, RegistrationDTO>(RegistrationDTO.class);
        registrationItems.setBeanIdProperty("specificIdentifier");
        registrationItems.addAll(registrations);

        grid.setContainerDataSource(buildGeneratedProperties(registrationItems));

        grid.removeAllColumns();

        Column typeColumn = grid.addColumn("registrationType");
        typeColumn.setRenderer(new HtmlRenderer(), new RegistrationTypeConverter());
        typeColumn.setHeaderCaption("");

        Column statusColumn = grid.addColumn("status");

        Column citationColumn = grid.addColumn("citation");
        citationColumn.setHeaderCaption("Publication");


        Column summaryColumn = grid.addColumn("summary");

        Column regidColumn = grid.addColumn("registrationId");
        regidColumn.setHeaderCaption("Id");
        regidColumn.setRenderer(new HtmlRenderer(), new UrlStringConverter("http://pyhcobank.org/"));

        Column createdColumn = grid.addColumn("created");
        createdColumn.setRenderer(new DateRenderer(), new JodaDateTimeConverter());
        createdColumn.setHidable(true);
        createdColumn.setHidden(true);

        Column regDateColumn = grid.addColumn("registrationDate");
        // regDateColumn.setHeaderCaption("Date");
        regDateColumn.setRenderer(new DateRenderer(), new JodaDateTimeConverter());
        regDateColumn.setHidable(true);
        regDateColumn.setHidden(true);


        Column buttonColumn = grid.addColumn("operation");
        buttonColumn.setRenderer(new ButtonRenderer(e -> eventBus.publishEvent(new NavigationEvent(
                RegistrationWorkflowViewBean.NAME,
                RegistrationWorkflowViewBean.ACTION_EDIT,
                e.getItemId().toString()
                ))));
        buttonColumn.setSortable(false);

        grid.setFrozenColumnCount(1);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populateList(Collection<RegistrationDTO> registrations) {
        VerticalLayout list = new VerticalLayout();
        list.setMargin(new MarginInfo(false, true));
        list.setSpacing(true);
        for(RegistrationDTO regDto : registrations) {

            Component lazyItem = new LazyLoadWrapper(new RegistrationItem(regDto, this));
            list.addComponent(lazyItem);
//            if(list.getComponentCount() > 10){
//                break;
//            }
        }
        panel.setContent(list);

    }

    /**
     * @param registrationItems
     * @return
     */
    private GeneratedPropertyContainer buildGeneratedProperties(
            BeanContainer<String, RegistrationDTO> registrationItems) {
        GeneratedPropertyContainer gpContainer = new GeneratedPropertyContainer(registrationItems);

        gpContainer.addGeneratedProperty("operation", new PropertyValueGenerator<String>() {

            @Override
            public String getValue(Item item, Object itemId, Object propertyId) {
                return "Open";
            }

            @Override
            public Class<String> getType() {
                return String.class;
            }
          });

        return gpContainer;
    }



}
