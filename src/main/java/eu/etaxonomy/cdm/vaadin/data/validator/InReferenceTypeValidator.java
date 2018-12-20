/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.data.validator;

import java.util.Objects;
import java.util.Set;

import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractSelect;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;

/**
 * @author a.kohlbecker
 * @since Dec 20, 2018
 *
 */
public class InReferenceTypeValidator implements Validator {


    private static final long serialVersionUID = 5704902636623629859L;
    private AbstractSelect referenceTypeSelect;

    public InReferenceTypeValidator(AbstractSelect referenceTypeSelect){
        this.referenceTypeSelect = referenceTypeSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(Object value) throws InvalidValueException {
        ReferenceType type = (ReferenceType)referenceTypeSelect.getValue();
        if(value != null){
            Set<ReferenceType> applicableInRefTypes = type.inReferenceContraints(type);
            Reference inReference = (Reference)value;
            if(!applicableInRefTypes.contains(inReference.getType())){
                throw new InvalidValueException(Objects.toString(inReference.getType(), "[NULL]") + " is not a suitable in-reference type for " + Objects.toString(type, "[NULL]") );
            }
        }

    }

}
