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
import java.util.UUID;

import com.vaadin.data.util.converter.Converter;

/**
 * @author freimeier
 * @date 21.11.2017
 *
 */
abstract class UuidTitleStringConverter implements Converter<String, UUID> {

    private static final long serialVersionUID = -8596384308607670542L;

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public abstract String convertToPresentation(UUID value, Class<? extends String> targetType, Locale locale)
            throws ConversionException;

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public UUID convertToModel(String value, Class<? extends UUID> targetType, Locale locale) throws ConversionException {
        /*
         *  This converter is only used to convert UUIDs into a human readable form.
         *  The title string might not be unique and therefore can't reliably be converted to an UUID again.
         */
        throw new ConversionException("Title string might not be unique and therefore can't be converted to an UUID!");
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getModelType()
     */
    @Override
    public Class<UUID> getModelType() {
        return UUID.class;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#getPresentationType()
     */
    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
