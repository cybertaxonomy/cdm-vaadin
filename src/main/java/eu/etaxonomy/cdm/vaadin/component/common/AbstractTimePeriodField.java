/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import java.util.HashSet;
import java.util.Set;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.strategy.parser.TimePeriodParser;
import eu.etaxonomy.cdm.vaadin.component.PartialDateField;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;
import eu.etaxonomy.cdm.vaadin.util.formatter.DateTimeFormat;
import eu.etaxonomy.cdm.vaadin.util.formatter.TimePeriodFormatter;

/**
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
public abstract class AbstractTimePeriodField<T extends TimePeriod> extends CustomField<T> {

    private static final long serialVersionUID = -7377778547595966252L;

    private static final String PRIMARY_STYLE = "v-time-period-field";

    private BeanFieldGroup<TimePeriod> fieldGroup = new BeanFieldGroup<>(TimePeriod.class);

    TextField parseField = null;

    TextField freeText = null;

    Label toLabel = null;

    GridLayout detailsViewGrid = new GridLayout(3, 4);

    CssLayout detailsView = new CssLayout();

    //TODO implement custom button textfield which does not require a gridLayout
    GridLayout buttonTextField = new GridLayout(2, 1);
    GridLayout simpleView = new GridLayout(2, 1);

    TextField cacheField = new TextFieldNFix();

    Set<Component> styledComponents = new HashSet<>();

    private TimePeriodFormatter timePeriodFormatter = new TimePeriodFormatter(DateTimeFormat.ISO8601_DATE);

    /**
     *
     */
    public AbstractTimePeriodField() {
        super();

    }

    /**
     * @param string
     */
    public AbstractTimePeriodField(String string) {
        this();
        setCaption(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {

        super.setPrimaryStyleName(PRIMARY_STYLE);

        CssLayout root = new CssLayout();

        initSimpleView();
        initDetailsView();


        detailsView.setWidth(100, Unit.PERCENTAGE);
        simpleView.setWidth(100, Unit.PERCENTAGE);
        root.addComponent(simpleView);
        root.addComponent(detailsView);

        applyDefaultStyles();

        showSimple();

        root.setWidth(100, Unit.PERCENTAGE);

        // fixed size to avoid layout and component expansion problems in the nested grid layouts
        this.setWidth(300, Unit.PIXELS);

        return root;
    }

    /**
     *
     */
    private void initSimpleView() {

        Button showDetailsButton = new Button(FontAwesome.CALENDAR);
        showDetailsButton.addClickListener(e -> showDetails());
        cacheField.setWidth(100, Unit.PERCENTAGE);
        // showDetailsButton.setWidth(100, Unit.PERCENTAGE);

        simpleView.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        simpleView.addComponent(showDetailsButton, 0, 0);
        simpleView.addComponent(cacheField, 1, 0);
        simpleView.setColumnExpandRatio(1, 0.9f);
    }

    /**
     *
     */
    private void initDetailsView() {

        parseField = new TextFieldNFix();
        // parseField.setWidth(100, Unit.PERCENTAGE);
        parseField.setInputPrompt("This field will parse the entered time period");
        parseField.addTextChangeListener(e -> parseInput(e));
        parseField.setWidth(100, Unit.PERCENTAGE);

        Button closeDetailsButton = new Button(FontAwesome.CLOSE);
        closeDetailsButton.addClickListener(e -> {
            try {
                fieldGroup.commit();
            } catch (CommitException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            updateCacheField();
            showSimple();
        });

        buttonTextField.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        buttonTextField.setWidth(100, Unit.PERCENTAGE);
        buttonTextField.addComponent(closeDetailsButton, 0, 0);
        buttonTextField.addComponent(parseField, 1, 0);
        buttonTextField.setColumnExpandRatio(1, 1.0f);

        PartialDateField startDate = new PartialDateField("Start");
        startDate.setInputPrompt("dd.mm.yyyy");
        PartialDateField endDate = new PartialDateField("End");
        endDate.setInputPrompt("dd.mm.yyyy");
        freeText = new TextFieldNFix("FreeText");
        freeText.setWidth(100, Unit.PERCENTAGE);

        fieldGroup.bind(startDate, "start");
        fieldGroup.bind(endDate, "end");
        fieldGroup.bind(freeText, "freeText");

        toLabel = new Label("\u2014"); // EM DASH : 0x2014
        startDate.setWidth(100, Unit.PERCENTAGE);
        endDate.setWidth(100, Unit.PERCENTAGE);
        toLabel.setWidthUndefined();

        int row = 0;
        detailsViewGrid.addComponent(buttonTextField, 0, row, 2, row);
        row++;
        detailsViewGrid.addComponent(startDate, 0, row, 0, row);
        detailsViewGrid.addComponent(toLabel, 1, row);
        detailsViewGrid.setComponentAlignment(toLabel, Alignment.BOTTOM_CENTER);
        detailsViewGrid.addComponent(endDate, 2, row, 2, row);
        row++;
        detailsViewGrid.addComponent(freeText, 0, row, 2, row);
        detailsViewGrid.setWidth(100, Unit.PERCENTAGE);
        detailsViewGrid.setColumnExpandRatio(0,  5);
        detailsViewGrid.setColumnExpandRatio(1,  1);
        detailsViewGrid.setColumnExpandRatio(2,  5);


        // apply the style of the container to all child components. E.g. make all tiny
        addStyleName((getStyleName()));

        detailsView.setStyleName("margin-wrapper");
        detailsView.addComponent(detailsViewGrid);

    }


    /**
     * @return
     */
    private void showSimple() {
        detailsView.setVisible(false);
        simpleView.setVisible(true);
    }

    /**
     * @return
     */
    private void showDetails() {
        detailsView.setVisible(true);
        simpleView.setVisible(false);
    }

    /**
     * @param e
     * @return
     */
    private void parseInput(TextChangeEvent e) {
        if(!e.getText().isEmpty()){
            TimePeriod parsedPeriod = TimePeriodParser.parseString(e.getText());
            fieldGroup.setItemDataSource(new BeanItem<TimePeriod>(parsedPeriod));
        }
    }

    /**
     *
     */
    private void applyDefaultStyles() {
        if(parseField != null) {
            parseField.addStyleName(RegistrationStyles.HELPER_FIELD);
            toLabel.addStyleName("to-label");
            buttonTextField.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        }

    }

    @Override
    protected void setInternalValue(T newValue) {
        if(newValue == null){
            newValue = newModelInstance();
        }
        super.setInternalValue(newValue);
            fieldGroup.setItemDataSource(new BeanItem<TimePeriod>(newValue));
        updateCacheField();
    }

    /**
     * @return
     */
    protected abstract T newModelInstance();

    /**
     * @param newValue
     */
    private void updateCacheField() {
        TimePeriod newValue = fieldGroup.getItemDataSource().getBean();
        cacheField.setReadOnly(false);
        cacheField.setValue(timePeriodFormatter.print(newValue));
        cacheField.setReadOnly(true);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        detailsViewGrid.iterator().forEachRemaining(c -> c.setStyleName(style));
        buttonTextField.iterator().forEachRemaining(c -> c.setStyleName(style));
        simpleView.iterator().forEachRemaining(c -> c.setStyleName(style));
        applyDefaultStyles();
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        detailsViewGrid.iterator().forEachRemaining(c -> c.addStyleName(style));
        simpleView.iterator().forEachRemaining(c -> {
            c.addStyleName(style);
        });

        buttonTextField.iterator().forEachRemaining(c -> c.addStyleName(style));
    }

    @Override
    public void commit() throws SourceException, InvalidValueException {
        super.commit();
        try {
            fieldGroup.commit();
        } catch (CommitException e) {
            throw new RuntimeException(e);
        }
    }
}