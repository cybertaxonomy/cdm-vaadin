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
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.vaadin.util.converter.JodaDateTimeConverter;

/**
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
public class TimePeriodField extends CustomField<TimePeriod> {

    private static final long serialVersionUID = -7377778547595966252L;

    private static final String PRIMARY_STYLE = "v-time-period-field";

    private BeanFieldGroup<TimePeriod> fieldGroup = new BeanFieldGroup<>(TimePeriod.class);

    GridLayout grid = new GridLayout(4, 2);

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

        // better use https://vaadin.com/directory#!addon/tuning-datefield ???
        TuningDateField startDateField = new TuningDateField();
        PopupDateField startDate = new PopupDateField("Start");
        startDate.setConverter(new JodaDateTimeConverter());
        PopupDateField endDate = new PopupDateField("End");
        endDate.setConverter(new JodaDateTimeConverter());
        TextField freeText = new TextField("FreeText");
        freeText.setWidth(100, Unit.PERCENTAGE);

        fieldGroup.bind(startDate, "start");
        fieldGroup.bind(endDate, "end");
        fieldGroup.bind(freeText, "freeText");

        Label dashLabel = new Label("-");

        grid.addComponent(startDate, 0, 0);
        grid.addComponent(dashLabel);
        grid.setComponentAlignment(dashLabel, Alignment.BOTTOM_CENTER);
        grid.addComponent(endDate, 2, 0);
        grid.addComponent(freeText, 0, 1, 2, 1);

        grid.iterator().forEachRemaining(c -> c.setStyleName(getStyleName()));

        CssLayout marginwrapper = new CssLayout();
        marginwrapper.setStyleName("margin-wrapper");
        marginwrapper.addComponent(grid);

        return marginwrapper;
    }



    @Override
    protected void setInternalValue(TimePeriod newValue) {
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(newValue);
    }

    @Override
    public void setStyleName(String style) {
        super.setStyleName(style);
        grid.iterator().forEachRemaining(c -> c.setStyleName(style));
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
