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
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.Column;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.renderers.ButtonRenderer;
import com.vaadin.ui.renderers.DateRenderer;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.presenter.phycobank.ListPresenter;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;
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

    /**
     * @author a.kohlbecker
     * @since Mar 7, 2017
     *
     */
    protected final class RegistrationTypeConverter implements Converter<String, RegistrationType> {
        @Override
        public RegistrationType convertToModel(String value, Class<? extends RegistrationType> targetType,
                Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
            // not implemented
            return null;
        }

        @Override
        public String convertToPresentation(RegistrationType value, Class<? extends String> targetType,
                Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
            if(value.equals(RegistrationType.name)) {
                return FontAwesome.TAG.getHtml();
            }
            if(value.equals(RegistrationType.typification)) {
                return FontAwesome.TAGS.getHtml();
            }
            return FontAwesome.WARNING.getHtml();
        }

        @Override
        public Class<RegistrationType> getModelType() {
            return RegistrationType.class;
        }

        @Override
        public Class<String> getPresentationType() {
            return String.class;
        }
    }
    private static final long serialVersionUID = 3543300933072824713L;

    public static final String NAME = "list";

    private VerticalLayout layout;

    private Grid grid;

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

        grid = buildGrid();
        layout.addComponent(grid);
        layout.setExpandRatio(grid, 1);

        setCompositionRoot(layout);
        this.setSizeFull();
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
        registrationItems.setBeanIdProperty("internalRegId");
        registrationItems.addAll(registrations);

        grid.setContainerDataSource(buildGeneratedProperties(registrationItems));

        grid.removeAllColumns();

        Column typeColumn = grid.addColumn("registrationType");
        typeColumn.setRenderer(new HtmlRenderer(), new RegistrationTypeConverter());
        typeColumn.setHeaderCaption("");
        typeColumn.setExpandRatio(0);

        Column statusColumn = grid.addColumn("status");
        statusColumn.setExpandRatio(0);

        Column summaryColumn = grid.addColumn("summary");
        summaryColumn.setExpandRatio(1);

        Column regidColumn = grid.addColumn("registrationId");
        regidColumn.setHeaderCaption("Id");
        regidColumn.setRenderer(new HtmlRenderer(), new UrlStringConverter());

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
