/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.converter;

import java.text.ParseException;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;

import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;

import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.cdm.model.location.Point.Sexagesimal;

/**
 * Converts longitute and latitute string representations into
 * double values. At the same time this class can be used as Validator.
 *
 * @author a.kohlbecker
 * @since Nov 20, 2018
 *
 */
public class GeoLocationConverterValidator implements Converter<String, Double>, Validator {

    public enum Axis {
        LONGITUDE, LATITUDE;
    }

    private static final long serialVersionUID = -1780028474672132160L;

    private Axis axis;

    public GeoLocationConverterValidator(Axis axis){
        this.axis = axis;
    }

    @Override
    public Double convertToModel(String value, Class<? extends Double> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(value == null){
            return null;
        }
        try {
            if(axis == Axis.LONGITUDE){
                return Point.parseLongitude(value);
            } else {
                return Point.parseLatitude(value);
            }
        } catch (ParseException e) {
            LogManager.getLogger(getClass()).error(e);
            throw new ConversionException(e);
        }
    }

    @Override
    public String convertToPresentation(Double value, Class<? extends String> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {

        if(value == null){
            return null;
        }
        if(axis == Axis.LONGITUDE){
            return Sexagesimal.valueOf(value, false).toString();
        } else {
            return Sexagesimal.valueOf(value, true).toString();
        }

    }

    @Override
    public Class<Double> getModelType() {
        return Double.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        if(value != null && value instanceof String && !((String)value).isEmpty()){
            try {
                convertToModel((String)value, Double.class, null);
            } catch (com.vaadin.data.util.converter.Converter.ConversionException e) {
                throw new InvalidValueException("Invalid " + axis.name().toLowerCase());
            }
        }


    }

}
