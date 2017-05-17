/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.component.SelectFieldFactory;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.util.converter.JodaDateTimeConverter;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class RegistrationPopupEditor extends AbstractCdmPopupEditor<Registration, RegistrationEditorPresenter>
    implements RegistrationPopEditorView, AccessRestrictedView {

    private static final long serialVersionUID = 5418275817834009509L;

    @Autowired
    private SelectFieldFactory selectFieldFactory;

    TextField identifierField;
    TextField specificIdentifierField;

    public RegistrationPopupEditor() {
        super(new FormLayout(), Registration.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initContent() {

        FormLayout form = (FormLayout)getFieldLayout();
        form.setSpacing(true);
        form.setMargin(true);

        identifierField = new TextField("Identifier");
        addField(identifierField, "identifier");

        specificIdentifierField = new TextField("Specific Identifier");
        addField(specificIdentifierField, "specificIdentifier");

        ListSelect statusSelect = new ListSelect("Status", Arrays.asList(RegistrationStatus.values()));
        statusSelect.setNullSelectionAllowed(false);
        statusSelect.setRows(1);
        addField(statusSelect, "status");

        ListSelect submitterField = selectFieldFactory.createListSelect("Submitter", User.class);
        submitterField.setEnabled(false);
        submitterField.setWidth(100, Unit.PERCENTAGE);
        addField(submitterField, "submitter");

        ListSelect institutionField = selectFieldFactory.createListSelect("Institution", Institution.class);
        institutionField.setEnabled(false);
        institutionField.setWidth(100, Unit.PERCENTAGE);
        addField(institutionField, "institution");

        DateField registrationDateField = new DateField("Registration date");
        addField(registrationDateField, "registrationDate");
        registrationDateField.setConverter(new JodaDateTimeConverter());

    }

    @Override
    protected void afterItemDataSourceSet() {
        identifierField.setEnabled(false);
        specificIdentifierField.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Registration editor";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        // none
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDefaultComponentStyles() {
        return "tiny";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }
}
