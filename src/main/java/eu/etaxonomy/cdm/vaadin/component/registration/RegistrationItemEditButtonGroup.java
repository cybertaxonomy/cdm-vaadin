/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.security.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
import eu.etaxonomy.vaadin.component.CompositeStyledComponent;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 *
 */
public class RegistrationItemEditButtonGroup extends CompositeStyledComponent {


    /**
     *
     */
    private static final String DEFAULT_BUTTON_STYLES = "";

    private static final long serialVersionUID = -5059163772392864050L;

    public static final String STYLE_NAMES = "edit-button-group  " + ValoTheme.LAYOUT_COMPONENT_GROUP;

    private IdButton<TaxonName> nameIdButton = null;

    private List<TypeDesignationWorkingSetButton> typeDesignationButtons = new ArrayList<>();

    private List<Label> labels = new ArrayList<>();

    private Button addTypeDesignationButton;

    private Label nameLabel = null;

    private Link identifierLink;

    public RegistrationItemEditButtonGroup(RegistrationDTO regDto) {

        boolean isRegistrationLocked = EnumSet.of(
                RegistrationStatus.PUBLISHED, RegistrationStatus.REJECTED)
                .contains(regDto.getStatus());

        setWidth(100, Unit.PERCENTAGE);

        if(regDto.getName() != null){
            Button nameButton = new Button("Name:");
            nameButton.setDescription("Edit the Name");
            nameIdButton = new IdButton<TaxonName>(TaxonName.class, regDto.getName().getId(), nameButton);
            Label nameLabel = new Label(regDto.getName().getLabel());
            nameLabel.setWidthUndefined();
            boolean userHasPermission = UserHelper.fromSession().userHasPermission(regDto.registration().getName(), CRUD.UPDATE);
            nameButton.setEnabled(!isRegistrationLocked && userHasPermission);

            addComponent(nameIdButton.getButton());
            PermissionDebugUtils.addGainPerEntityPermissionButton(this, TaxonName.class, regDto.getName().getId(),
                    EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
            addComponent(nameLabel);
        } else {
            // no name in the registration! we only show the typified name as label
            if(regDto.getTypifiedName() != null){
                nameLabel = new Label(regDto.getTypifiedName().getLabel());
                addComponent(nameLabel);
            }
        }
        if(regDto.getOrderdTypeDesignationWorkingSets() != null){
            for(TypedEntityReference<TypeDesignationBase<?>> baseEntityRef : regDto.getOrderdTypeDesignationWorkingSets().keySet()) {
                TypeDesignationWorkingSet typeDesignationWorkingSet = regDto.getOrderdTypeDesignationWorkingSets().get(baseEntityRef);
                String buttonLabel = SpecimenOrObservationBase.class.isAssignableFrom(baseEntityRef.getType()) ? "Type": "NameType";
                Button tdButton = new Button(buttonLabel + ":");
                tdButton.setDescription("Edit the type designation working set");
                tdButton.setEnabled(!isRegistrationLocked && UserHelper.fromSession().userHasPermission(baseEntityRef.getType(), baseEntityRef.getId(), CRUD.UPDATE));
                addComponent(tdButton);

                PermissionDebugUtils.addGainPerEntityPermissionButton(this, SpecimenOrObservationBase.class,
                        baseEntityRef.getId(), EnumSet.of(CRUD.UPDATE, CRUD.DELETE), RegistrationStatus.PREPARATION.name());

                typeDesignationButtons.add(new TypeDesignationWorkingSetButton(
                        typeDesignationWorkingSet.getWorkingsetType(),
                        typeDesignationWorkingSet.getWorkingSetId(),
                        tdButton)
                        );
                String labelText = typeDesignationWorkingSet.getRepresentation();
                labelText = labelText.replaceAll("^[^:]+:", ""); // remove "Type:", "NameType:" from the beginning
                Label label = new Label(labelText);

                label.setWidthUndefined();
                addComponent(label);
                labels.add(label);
            }
        }
        addTypeDesignationButton = new Button(FontAwesome.PLUS);
        addTypeDesignationButton.setDescription("Add a new type designation workingset");
        addTypeDesignationButton.setVisible(!isRegistrationLocked);
        addComponent(addTypeDesignationButton);

        //TODO make responsive and use specificIdentifier in case the space gets too narrow
        identifierLink = new Link(regDto.getIdentifier(), new ExternalResource(regDto.getIdentifier()));
        identifierLink.setEnabled(regDto.getStatus() == RegistrationStatus.PUBLISHED);

        addComponents(identifierLink);

        iterator().forEachRemaining(c -> addStyledComponent(c));
        addDefaultStyles();

    }

    public IdButton<TaxonName> getNameButton() {
        return nameIdButton;
    }

    public List<TypeDesignationWorkingSetButton> getTypeDesignationButtons() {
        return typeDesignationButtons;
    }

    public Button getAddTypeDesignationButton() {
        return addTypeDesignationButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        addStyleName(STYLE_NAMES);
        if(nameIdButton != null){
            nameIdButton.getButton().addStyleName(DEFAULT_BUTTON_STYLES);
        }
        if(nameLabel != null){
            nameLabel.addStyleName("v-disabled");
        }
        typeDesignationButtons.forEach(idb -> idb.getButton().addStyleName(DEFAULT_BUTTON_STYLES));
        addTypeDesignationButton.addStyleName(DEFAULT_BUTTON_STYLES);
    }

    public class TypeDesignationWorkingSetButton {
        private Integer id;
        private TypeDesignationWorkingSetType type;
        private Button button;

        public TypeDesignationWorkingSetButton(TypeDesignationWorkingSetType type, Integer id, Button button){
            this.type = type;
            this.id = id;
            this.button = button;
        }

        /**
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @return the button
         */
        public Button getButton() {
            return button;
        }

        /**
         * @return the type
         */
        public TypeDesignationWorkingSetType getType() {
            return type;
        }

    }

    public class IdButton<T> {
        private Integer id;
        private Class<T> entityType;
        private Button button;

        public IdButton(Class<T> type, Integer id, Button button){
            this.entityType = type;
            this.id = id;
            this.button = button;
        }

        /**
         * @return the id
         */
        public Integer getId() {
            return id;
        }

        /**
         * @return the button
         */
        public Button getButton() {
            return button;
        }

        /**
         * @return the type
         */
        public Class<T> getType() {
            return entityType;
        }

    }

}
