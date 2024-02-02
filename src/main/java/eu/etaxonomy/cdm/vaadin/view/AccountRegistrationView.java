/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Nov 22, 2021
 */
public interface AccountRegistrationView
        extends ApplicationView<AccountRegistrationView,AccountRegistrationPresenter>   {

    public TextField getUserName();

    public PasswordField getPassword1Field();

    public void showSuccessMessage(String text);

    public void showErrorMessage(String text, boolean disable);

    public TextField getEmailAddress();

    public TextField getPrefix();

    public TextField getFamilyName();

    public TextField getGivenName();

}
