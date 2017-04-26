/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Partial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 *
 * FIXME move into cdmlib
 *
 * @author a.kohlbecker
 * @since Apr 26, 2017
 *
 */
public class TimePeriodFormatter {

    public enum Format {
        /**
         * yyyy-mm-dd or yyyy-mm or yyyy
         */
        ISO8601
    }

    private Format format;

    public TimePeriodFormatter(Format format) {
        this.format = format;
    }

    public String print(TimePeriod timePeriod) {

        if ( StringUtils.isNotBlank(timePeriod.getFreeText())){
           return timePeriod.getFreeText();
        }else{
            switch (format) {
            case ISO8601:
            default:
                return printISO8601(timePeriod);
            }
        }
    }

    /**
     * @param datePublished
     */
    private String printISO8601(TimePeriod datePublished) {
        StringBuffer sb = new StringBuffer();
        if (datePublished.getStart() != null) {
            sb.append(datePublished.getStart().toString(determineISO860Formatter(datePublished.getStart())));
        }
        if (datePublished.getEnd() != null) {
            if (sb.length() > 0) {
                sb.append('-');
                sb.append(datePublished.getEnd().toString(determineISO860Formatter(datePublished.getEnd())));
            }
        }
        return sb.toString();
    }

    /**
     * @param partial
     * @return
     */
    private DateTimeFormatter determineISO860Formatter(Partial partial) {
        if (partial.isSupported(TimePeriod.DAY_TYPE)) {
            return ISODateTimeFormat.yearMonthDay();
        }
        if (partial.isSupported(TimePeriod.MONTH_TYPE)) {
            return ISODateTimeFormat.yearMonth();
        }
        return ISODateTimeFormat.year();

    }

}
