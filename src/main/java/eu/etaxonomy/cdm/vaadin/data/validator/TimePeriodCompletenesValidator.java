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

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 * @author a.kohlbecker
 * @since Mar 27, 2019
 */
public class TimePeriodCompletenesValidator implements Validator {

    private static final long serialVersionUID = 4651375734846907644L;

    private PartialCompletenesValidator partialValidator = new PartialCompletenesValidator();

    @Override
    public void validate(Object value) throws InvalidValueException {
        if(value != null){
            TimePeriod timePeriod = (TimePeriod)value;
            if(timePeriod.getStart() != null){
                partialValidator.validate(timePeriod.getStart());
            }
            if(timePeriod.getEnd() != null){
                partialValidator.validate(timePeriod.getEnd());
            }
        }
    }
}