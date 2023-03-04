/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.data.validator;

import org.joda.time.DateTimeFieldType;
import org.joda.time.Partial;

import com.vaadin.data.Validator;

/**
 * @author a.kohlbecker
 * @since Mar 27, 2019
 *
 */
public class PartialCompletenesValidator implements Validator {

    private static final long serialVersionUID = -2739242148516872452L;

    @Override
    public void validate(Object value) throws InvalidValueException {
        if(value != null){
            Partial partial = (Partial)value;

            if(partial.isSupported(DateTimeFieldType.monthOfYear())
                    && !partial.isSupported(DateTimeFieldType.year())
                ){
                throw new InvalidValueException("The Partial must support year if monthOfYear is set");
            }
            if(partial.isSupported(DateTimeFieldType.dayOfMonth())
                    && ! (
                            partial.isSupported(DateTimeFieldType.monthOfYear())
                            && partial.isSupported(DateTimeFieldType.year())
                         )
                ){
                throw new InvalidValueException("The Partial must support monthOfYear and year if dayOfMonth is set");
            }
        }
    }
}