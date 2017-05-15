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

import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
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


    public RegistrationPopupEditor() {
        super(new FormLayout(), Registration.class);
        FormLayout form = (FormLayout)getFieldLayout();
        form.setSpacing(true);
        form.setMargin(true);

        TextField identifierField = new TextField("Identifier");
        addField(identifierField, "identifier");
        identifierField.setEnabled(false);

        TextField specificIdentifierField = new TextField("Specific Identifier");
        addField(specificIdentifierField, "specificIdentifier");
        specificIdentifierField.setEnabled(false);

        ListSelect statusSelect = new ListSelect("Status", Arrays.asList(RegistrationStatus.values()));
        statusSelect.setNullSelectionAllowed(false);
        statusSelect.setRows(1);
        addField(statusSelect, "status");

        TextField submitterField = new TextField("Submitter");
        submitterField.setEnabled(false);
        addField(submitterField, "submitter");

//        TextField institutionField = new TextField("Institution");
//        institutionField.setEnabled(false);
//        addField(institutionField, "institution");

        DateField registrationDateField = new DateField("Registration date");
        addField(registrationDateField, "registrationDate");
        registrationDateField.setConverter(new JodaDateTimeConverter());
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
    @Autowired
    protected void injectPresenter(RegistrationEditorPresenter presenter) {
        setPresenter(presenter);
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
