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

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.converter.Converter;

import eu.etaxonomy.cdm.common.DOI;

/**
 * Creates a link from the values in the column.
 *
 * @author a.kohlbecker
 * @since Mar 7, 2017
 *
 */
public final class DoiConverter implements Converter<String, DOI> {

    private static final long serialVersionUID = -6695362724769275484L;

    public DoiConverter() {

    }


    @Override
    public Class<DOI> getModelType() {
        return DOI.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public DOI convertToModel(String value, Class<? extends DOI> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        if(StringUtils.isNoneEmpty(value)){
            try{
                DOI doi = DOI.fromString(value);
                return doi;
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
    public String convertToPresentation(DOI value, Class<? extends String> targetType, Locale locale)
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