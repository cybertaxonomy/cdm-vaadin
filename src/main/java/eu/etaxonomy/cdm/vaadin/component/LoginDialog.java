/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.vaadin.design.LoginDialogDesign;

/**
 * @author a.kohlbecker
 * @since Apr 25, 2017
 *
 */
public class LoginDialog extends LoginDialogDesign {

    private static final long serialVersionUID = 6792455534312958806L;

    public LoginDialog() {
        addSelectedTabChangeListener(e -> focusFirstElement(e.getTabSheet()));
        getMessageLabel().setVisible(false);
        getTab(1).setEnabled(false);
        getTab(2).setEnabled(false);
        focusFirstElement(this);
    }

    /**
     * @param tabSheet
     * @return
     */
    private void focusFirstElement(TabSheet tabSheet) {
        switch (tabSheet.getTabIndex()){
        case 0: // login
            getUserName().focus();
            break;
        case 1: // Register
            getUserNameSuggestion().focus();
            break;
        case 2: // Password Recovery
            getUserNameOrEmail().focus();
            break;
        default:
            // Ignore
            break;

        }
    }

    /**
     * @return the userName
     */
    public TextField getUserName() {
        return userName;
    }

    /**
     * @return the password
     */
    public PasswordField getPassword() {
        return password;
    }

    /**
     * @return the loginButton
     */
    public Button getLoginButton() {
        return loginButton;
    }

    /**
     * @return the cancelLoginButton
     */
    public Button getCancelLoginButton() {
        return cancelLoginButton;
    }

    /**
     * @return the userNameSuggestion
     */
    public TextField getUserNameSuggestion() {
        return userNameSuggestion;
    }

    /**
     * @return the email
     */
    public TextField getEmail() {
        return email;
    }

    /**
     * @return the registerButton
     */
    public Button getRegisterButton() {
        return registerButton;
    }

    /**
     * @return the cancelRegistrationButton
     */
    public Button getCancelRegistrationButton() {
        return cancelRegistrationButton;
    }

    /**
     * @return the userNameOrEmail
     */
    public TextField getUserNameOrEmail() {
        return userNameOrEmail;
    }

    /**
     * @return the sendOnetimeLogin
     */
    public Button getSendOnetimeLogin() {
        return sendOnetimeLogin;
    }

    public Label getMessageLabel() {
        return message;
    }


}
