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
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public final class UrlStringConverter implements Converter<String, String> {
    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        // TODO Auto-generated method stub
        return "<a href=\"" + value + "\" target=\"external\">" + value + "</a>";
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