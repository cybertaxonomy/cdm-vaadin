/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.util.Optional;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.format.ReferenceEllypsisFormatter.LabelType;
import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.RelationshipBase.Direction;
import eu.etaxonomy.cdm.model.name.NameRelationshipType;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.model.name.NameRelationshipDTO;
import eu.etaxonomy.cdm.vaadin.util.ReferenceEllypsisCaptionGenerator;


/**
 * @author a.kohlbecker
 * @since May 3, 2018
 *
 */
public class NameRelationField extends CompositeCustomField<NameRelationshipDTO> {

    /**
     *
     */
    public static final String PRIMARY_STYLE = "v-name-relation-field";

    private static final long serialVersionUID = -7080885013120044655L;

    private CssLayout compositeWrapper = new CssLayout();

    private CssLayout toolBar= new CssLayout();

    private Button removeButton = ButtonFactory.REMOVE_ITEM.createButton();

    private Button newButton = ButtonFactory.ADD_ITEM.createButton();

    private BeanFieldGroup<NameRelationshipDTO> fieldGroup = new BeanFieldGroup<>(NameRelationshipDTO.class);

    ToOneRelatedEntityCombobox<TaxonName> relatedNameComboBox;

    ToOneRelatedEntityCombobox<Reference> citatonComboBox;

    TextFieldNFix citationMicroReferenceField = new TextFieldNFix();

    TextFieldNFix ruleConsideredField = new TextFieldNFix();

    NativeSelect codeEditionSelect = new NativeSelect();

    private Direction direction;

    private NameRelationshipType type;

    private GridLayout grid;

    private String nameFieldCaption;


    /**
     * @param string
     */
    public NameRelationField(String caption, String nameFieldCaption, Direction direction, NameRelationshipType type) {
        this.direction = direction;
        this.type = type;

        setCaption(caption);
        setPrimaryStyleName(PRIMARY_STYLE);

        if(nameFieldCaption == null){
            this.nameFieldCaption = "Related name";
        }

        relatedNameComboBox = new ToOneRelatedEntityCombobox<TaxonName>(this.nameFieldCaption, TaxonName.class);
        citatonComboBox = new ToOneRelatedEntityCombobox<Reference>("Reference", Reference.class);

        setValidationVisible(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<FieldGroup> getFieldGroup() {
        return Optional.of(fieldGroup);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {

        newButton.addClickListener(e -> {
            setValue(new NameRelationshipDTO(direction, type));
            updateToolBarButtonStates();
            });
        removeButton.addClickListener(e -> {
                setValue(null);
                updateToolBarButtonStates();
            });
        relatedNameComboBox.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<TaxonName>(relatedNameComboBox));
        citatonComboBox.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(citatonComboBox, false));
        citatonComboBox.getSelect().setCaptionGenerator(
                new ReferenceEllypsisCaptionGenerator(LabelType.BIBLIOGRAPHIC, citatonComboBox.getSelect())
                );

        grid = new GridLayout(2, 3);

        grid.addComponent(relatedNameComboBox, 0, 0, 1, 0);

        relatedNameComboBox.setCaption(nameFieldCaption);
        citatonComboBox.setCaption("Reference");
        citationMicroReferenceField.setCaption("Reference detail");
        ruleConsideredField.setCaption("Rule considered");
        codeEditionSelect.setCaption("Nom. code");

        grid.addComponent(citatonComboBox, 0, 1, 0, 1);
        grid.addComponent(citationMicroReferenceField, 1, 1, 1, 1);
        grid.addComponent(ruleConsideredField, 0, 2, 0, 2);
        grid.addComponent(codeEditionSelect, 1, 2, 1, 2);

        relatedNameComboBox.setWidth(100, Unit.PERCENTAGE);
        ruleConsideredField.setWidth(100, Unit.PERCENTAGE);
        citatonComboBox.setWidth(100, Unit.PERCENTAGE);
        codeEditionSelect.setWidth(100, Unit.PERCENTAGE);



        grid.setColumnExpandRatio(0, 7);
        grid.setWidth(100, Unit.PERCENTAGE);

        toolBar.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP + " toolbar");
        toolBar.addComponents(newButton, removeButton);

        compositeWrapper.setStyleName("margin-wrapper");
        compositeWrapper.addComponents(toolBar);

        addSizedComponents(compositeWrapper);
        addStyledComponents(relatedNameComboBox, citationMicroReferenceField, citatonComboBox, ruleConsideredField, codeEditionSelect, newButton, removeButton);

        return compositeWrapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<NameRelationshipDTO> getType() {
        return NameRelationshipDTO.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setInternalValue(NameRelationshipDTO newValue) {

        NameRelationshipDTO oldValue = getValue();

        super.setInternalValue(newValue);

        newValue = HibernateProxyHelper.deproxy(newValue);
        if(newValue != null) {
            compositeWrapper.addComponent(grid);
            fieldGroup.bind(relatedNameComboBox, "otherName");
            fieldGroup.bind(citationMicroReferenceField, "citationMicroReference");
            fieldGroup.bind(citatonComboBox, "citation");
            fieldGroup.bind(ruleConsideredField, "ruleConsidered");
            fieldGroup.bind(codeEditionSelect, "codeEdition");

            fieldGroup.setItemDataSource(newValue);
        } else {
            if(oldValue != null){
                compositeWrapper.removeComponent(grid);
                fieldGroup.unbind(relatedNameComboBox);
                fieldGroup.unbind(citationMicroReferenceField);
                fieldGroup.unbind(citatonComboBox);
                fieldGroup.unbind(ruleConsideredField);
                fieldGroup.unbind(codeEditionSelect);

                fieldGroup.setItemDataSource(newValue);
            }
        }

        updateToolBarButtonStates();

    }


   private void updateToolBarButtonStates(){
       boolean hasValue = getValue() != null;
       removeButton.setEnabled(hasValue);
       newButton.setEnabled(!hasValue);
   }

    /**
     * @return the relatedNameComboBox
     */
    public ToOneRelatedEntityCombobox<TaxonName> getRelatedNameComboBox() {
        return relatedNameComboBox;
    }

    /**
     * @return the citatonComboBox
     */
    public ToOneRelatedEntityCombobox<Reference> getCitatonComboBox() {
        return citatonComboBox;
    }

    public AbstractSelect getCodeEditionSelect(){
        return codeEditionSelect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setDeepReadOnly(readOnly, grid, null);
        setDeepReadOnly(readOnly, toolBar, null);
    }


}
