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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationWorkingSet.TypeDesignationWorkingSetType;
import eu.etaxonomy.cdm.api.util.UserHelper;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.strategy.cache.TagEnum;
import eu.etaxonomy.cdm.strategy.cache.TaggedCacheHelper;
import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.permission.PermissionDebugUtils;
import eu.etaxonomy.vaadin.component.CompositeStyledComponent;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 */
public class RegistrationItemNameAndTypeButtons extends CompositeStyledComponent {

    private final static Logger logger = Logger.getLogger(RegistrationItemNameAndTypeButtons.class);


    private static final String DEFAULT_BUTTON_STYLES = "";

    private static final long serialVersionUID = -5059163772392864050L;

    public static final String STYLE_NAMES = "edit-button-group  " + ValoTheme.LAYOUT_COMPONENT_GROUP;

    private IdButton<TaxonName> nameIdButton = null;

    private List<TypeDesignationWorkingSetButton> typeDesignationButtons = new ArrayList<>();

    private List<Label> labels = new ArrayList<>();

    private List<ButtonWithUserEditPermission> editButtons = new ArrayList<>();

    private Button addTypeDesignationButton;

    private Label nameLabel = null;

    private Link identifierLink;

    private boolean isRegistrationLocked;

    private boolean isLockOverride;

    public RegistrationItemNameAndTypeButtons(RegistrationDTO regDto, ICdmEntityUuidCacher entitiyCacher) {

        isRegistrationLocked = EnumSet.of(
                RegistrationStatus.PUBLISHED, RegistrationStatus.REJECTED)
                .contains(regDto.getStatus());

        setWidth(100, Unit.PERCENTAGE);

        UserHelper userHelper;
        if(entitiyCacher != null){
            userHelper = UserHelperAccess.userHelper().withCache(entitiyCacher);
        } else {
            userHelper = UserHelperAccess.userHelper();
        }

        if(regDto.getNameRef() != null){
            Button nameButton = new Button("Name:");
            nameButton.setDescription("Edit the Name");
            nameIdButton = new IdButton<TaxonName>(TaxonName.class, regDto.getNameRef().getUuid(), nameButton);
            Label nameLabel = new Label(regDto.getNameRef().getLabel());
            nameLabel.setWidthUndefined();
            boolean userHasPermission = userHelper.userHasPermission(regDto.registration().getName(), CRUD.UPDATE);
            editButtons.add(new ButtonWithUserEditPermission(nameButton, userHasPermission));

            addComponent(nameIdButton.getButton());
            PermissionDebugUtils.addGainPerEntityPermissionButton(this, TaxonName.class, regDto.getNameRef().getUuid(),
                    EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
            addComponent(nameLabel);
        } else {
            // no name in the registration! we only show the typified name as label
            if(regDto.getTypifiedNameRef() != null){
                nameLabel = new Label(regDto.getTypifiedNameRef().getLabel());
                addComponent(nameLabel);
            }
        }
        boolean userHasAddPermission = !regDto.isPersisted() || userHelper.userHasPermission(regDto.registration(), CRUD.UPDATE);
        LinkedHashMap<TypedEntityReference<? extends VersionableEntity>, TypeDesignationWorkingSet> typeDesignationworkingSets = regDto.getOrderedTypeDesignationWorkingSets();
        if(typeDesignationworkingSets != null){
            for(TypedEntityReference<? extends VersionableEntity> baseEntityRef : typeDesignationworkingSets.keySet()) {
                TypeDesignationWorkingSet typeDesignationWorkingSet = typeDesignationworkingSets.get(baseEntityRef);
                logger.debug("WorkingSet:" + typeDesignationWorkingSet.getWorkingsetType() + ">" + typeDesignationWorkingSet.getBaseEntityReference());
                String buttonLabel = SpecimenOrObservationBase.class.isAssignableFrom(baseEntityRef.getType()) ? "Type": "NameType";
                Button tdButton = new Button(buttonLabel + ":");
                tdButton.setDescription("Edit the type designation working set");
                boolean userHasUpdatePermission = userHelper.userHasPermission(baseEntityRef.getType(), baseEntityRef.getUuid(), CRUD.UPDATE, CRUD.DELETE);
                editButtons.add(new ButtonWithUserEditPermission(tdButton, userHasUpdatePermission));
                addComponent(tdButton);

                PermissionDebugUtils.addGainPerEntityPermissionButton(this, SpecimenOrObservationBase.class,
                        baseEntityRef.getUuid(), EnumSet.of(CRUD.UPDATE, CRUD.DELETE), RegistrationStatus.PREPARATION.name());

                typeDesignationButtons.add(new TypeDesignationWorkingSetButton(
                        typeDesignationWorkingSet.getWorkingsetType(),
                        typeDesignationWorkingSet.getBaseEntityReference(),
                        tdButton)
                        );

                String labelText = "<span class=\"field-unit-label\">" + baseEntityRef.getLabel() + "</span>"; // renders the FieldUnit label
                for(TypeDesignationStatusBase<?> typeStatus : typeDesignationWorkingSet.keySet()){
                    labelText += " <strong>" + typeStatus.getLabel() +  (typeDesignationWorkingSet.getTypeDesignations().size() > 1 ? "s":"" ) + "</strong>: ";
                    boolean isFirst = true;
                    for(TypeDesignationDTO<?> dtDTO : typeDesignationWorkingSet.getTypeDesignations()) {
                        labelText += ( isFirst ? "" : ", ") + TaggedCacheHelper.createString(dtDTO.getTaggedText(), EnumSet.of(TagEnum.reference, TagEnum.separator)); // TagEnum.separator removes "designated By"
                        isFirst = false;
                    }

                }

                Label label = new Label(labelText, ContentMode.HTML);
                label.setWidthUndefined();
                addComponent(label);
                labels.add(label);
            }
        }
        addTypeDesignationButton = ButtonFactory.ADD_ITEM.createButton();
        addTypeDesignationButton.setDescription("Add a new type designation workingset.");
        addTypeDesignationButton.setVisible(!isRegistrationLocked && userHasAddPermission);
        addComponent(addTypeDesignationButton);

        //TODO make responsive and use specificIdentifier in case the space gets too narrow
        if(regDto.isPersisted()){
            identifierLink = new Link(regDto.getIdentifier(), new ExternalResource(regDto.getIdentifier()));
            identifierLink.setEnabled(regDto.getStatus() == RegistrationStatus.PUBLISHED);
            addComponents(identifierLink);
        }

        iterator().forEachRemaining(c -> addStyledComponent(c));
        updateEditorButtonReadonlyStates();
        addDefaultStyles();

    }


    private void updateEditorButtonReadonlyStates() {
        for(ButtonWithUserEditPermission b : editButtons){
            boolean impossibleToUnlock = !b.userCanEdit && isLockOverride && isRegistrationLocked;
            b.button.setReadOnly((isRegistrationLocked && !isLockOverride) || !b.userCanEdit);
            b.button.setEnabled(!impossibleToUnlock);
            b.button.setDescription(impossibleToUnlock ? "Unlock failed due to missing permissions!" : "");
            b.button.setIcon(isLockOverride ? FontAwesome.UNLOCK_ALT : null);
        }

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

        private TypedEntityReference baseEntityRef;
        private TypeDesignationWorkingSetType type;
        private Button button;

        public TypeDesignationWorkingSetButton(TypeDesignationWorkingSetType type, TypedEntityReference baseEntityRef, Button button){
            this.type = type;
            this.baseEntityRef = baseEntityRef;
            this.button = button;
        }

        /**
         * @return the id
         */
        public TypedEntityReference getBaseEntity() {
            return baseEntityRef;
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
        private UUID uuid;
        private Class<T> entityType;
        private Button button;

        public IdButton(Class<T> type, UUID uuid, Button button){
            this.entityType = type;
            this.uuid = uuid;
            this.button = button;
        }

        /**
         * @return the id
         */
        public UUID getUuid() {
            return uuid;
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

    public class ButtonWithUserEditPermission {

        Button button;
        boolean userCanEdit;
        /**
         * @param button
         * @param userCanEdit
         */
        public ButtonWithUserEditPermission(Button button, boolean userCanEdit) {
            super();
            this.button = button;
            this.userCanEdit = userCanEdit;
        }



    }
    public boolean isRegistrationLocked() {
        return isRegistrationLocked;
    }


    /**
     * @return the isLockOverride
     */
    public boolean isLockOverride() {
        return isLockOverride;
    }

    /**
     * @param isLockOverride the isLockOverride to set
     */
    public void setLockOverride(boolean isLockOverride) {
        if(this.isLockOverride != isLockOverride){
            this.isLockOverride = isLockOverride;
            updateEditorButtonReadonlyStates();
        }
    }

}
