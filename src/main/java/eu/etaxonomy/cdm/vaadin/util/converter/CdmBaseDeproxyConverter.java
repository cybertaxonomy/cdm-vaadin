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

import com.vaadin.data.util.converter.Converter;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * Converter which cares for deproxing hibernate proxy beans
 *
 * @author a.kohlbecker
 * @since Jul 11, 2017
 *
 */
public class CdmBaseDeproxyConverter<MODEL extends CdmBase> implements Converter<MODEL, MODEL> {

    private static final long serialVersionUID = 1565836754627309870L;

    /**
     * {@inheritDoc}
     */
    @Override
    public MODEL convertToModel(MODEL value, Class<? extends MODEL> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MODEL convertToPresentation(MODEL value, Class<? extends MODEL> targetType, Locale locale)
            throws com.vaadin.data.util.converter.Converter.ConversionException {
        return HibernateProxyHelper.deproxy(value, targetType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MODEL> getModelType() {
        return (Class<MODEL>)CdmBase.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<MODEL> getPresentationType() {
        return (Class<MODEL>)CdmBase.class;
    }

}
