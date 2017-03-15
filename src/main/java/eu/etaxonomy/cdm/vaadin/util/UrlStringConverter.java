/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Creates a link from the values in the column.
 *
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public final class UrlStringConverter implements Converter<String, String> {

    private String regex = null;
    private String replacement = "";

    public UrlStringConverter() {

    }

    /**
     * @param regex Regular expression for creating an alternative label by string replacement on the value.
     *  This is optional. No string replacement will be done when this is <code>null</code>.
     */
    public UrlStringConverter(String regex) {
        this(regex, null);
    }

    /**
     * @param regex Regular expression for creating an alternative label by string replacement on the value.
     *  This is optional. No string replacement will be done when this is <code>null</code>.
     * @param replacement The replacement to be used with the <code>regex</code>.
     * Defaults to an empty string when null.
     */
    public UrlStringConverter(String regex, String replacement) {
        this.regex = regex;
        if(replacement != null){
            this.replacement = replacement;
        }
    }

    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        String label = value;
        if(regex != null){
            label = label.replace(regex, replacement);
        }
        return "<a href=\"" + value + "\" target=\"external\">" + label + "</a>";
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}