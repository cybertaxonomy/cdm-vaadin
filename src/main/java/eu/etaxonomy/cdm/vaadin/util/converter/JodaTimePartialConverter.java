/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;

import com.vaadin.data.util.converter.Converter;


/**
 *
 * @author a.kohlbecker
 * @since Apr 7, 2017
 *
 */
public class JodaTimePartialConverter implements Converter<String, Partial> {

    /**
     * iso8601: YYY-MM-DD
     * yyyymmddDot: dd.mm.yyyy
     *
     * @author a.kohlbecker
     * @since Apr 10, 2017
     *
     */
    public enum DateFormat {
        ISO8601,
        DAY_MONTH_YEAR_DOT
    }

    private static final long serialVersionUID = 976413549472527584L;

    DateFormat format;

    Pattern partialPatternIso8601 = Pattern.compile("^(?<year>(?:[1,2][7,8,9,0,1])?[0-9]{2})(?:-(?<month>[0-1]?[0-9])(?:-(?<day>[0-3]?[0-9]))?)?$");

    Pattern partialPatternDayMonthYearDot = Pattern.compile("^(?:(?:(?<day>[0-3]?[0-9])\\.)?(?<month>[0-1]?[0-9])\\.)?(?<year>(?:[1,2][7,8,9,0,1])?[0-9]{2})$");

    List<Pattern> patterns = Arrays.asList(new Pattern[]{partialPatternIso8601, partialPatternDayMonthYearDot});



    /**
     * @param format The format of the string representation
     */
    public JodaTimePartialConverter(DateFormat format) {
        super();
        this.format = format;
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
            for(Pattern pattern : patterns){
            Matcher m = pattern.matcher(value);
                if(m.matches()){
                    partial = makePartial(m);
                    break;
                }
            }
        }
        return partial;
    }

    /**
     * @param partial
     * @param m
     */
    private Partial makePartial(Matcher m) {

        Partial partial = new Partial();
        try {
            try {
                String year = m.group("year");
                partial = partial.with(DateTimeFieldType.year(), Integer.parseInt(year));
            } catch (IllegalArgumentException e) {
                // a valid year should be present here
                throw new ConversionException(e);
            }
            try {
                String month = m.group("month");
                partial = partial.with(DateTimeFieldType.monthOfYear(), Integer.parseInt(month));
                try {
                    String day = m.group("day");
                    partial = partial.with(DateTimeFieldType.dayOfMonth(), Integer.parseInt(day));
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
        return partial;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(Partial value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value != null){
            switch(format) {
            case ISO8601:
                return formatIso8601(value);
            case DAY_MONTH_YEAR_DOT:
                return formatYyyymmddDot(value);
            default:
                return "JodaTimePartialConverter Error: unsupported format";
           }
        }
        return "";
    }

    /**
     * @param value
     * @param sb
     */
    private String formatIso8601(Partial value) {
        StringBuffer sb = new StringBuffer();
        String glue = "-";
        try {
            sb.append(value.get(DateTimeFieldType.year()));
            try {
                String month = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.monthOfYear()))), 2, "0");
                sb.append(glue).append(month);
                try {
                    String day = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.dayOfMonth()))), 2, "0");
                    sb.append(glue).append(day);
                } catch (IllegalArgumentException e){
                    /* IGNORE */
                }
            } catch (IllegalArgumentException e){
                /* IGNORE */
            }
        } catch (IllegalArgumentException e){
            /* IGNORE */
        }
        return sb.toString();
    }

    /**
     * @param value
     * @param sb
     */
    private String formatYyyymmddDot(Partial value) {
        StringBuffer sb = new StringBuffer();
        String glue = ".";
        try {
            sb.append(StringUtils.reverse(Integer.toString(value.get(DateTimeFieldType.year()))));
            try {
                String month = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.monthOfYear()))), 2, "0");
                sb.append(glue).append(StringUtils.reverse(month));
                try {
                    String day = StringUtils.leftPad(Integer.toString((value.get(DateTimeFieldType.dayOfMonth()))), 2, "0");
                    sb.append(glue).append(StringUtils.reverse(day));
                } catch (IllegalArgumentException e){
                    /* IGNORE */
                }
            } catch (IllegalArgumentException e){
                /* IGNORE */
            }
        } catch (IllegalArgumentException e){
            /* IGNORE */
        }
        return StringUtils.reverse(sb.toString());
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
