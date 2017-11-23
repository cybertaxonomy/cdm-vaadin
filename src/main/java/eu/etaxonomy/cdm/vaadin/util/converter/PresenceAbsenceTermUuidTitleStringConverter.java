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

import eu.etaxonomy.cdm.model.common.Language;
import eu.etaxonomy.cdm.model.common.Representation;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.util.DistributionEditorUtil;

/**
 * @author freimeier
 * @date 22.11.2017
 *
 */
public class PresenceAbsenceTermUuidTitleStringConverter extends UuidTitleStringConverter {

    private static final long serialVersionUID = 6205306206377080294L;

    /* (non-Javadoc)
     * @see com.vaadin.data.util.converter.Converter#convertToPresentation(java.lang.Object, java.lang.Class, java.util.Locale)
     */
    @Override
    public String convertToPresentation(UUID value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        PresenceAbsenceTerm presenceAbsenceTerm = null;
        String formattedValue = null;
        if (value == null) {
            return formattedValue;
        }
        try{
            presenceAbsenceTerm = (PresenceAbsenceTerm) CdmSpringContextHelper.getTermService().load(value);
        }catch(IllegalArgumentException iae) {
            presenceAbsenceTerm = null;
        }

        if(presenceAbsenceTerm != null){
            Representation representation = presenceAbsenceTerm.getRepresentation(Language.DEFAULT());
            if(representation!=null){
                if(DistributionEditorUtil.isAbbreviatedLabels()){
                    formattedValue = representation.getAbbreviatedLabel();
                }
                else{
                    formattedValue = representation.getLabel();
                }
            }
            if(formattedValue==null){
                formattedValue = presenceAbsenceTerm.getTitleCache();
            }
        }
        return formattedValue;
    }
}
