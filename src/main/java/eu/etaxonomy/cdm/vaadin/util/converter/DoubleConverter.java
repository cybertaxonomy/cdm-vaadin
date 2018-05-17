/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.converter.Converter;

/**
 * @author a.kohlbecker
 * @since Mar 28, 2018
 *
 */
public class DoubleConverter implements Converter<String, Double> {

    private static final long serialVersionUID = -8799792699785931554L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(StringUtils.isBlank(value)){
            return null;
        }
        if(locale == null){
            locale = Locale.getDefault();
        }
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        String separator = symbols.getDecimalSeparator() + "";
        value = value.replaceAll("[.,;]", separator);
        try {
            return nf.parse(value).doubleValue();
        } catch (ParseException e){
            throw new ConversionException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(value == null){
            return null;
        }
        if(locale == null){
            locale = Locale.getDefault();
        }
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
