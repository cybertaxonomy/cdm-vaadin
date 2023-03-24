/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.data.validator;

import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.data.Validator;
import com.vaadin.ui.PasswordField;

import eu.etaxonomy.cdm.validation.constraint.ValidPasswordValidator;
import eu.etaxonomy.cdm.validation.constraint.ValidPasswordValidator.PasswordRulesValidator;

/**
 * Checks that the passwords entered in two password fields match.
 * Intended to be added as validator to the second field of the
 * {@link PasswordField} pair.
 *
 * @author a.kohlbecker
 * @since Nov 12, 2021
 */
public class PasswordsPolicyValidator implements Validator {

    private static final long serialVersionUID = -9048318480638222817L;

    private PasswordRulesValidator validator = new ValidPasswordValidator.PasswordRulesValidator();

    @Override
    public void validate(Object value) throws InvalidValueException {
        String password = (String)value;
        if(password != null && !password.isEmpty()) {
            List<String> violationMessages = validator.validateUserPassword(password);
            if(!violationMessages.isEmpty()) {
                throw new InvalidValueException(violationMessages.stream().collect(Collectors.joining(" ")));
            }
        }
    }
}