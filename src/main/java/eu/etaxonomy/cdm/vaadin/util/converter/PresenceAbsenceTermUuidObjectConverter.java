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

import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author freimeier
 * @date 22.11.2017
 *
 */
public class PresenceAbsenceTermUuidObjectConverter implements Converter<Object, UUID> {

    private static final long serialVersionUID = 4373972113891990480L;

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToModel(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public UUID convertToModel(Object value, Class<? extends UUID> targetType, Locale locale) throws ConversionException {
        if(value != null) {
            return ((PresenceAbsenceTerm) value).getUuid();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public Object convertToPresentation(UUID value, Class<? extends Object> targetType, Locale locale) throws ConversionException {
        if(value != null) {
            try {
               return CdmSpringContextHelper.getTermService().load(value);
            }catch(IllegalArgumentException iae) {
               return null;
            }
        }
        return null;
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
    public Class<Object> getPresentationType() {
        return Object.class;
    }
}