/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
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
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;

/**
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
public class TimePeriodField extends CustomField<TimePeriod> {

    private static final long serialVersionUID = -7377778547595966252L;

    private static final String PRIMARY_STYLE = "v-time-period-field";

    private BeanFieldGroup<TimePeriod> fieldGroup = new BeanFieldGroup<>(TimePeriod.class);

    TextField parseField = null;

    TextField freeText = null;

    Label toLabel = null;

    GridLayout grid = new GridLayout(3, 3);

    CssLayout detailsView = new CssLayout();
    CssLayout buttonTextField = new CssLayout();

    CssLayout simpleView = new CssLayout();

    TextField cacheField = new TextField();

    /**
     *
     */
    public TimePeriodField() {
        super();
    }

    /**
     * @param string
     */
    public TimePeriodField(String string) {
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
        root.addComponent(simpleView);

        initDetailsView();
        root.addComponent(detailsView);

        applyDefaultStyles();

        showSimple();

        return root;
    }

    /**
     *
     */
    private void initSimpleView() {

        cacheField.setWidth(100, Unit.PERCENTAGE);
        Button showDetailsButton = new Button(FontAwesome.CALENDAR);
        showDetailsButton.addClickListener(e -> showDetails());
        simpleView.addComponent(showDetailsButton);
        simpleView.addComponent(cacheField);
        simpleView.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    }

    /**
     *
     */
    private void initDetailsView() {

        parseField = new TextField();
        parseField.setWidth(100, Unit.PERCENTAGE);
        parseField.setInputPrompt("This field will parse the entered time period");
        parseField.addTextChangeListener(e -> parseInput(e));

        Button closeDetailsButton = new Button(FontAwesome.CLOSE);
        closeDetailsButton.addClickListener(e -> showSimple());

        buttonTextField.addComponent(closeDetailsButton);
        buttonTextField.addComponent(parseField);

        PartialDateField startDate = new PartialDateField("Start");
        startDate.setInputPrompt("dd.mm.yyy");
        PartialDateField endDate = new PartialDateField("End");
        endDate.setInputPrompt("dd.mm.yyy");
        freeText = new TextField("FreeText");
        freeText.setWidth(100, Unit.PERCENTAGE);

        fieldGroup.bind(startDate, "start");
        fieldGroup.bind(endDate, "end");
        fieldGroup.bind(freeText, "freeText");

        toLabel = new Label("\u2014"); // EM DASH : 0x2014

        int row = 0;
        grid.addComponent(buttonTextField, 0, row, 2, row);
        row++;
        grid.addComponent(startDate, 0, row);
        grid.addComponent(toLabel, 1, row);
        grid.setComponentAlignment(toLabel, Alignment.BOTTOM_CENTER);
        grid.addComponent(endDate, 2, row);
        row++;
        grid.addComponent(freeText, 0, row, 2, row);

        grid.iterator().forEachRemaining(c -> c.setStyleName(getStyleName()));

        detailsView.setStyleName("margin-wrapper");
        detailsView.addComponent(grid);
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
    protected void setInternalValue(TimePeriod newValue) {
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(new BeanItem<TimePeriod>(newValue));

        cacheField.setReadOnly(false);
        cacheField.setValue(newValue.toString());
        cacheField.setReadOnly(true);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        grid.iterator().forEachRemaining(c -> c.setStyleName(style));
        buttonTextField.iterator().forEachRemaining(c -> c.setStyleName(style));
        simpleView.iterator().forEachRemaining(c -> c.setStyleName(style));
        applyDefaultStyles();
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        grid.iterator().forEachRemaining(c -> c.addStyleName(style));
        simpleView.iterator().forEachRemaining(c -> c.addStyleName(style));
        buttonTextField.iterator().forEachRemaining(c -> c.addStyleName(style));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends TimePeriod> getType() {
        return TimePeriod.class;
    }



}
