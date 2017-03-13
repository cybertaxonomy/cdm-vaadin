/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;

import com.vaadin.data.util.converter.Converter;

/**
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public final class JodaDateTimeConverter implements Converter<Date, DateTime> {
    @Override
    public DateTime convertToModel(Date value, Class<? extends DateTime> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        DateTime dateTime = null;
        if(value != null) {
            try {
                dateTime = new DateTime(value);
            } catch (IllegalArgumentException e) {
                throw new ConversionException(e);
            }
        }
        return dateTime;
    }

    @Override
    public Date convertToPresentation(DateTime value, Class<? extends Date> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        Date date = null;
        if(value != null){
            date = value.toDate();
        }
        return date;
    }

    @Override
    public Class<DateTime> getModelType() {
        return DateTime.class;
    }

    @Override
    public Class<Date> getPresentationType() {
        return Date.class;
    }
}