/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.formatter;

import org.apache.commons.lang3.StringUtils;

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 *
 * FIXME move into cdmlib
 *
 * @author a.kohlbecker
 * @since Apr 26, 2017
 */
public class TimePeriodFormatter {

    private PartialFormatter partialFormatter;

    public TimePeriodFormatter(DateTimeFormat format) {
        partialFormatter = new PartialFormatter(format);
    }

    public String print(TimePeriod timePeriod) {

        if ( StringUtils.isNotBlank(timePeriod.getFreeText())){
           return timePeriod.getFreeText();
        }else{
            StringBuffer sb = new StringBuffer();
            if (timePeriod.getStart() != null && timePeriod.getStart().getFields().length > 0) {
                sb.append(partialFormatter.print(timePeriod.getStart()));
            }
            if (timePeriod.getEnd() != null && timePeriod.getEnd().getFields().length > 0) {
                if (sb.length() > 0) {
                    sb.append(" - ");
                }
                sb.append(partialFormatter.print(timePeriod.getEnd()));
            }
            return sb.toString();
        }
    }
}