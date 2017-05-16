/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.formatter;

import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 * @author a.kohlbecker
 * @since Apr 28, 2017
 *
 */
public class PartialFormatter {

    private DateTimeFormat format;

    private static final DateTimeFormatter dmyDotDayMonthYear =  org.joda.time.format.DateTimeFormat.forPattern("dd.MM.y");

    private static final DateTimeFormatter dmyDotMonthYear =  org.joda.time.format.DateTimeFormat.forPattern("MM.y");

    private static final DateTimeFormatter dmyDotYear =  org.joda.time.format.DateTimeFormat.forPattern("y");

    public PartialFormatter(DateTimeFormat format) {
        this.format = format;
    }

    public String print(Partial partial) {
        switch (format) {
            case DMY_DOT:
                return partial.toString(determine_DMY_DOT_Formatter(partial));
            case ISO8601_DATE:
            default:
                return partial.toString(determine_ISO860_Formatter(partial));
        }
    }

    /**
     * @param partial
     * @return
     */
    private DateTimeFormatter determine_ISO860_Formatter(Partial partial) {
        if (partial.isSupported(TimePeriod.DAY_TYPE)) {
            return ISODateTimeFormat.yearMonthDay();
        }
        if (partial.isSupported(TimePeriod.MONTH_TYPE)) {
            return ISODateTimeFormat.yearMonth();
        }
        return ISODateTimeFormat.year();

    }

    /**
     * @param partial
     * @return
     */
    private DateTimeFormatter determine_DMY_DOT_Formatter(Partial partial) {
        if (partial.isSupported(TimePeriod.DAY_TYPE)) {
            return dmyDotDayMonthYear;
        }
        if (partial.isSupported(TimePeriod.MONTH_TYPE)) {
            return dmyDotMonthYear;
        }
        return dmyDotYear;

    }

}
