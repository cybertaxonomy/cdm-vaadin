/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.data.util.converter.Converter.ConversionException;

import eu.etaxonomy.cdm.vaadin.util.converter.GeoLocationConverterValidator;
import eu.etaxonomy.cdm.vaadin.util.converter.GeoLocationConverterValidator.Axis;

/**
 * @author a.kohlbecker
 * @since Nov 22, 2018
 *
 */
public class LongLatField extends TextFieldNFix {

    private static final long serialVersionUID = -7794173729396522366L;

    private GeoLocationConverterValidator converterValidator;

    private Axis axis;

    public LongLatField(String caption, GeoLocationConverterValidator.Axis axis){
        super(caption);
        this.axis = axis;
        converterValidator = new GeoLocationConverterValidator(axis);
        setConverter(converterValidator);
        addValidator(converterValidator);
    }

    @Override
    protected String getConversionError(Class<?> dataSourceType,
            ConversionException e) {
        return "Invalid " + axis.name().toLowerCase() + " value";
    }

}
