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
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.config.CdmConfigurationKeys;
import eu.etaxonomy.cdm.vaadin.data.validator.PasswordsMatchValidator;
import eu.etaxonomy.cdm.vaadin.data.validator.PasswordsPolicyValidator;
import eu.etaxonomy.cdm.vaadin.event.UserAccountEvent;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
@SpringView(name=PasswordResetViewBean.NAME)
public class PasswordResetViewBean
        extends AbstractView<PasswordResetView,PasswordResetPresenter>
        implements PasswordResetView, View  {

    private static final long serialVersionUID = -1857662031516326300L;

    @Autowired
    protected EventBus.UIEventBus uiEventBus;

    @Autowired
    protected Environment env;

    public static final String NAME = "passwordReset";

    private Label header = new Label();

    private PasswordField password1Field = new PasswordField ("New password");

    private PasswordField password2Field = new PasswordField("Repeat password");

    private Button resetButton = new Button("Set new password");

    private Label messageLabel = new Label();

    private String userName;

    @Override
    protected void initContent() {
        VerticalLayout root = new VerticalLayout();

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(header, password1Field, password2Field, resetButton, messageLabel);
        formLayout.setComponentAlignment(messageLabel, Alignment.MIDDLE_CENTER);
        formLayout.setMargin(true);
        formLayout.setSizeUndefined();

        header.setValue("Reset your password ...");
        header.setStyleName(ValoTheme.LABEL_H3);
        resetButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        resetButton.setEnabled(false);
        resetButton.setWidth(100, Unit.PERCENTAGE);
        resetButton.addClickListener(e -> {
            getViewEventBus().publish(this, new UserAccountEvent(UserAccountEvent.UserAccountAction.RESET_PASSWORD,e));
        });
        messageLabel.setCaptionAsHtml(true);
        messageLabel.setVisible(false);

        setCompositionRoot(root);
        root.addComponent(formLayout);
        root.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);

        root.setSizeFull();

        password1Field.addValidator(new PasswordsPolicyValidator());
        password2Field.addValidator(new PasswordsMatchValidator("The passwords are not identical.", password1Field, password2Field));
        password1Field.addValueChangeListener(e -> updateResetButtonState());
        password2Field.addValueChangeListener(e -> updateResetButtonState());
    }

    private void updateResetButtonState() {
        resetButton.setEnabled(StringUtils.isNoneBlank(password1Field.getValue()) && password1Field.getErrorMessage() == null && password2Field.getErrorMessage() == null);
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
        resetButton.setEnabled(false);
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
        String dataSourceID = env.getProperty(CdmConfigurationKeys.CDM_DATA_SOURCE_ID);
        header.setValue("Reset your password for " + userName + " at " + dataSourceID);
    }

    @Override
    public PasswordField getPassword1Field() {
        return password1Field;
    }
}