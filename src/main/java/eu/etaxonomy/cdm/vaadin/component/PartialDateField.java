/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.data.Property;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.vaadin.util.converter.JodaTimePartialConverter;

/**
 * The PartialDateField is just a simple TextField by now but will evolve to
 * a more user friendly widget with either selectors for year, month, day
 * or it will use calendar widget like the DateField which allows selecting only
 * the year or year + month.
 *
 * @author a.kohlbecker
 * @since Apr 7, 2017
 *
 */
public class PartialDateField extends TextField {

    JodaTimePartialConverter.DateFormat format = JodaTimePartialConverter.DateFormat.DAY_MONTH_YEAR_DOT;

    /**
     *
     */
    public PartialDateField() {
        super();
        setConverter(new JodaTimePartialConverter(format));
    }

    /**
     * @param dataSource
     */
    public PartialDateField(Property dataSource) {
        super(dataSource);
        setConverter(new JodaTimePartialConverter(format));
    }

    /**
     * @param caption
     * @param dataSource
     */
    public PartialDateField(String caption, Property dataSource) {
        super(caption, dataSource);
        setConverter(new JodaTimePartialConverter(format));
    }

    /**
     * @param caption
     * @param value
     */
    public PartialDateField(String caption, String value) {
        super(caption, value);
        setConverter(new JodaTimePartialConverter(format));
    }

    /**
     * @param caption
     */
    public PartialDateField(String caption) {
        super(caption);
        setConverter(new JodaTimePartialConverter(format));
    }



}
