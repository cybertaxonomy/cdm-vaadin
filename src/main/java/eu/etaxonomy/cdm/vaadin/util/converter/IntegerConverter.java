/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.converter.Converter;

import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;

/**
 * @author a.kohlbecker
 * @since Mar 28, 2018
 *
 */
public class IntegerConverter implements Converter<String, Integer> {

    private static final long serialVersionUID = -8799792699785931554L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(StringUtils.isBlank(value)){
            return null;
        }
        NumberFormat nf = numberFormat(locale);
        try {
            return nf.parse(value).intValue();
        } catch (ParseException e){
            throw new ConversionException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(value == null){
            return null;
        }
        NumberFormat nf = numberFormat(locale);
        return nf.format(value);
    }

    /**
     * @param locale
     * @return
     */
    public NumberFormat numberFormat(Locale locale) {
        NumberFormat nf;
        if(RegistrationUIDefaults.NUMBER_FORMAT_OVERRIDE != null){
            nf = RegistrationUIDefaults.NUMBER_FORMAT_OVERRIDE;
        } else {
            if(locale == null){
                locale = Locale.getDefault();
            }
            nf = NumberFormat.getInstance(locale);
        }
        return nf;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<Integer> getModelType() {
        return Integer.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

}
