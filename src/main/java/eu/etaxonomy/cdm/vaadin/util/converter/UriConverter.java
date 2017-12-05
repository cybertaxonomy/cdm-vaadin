/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.net.URI;
import java.util.Locale;

import com.vaadin.data.util.converter.Converter;

/**
 * Creates a link from the values in the column.
 *
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public final class UriConverter implements Converter<String, URI> {

    private static final long serialVersionUID = -6695362724769275484L;

    public UriConverter() {

    }


    @Override
    public Class<URI> getModelType() {
        return URI.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public URI convertToModel(String value, Class<? extends URI> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value != null){
            try{
                URI uri = new URI(value);
                return uri;
            } catch (Exception e){
                throw new com.vaadin.data.util.converter.Converter.ConversionException(e);
            }
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String convertToPresentation(URI value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(value != null){
            try{
                return value.toString();
            } catch (Exception e){
                throw new com.vaadin.data.util.converter.Converter.ConversionException(e);
            }
        }
        return null;
    }
}