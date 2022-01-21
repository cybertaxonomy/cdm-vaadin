/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.vaadin.spring.events.EventBus;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.data.validator.PasswordsMatchValidator;
import eu.etaxonomy.cdm.vaadin.data.validator.PasswordsPolicyValidator;
import eu.etaxonomy.cdm.vaadin.event.UserAccountEvent;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
@SpringView(name=AccountRegistrationViewBean.NAME)
public class AccountRegistrationViewBean extends AbstractView<AccountRegistrationPresenter> implements AccountRegistrationView, View  {

    private static final long serialVersionUID = -1857662031516326300L;

    @Autowired
    protected EventBus.UIEventBus uiEventBus;

    @Autowired
    protected Environment env;

    public static final String NAME = "accountCreation";

    private Label header = new Label();

    private TextField emailAddress = new TextField("Email address");

    private TextField userName = new TextField("User name");

    private PasswordField password1Field = new PasswordField ("Password");

    private PasswordField password2Field = new PasswordField("Repeat password");

    private TextField prefix = new TextField("Name prefix");

    private TextField familyName = new TextField("Family name");

    private TextField givenName = new TextField("Given name");

    private Button registerButton = new Button("Register user account");

    private Label messageLabel = new Label();

    @Override
    protected void initContent() {
        VerticalLayout root = new VerticalLayout();

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(header, userName, password1Field, password2Field, prefix, givenName, familyName, registerButton, messageLabel);
        formLayout.setComponentAlignment(messageLabel, Alignment.MIDDLE_CENTER);
        formLayout.setMargin(true);
        formLayout.setSizeUndefined();

        header.setValue("Register a user account");
        header.setStyleName(ValoTheme.LABEL_H3);
        registerButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        registerButton.setEnabled(false);
        registerButton.setWidth(100, Unit.PERCENTAGE);
        registerButton.addClickListener(e -> {
            getViewEventBus().publish(this, new UserAccountEvent(UserAccountEvent.UserAccountAction.REGISTER_ACCOUNT, e));
        });
        messageLabel.setCaptionAsHtml(true);
        messageLabel.setVisible(false);

        setCompositionRoot(root);
        root.addComponent(formLayout);
        root.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);

        root.setSizeFull();

        userName.setRequired(true);
        password1Field.setRequired(true);
        password2Field.setRequired(true);

        password1Field.addValidator(new PasswordsPolicyValidator());
        password2Field.addValidator(new PasswordsMatchValidator("The passwords are not identical.", password1Field, password2Field));
        password1Field.addValueChangeListener(e -> updateResetButtonState());
        password2Field.addValueChangeListener(e -> updateResetButtonState());

        // value will be set by presenter and user must not change it here
        emailAddress.setEnabled(false);
        emailAddress.setReadOnly(true);


        prefix.setInputPrompt("Dr., Prof, ...");
    }

    private void updateResetButtonState() {
        registerButton.setEnabled(StringUtils.isNoneBlank(password1Field.getValue()) && password1Field.getErrorMessage() == null && password2Field.getErrorMessage() == null);
    }

    @Override
    // TODO pull up to AbstractView and let AbstractView implement View?
    public void enter(ViewChangeEvent event) {
        getPresenter().onViewEnter();
    }

    @Override
    public void showErrorMessage(String text) {
        messageLabel.setValue(text);
        messageLabel.setStyleName(ValoTheme.LABEL_FAILURE);
        messageLabel.setVisible(true);
        disableForm();
    }

    @Override
    public void showSuccessMessage(String text) {
        messageLabel.setValue(text);
        messageLabel.setStyleName(ValoTheme.LABEL_SUCCESS);
        messageLabel.setVisible(true);
        disableForm();
    }

    public void disableForm() {
        password1Field.setEnabled(false);
        password2Field.setEnabled(false);
        registerButton.setEnabled(false);
    }

    @Override
    public TextField getUserName() {
        return userName;
    }

    @Override
    public PasswordField getPassword1Field() {
        return password1Field;
    }

     @Override
    public TextField getEmailAddress() {
        return emailAddress;
    }

     @Override
     public TextField getPrefix() {
        return prefix;
    }

     @Override
     public TextField getFamilyName() {
        return familyName;
    }

     @Override
     public TextField getGivenName() {
        return givenName;
    }


}
