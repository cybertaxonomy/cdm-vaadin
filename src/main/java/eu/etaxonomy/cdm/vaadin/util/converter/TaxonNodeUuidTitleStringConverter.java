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

import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author freimeier
 * @since 22.11.2017
 *
 */
public class TaxonNodeUuidTitleStringConverter extends UuidTitleStringConverter {

    private static final long serialVersionUID = 2372327533920312233L;

    @Override
    public String convertToPresentation(UUID value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if(value != null) {
            try {
               return CdmSpringContextHelper.getTaxonNodeService().load(value).getTaxon().getTitleCache();
            }catch(IllegalArgumentException iae) {
               return null;
            }
        }
        return null;
    }
}
