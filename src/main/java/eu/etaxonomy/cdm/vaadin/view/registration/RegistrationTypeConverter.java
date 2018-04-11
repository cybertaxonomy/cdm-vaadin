/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Locale;

import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.FontAwesome;

import eu.etaxonomy.cdm.api.service.dto.RegistrationType;

/**
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public class RegistrationTypeConverter implements Converter<String, RegistrationType> {
    @Override
    public RegistrationType convertToModel(String value, Class<? extends RegistrationType> targetType,
            Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        // not implemented
        return null;
    }

    @Override
    public String convertToPresentation(RegistrationType value, Class<? extends String> targetType,
            Locale locale) throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value.equals(RegistrationType.NAME)) {
            return FontAwesome.TAG.getHtml();
        }
        if(value.equals(RegistrationType.TYPIFICATION)) {
            return FontAwesome.TAGS.getHtml();
        }
        return FontAwesome.WARNING.getHtml();
    }

    @Override
    public Class<RegistrationType> getModelType() {
        return RegistrationType.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}