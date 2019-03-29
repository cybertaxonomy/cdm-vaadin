/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.data.validator;

import com.vaadin.data.Validator;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.vaadin.model.name.NameRelationshipDTO;

/**
 * Validates a {@link TaxonName} for existence of the nomenclatural reference.
 *
 * Compatible with fields operating on {@link TaxonName}, {@link NameRelationshipDTO},
 *
 * @author a.kohlbecker
 * @since Mar 27, 2019
 *
 */
public class NomenclaturalReferenceExistsValidator implements Validator {


    private static final long serialVersionUID = -7750232876262922982L;

    private String userHint = "";

    public NomenclaturalReferenceExistsValidator(String userHint){
        this.userHint = userHint;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        if(value != null){
            if(value instanceof TaxonName){
                TaxonName name = (TaxonName)value;
                validateName(name);
            }
            if(value instanceof NameRelationshipDTO){
                NameRelationshipDTO nameRelDto = (NameRelationshipDTO)value;
                validateName(nameRelDto.getOtherName());
            }
        }

    }

    /**
     * @param name
     */
    public void validateName(TaxonName name) {
        if(name.getNomenclaturalReference() == null) {
            throw new InvalidValueException("The taxon name must have a nomenclatural reference. " + userHint);
        }
    }

}
