/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;

import com.vaadin.data.util.converter.Converter;


/**
 * @author a.kohlbecker
 * @since Apr 7, 2017
 *
 */
public class JodaTimePartialConverter implements Converter<String, Partial> {

    private static final long serialVersionUID = 976413549472527584L;

    static final String GLUE = "-";

    Pattern partialPattern = Pattern.compile("^(?<year>(?:[1,2][7,8,9,0,1])?[0-9]{2})(?:-(?<month>[0-1]?[0-9])(?:-(?<day>[0-3]?[0-9]))?)?$");

    /**
     *
     */
    public JodaTimePartialConverter() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Partial convertToModel(String value, Class<? extends Partial> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        Partial partial = null;
        if(value != null){
            partial = new Partial();
            Matcher m = partialPattern.matcher(value);
            if(m.matches()){
                try {
                    try {
                        partial.with(DateTimeFieldType.year(), Integer.parseInt(m.group("year")));
                    } catch (IllegalArgumentException e) {
                        // a valid year should be present here
                        throw new ConversionException(e);
                    }
                    try {
                        partial.with(DateTimeFieldType.monthOfYear(), Integer.parseInt(m.group("month")));
                        try {
                            partial.with(DateTimeFieldType.dayOfMonth(), Integer.parseInt(m.group("day")));
                        } catch (IllegalArgumentException e) {
                            /* IGNORE days are not required */
                        }
                    } catch (IllegalArgumentException e) {
                        /* IGNORE months are not required */
                    }
                } catch (NumberFormatException ne) {
                    // all numbers should be parsable, this is guaranteed by the partialPattern
                    throw new ConversionException(ne);
                }
            }
        }
        return partial;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(Partial value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        StringBuffer sb = new StringBuffer();
        if(value != null){
            try {
                sb.append(value.get(DateTimeFieldType.year()));
                try {
                    String month = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.monthOfYear()))), 2, "0");
                    sb.append(GLUE).append(month);
                    try {
                        String day = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.dayOfMonth()))), 2, "0");
                        sb.append(GLUE).append(day);
                    } catch (IllegalArgumentException e){
                        /* IGNORE */
                    }
                } catch (IllegalArgumentException e){
                    /* IGNORE */
                }
            } catch (IllegalArgumentException e){
                /* IGNORE */
            }
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Partial> getModelType() {
        return Partial.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
