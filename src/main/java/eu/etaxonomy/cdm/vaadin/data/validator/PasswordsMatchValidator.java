/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.data.validator;

import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.PasswordField;

/**
 * Checks that the passwords entered in two password fields match.
 * Intended to be added as validator to the second field of the
 * {@link PasswordField} pair.
 *
 * @author a.kohlbecker
 * @since Nov 12, 2021
 */
public class PasswordsMatchValidator extends AbstractStringValidator {

    private static final long serialVersionUID = -9048318480638222817L;

    PasswordField firstField;
    PasswordField secondField;

    /**
     * @param errorMessage
     */
    public PasswordsMatchValidator(String errorMessage, PasswordField firstField, PasswordField secondField) {
        super(errorMessage);
        this.firstField = firstField;
        this.secondField = secondField;
        firstField.addValueChangeListener(event -> {
            getErrorMessage();
        });
    }

    @Override
    protected boolean isValidValue(String value) {
        return firstField.getValue().equals(secondField.getValue());
    }

}
