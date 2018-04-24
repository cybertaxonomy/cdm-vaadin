/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import org.vaadin.teemu.switchui.Switch;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;


/**
 * @author a.kohlbecker
 * @since May 11, 2017
 *
 */
public class SwitchableTextField extends CompositeCustomField<String> {

    private static final long serialVersionUID = -4760153886584883137L;

    private static final String PRIMARY_STYLE = "v-switchable-textfield";

    CssLayout root = new CssLayout();
    private TextField textField = new TextFieldNFix();
    private SwitchButton unlockSwitch = new SwitchButton();

    /**
     * @param caption
     */
    public SwitchableTextField(String caption) {
        super();
        textField.setCaption(caption);
        unlockSwitch.addValueChangeListener(e -> {
            textField.setEnabled(unlockSwitch.getValue());
            textField.focus();
        });
        unlockSwitch.setValueSetLister(e -> {
            textField.setEnabled(unlockSwitch.getValue());
        });

        addSizedComponent(root);
        addSizedComponent(textField);

        addStyledComponent(textField);
        addStyledComponent(unlockSwitch);
        addDefaultStyles();
    }

    /**
     * {@inheritDoc}textField
     */
    @Override
    protected Component initContent() {
        root = new CssLayout();
        root.addComponent(textField);
        root.setWidth(getWidth(), getWidthUnits());
        textField.setWidth(getWidth(), getWidthUnits());
        root.addComponent(unlockSwitch);
        setPrimaryStyleName(PRIMARY_STYLE);

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends String> getType() {
        return String.class;
    }

    /**
     * @return the textField
     */
    public TextField getTextField() {
        return textField;
    }

    /**
     * @return the unlockSwitch
     */
    public Switch getUnlockSwitch() {
        return unlockSwitch;
    }

    public void bindTo(FieldGroup fieldGroup, Object textPropertyId, Object switchPropertyId){
        fieldGroup.bind(textField, textPropertyId);
        fieldGroup.bind(unlockSwitch, switchPropertyId);
        textField.setEnabled(unlockSwitch.getValue());
    }

    public void unbindFrom(FieldGroup fieldGroup){
        fieldGroup.unbind(textField);
        fieldGroup.unbind(unlockSwitch);
        textField.setEnabled(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        unlockSwitch.addStyleName(Switch.DOM_STYLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        // the unlockSwitch needs to be handled explicitly, for the textField this is not needed.
        unlockSwitch.setVisible(visible);
    }

}
