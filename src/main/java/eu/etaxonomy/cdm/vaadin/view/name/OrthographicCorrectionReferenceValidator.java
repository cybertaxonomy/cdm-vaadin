/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import com.vaadin.data.Validator;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.model.name.NameRelationshipDTO;

/**
 * @author a.kohlbecker
 * @since Dec 10, 2018
 *
 */
public class OrthographicCorrectionReferenceValidator implements Validator {

    private static final long serialVersionUID = 8489749130640936863L;

    private Field<Reference> nomenclaturalReferenceField;

    public OrthographicCorrectionReferenceValidator(Field<Reference> nomenclaturalReferenceField){
        this.nomenclaturalReferenceField = nomenclaturalReferenceField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        if(value != null){
            NameRelationshipDTO nameRel = (NameRelationshipDTO)value;
            if(nameRel.getOtherName() != null){
                if(nameRel.getOtherName().getNomenclaturalReference() == null){
                    throw new InvalidValueException("The nomenclatural reference of the corrected name must not be null.");
                } else {
                    if(!nameRel.getOtherName().getNomenclaturalReference().equals(nomenclaturalReferenceField.getValue())){
                        throw new InvalidValueException("The nomenclatural references of the corrected name and of the correcting name must be equal.");
                    }
                }
            }
        }

    }

}
