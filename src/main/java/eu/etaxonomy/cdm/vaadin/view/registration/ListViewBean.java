/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;

import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.util.converter.JodaDateTimeConverter;
import eu.etaxonomy.cdm.vaadin.util.converter.UrlStringConverter;
import eu.etaxonomy.cdm.vaadin.view.AbstractPageView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=ListViewBean.NAME)
public class ListViewBean extends AbstractPageView<ListPresenter> implements ListView, View {

    private static final long serialVersionUID = 3543300933072824713L;

    public static final String NAME = "list";

    private CssLayout listContainer;

    private Grid grid;

    private CssLayout toolBar;

    public ListViewBean() {

        super();

        toolBar = new CssLayout();
        toolBar.setWidth(100, Unit.PERCENTAGE);
        toolBar.addComponent(new Button("As grid", e -> toggleListType(e)));
        getLayout().addComponent(toolBar);

        buildList();
        buildGrid();

        showList();
    }

    @Override
    protected String getHeaderText() {
        return "Registrations";
    }

    @Override
    protected String getSubHeaderText() {
        return "This is the list of all your registrations in progress.";
    }

    private void buildList() {
        listContainer = new CssLayout();
        listContainer.setId("registration-list");
        listContainer.setWidth(100, Unit.PERCENTAGE);
    }

    private void buildGrid() {
        grid = new Grid();
        grid.setSizeFull();
        grid.setEditorEnabled(false);
        grid.setId("registration-list");
        grid.setCellStyleGenerator(cellRef -> cellRef.getPropertyId().toString());
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setHeightMode(HeightMode.CSS);
        // add status as class  attribute to the rows to allow styling with css
        grid.setRowStyleGenerator(rowRef -> {return "status-" + rowRef.getItem().getItemProperty("status").getValue().toString();});
    }

    /**
     * @param e
     * @return
     */
    private Object toggleListType(ClickEvent e) {
        Button button = e.getButton();
        if(button.getCaption().equals("As grid")){
            button.setCaption("As list");
            showGrid();
        } else {
            button.setCaption("As grid");
            showList();
        }
        return null;
    }

    private void showList() {
        if(grid != null){
            getLayout().removeComponent(grid);
        }
        getLayout().addComponent(listContainer);
    }

    private void showGrid() {
        if(listContainer != null){
            getLayout().removeComponent(listContainer);
        }
        getLayout().addComponent(grid);
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

    @Override
    public void populate(Collection<RegistrationDTO> registrations) {

        registrations = new ArrayList<RegistrationDTO>(registrations).subList(0, 10);

        populateGrid(registrations);
        populateList(registrations);
    }


    public void populateGrid(Collection<RegistrationDTO> registrations) {

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

        Column regidColumn = grid.addColumn("identifier");
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


    public void populateList(Collection<RegistrationDTO> registrations) {

        for(RegistrationDTO regDto : registrations) {
            Component item = new RegistrationItem(regDto, this);
            item.setWidth(100, Unit.PERCENTAGE);
            listContainer.addComponent(item);
        }
    }

    @Override
    public void openDetailsPopup(String caption, List<String> messages){
        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"details-popup-content\">");
        messages.forEach(s -> sb.append(s).append("</br>"));
        sb.append("</div>");
        new Notification(caption, sb.toString(), Notification.Type.HUMANIZED_MESSAGE, true).show(Page.getCurrent());
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
