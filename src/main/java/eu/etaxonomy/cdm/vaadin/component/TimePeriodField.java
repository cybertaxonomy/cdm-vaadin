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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

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

        parseField = new TextField();
        parseField.setWidth(100, Unit.PERCENTAGE);
        parseField.setInputPrompt("This field will parse the entered time period");
        parseField.addTextChangeListener(e -> parseInput(e));

        PartialDateField startDate = new PartialDateField("Start");
        PartialDateField endDate = new PartialDateField("End");
        freeText = new TextField("FreeText");
        freeText.setWidth(100, Unit.PERCENTAGE);

        fieldGroup.bind(startDate, "start");
        fieldGroup.bind(endDate, "end");
        fieldGroup.bind(freeText, "freeText");

        toLabel = new Label("\u2014"); // EM DASH : 0x2014

        int row = 0;
        grid.addComponent(parseField, 0, row, 2, row);
        row++;
        grid.addComponent(startDate, 0, row);
        grid.addComponent(toLabel, 1, row);
        grid.setComponentAlignment(toLabel, Alignment.BOTTOM_CENTER);
        grid.addComponent(endDate, 2, row);
        row++;
        grid.addComponent(freeText, 0, row, 2, row);

        grid.iterator().forEachRemaining(c -> c.setStyleName(getStyleName()));

        CssLayout marginwrapper = new CssLayout();
        marginwrapper.setStyleName("margin-wrapper");
        marginwrapper.addComponent(grid);

        applyDefaultStyles();

        return marginwrapper;
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
        }

    }

    @Override
    protected void setInternalValue(TimePeriod newValue) {
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(new BeanItem<TimePeriod>(newValue));
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        grid.iterator().forEachRemaining(c -> c.setStyleName(style));
        applyDefaultStyles();
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        grid.iterator().forEachRemaining(c -> c.addStyleName(style));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends TimePeriod> getType() {
        return TimePeriod.class;
    }



}
