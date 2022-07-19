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
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationDTO;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSet;
import eu.etaxonomy.cdm.api.service.name.TypeDesignationSet.TypeDesignationSetType;
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
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationTermLists.RegistrationTypeDesignationStatusComparator;
import eu.etaxonomy.cdm.vaadin.permission.PermissionDebugUtils;
import eu.etaxonomy.vaadin.component.CompositeStyledComponent;

/**
 * @author a.kohlbecker
 * @since May 19, 2017
 */
public class RegistrationItemNameAndTypeButtons extends CompositeStyledComponent {

    private final static Logger logger = LogManager.getLogger();

    private static final String DEFAULT_BUTTON_STYLES = "";

    private static final long serialVersionUID = -5059163772392864050L;

    public static final String STYLE_NAMES = "edit-button-group  " + ValoTheme.LAYOUT_COMPONENT_GROUP;

    private IdButton<TaxonName> nameIdButton = null;

    private List<TypeDesignationSetButton> typeDesignationButtons = new ArrayList<>();

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
        Map<TypedEntityReference<? extends VersionableEntity>,TypeDesignationSet> typeDesignationSets = regDto.getOrderedTypeDesignationSets();

        if(typeDesignationSets != null){
            // order the typeDesignationSet keys so that holotypes come first, etc
            List<TypedEntityRefWithStatus> baseRefsByHighestStatus = new ArrayList<>();
            for(TypedEntityReference<? extends VersionableEntity> baseEntityRef : typeDesignationSets.keySet()) {
                baseRefsByHighestStatus.add(new TypedEntityRefWithStatus(baseEntityRef, typeDesignationSets.get(baseEntityRef).highestTypeStatus(new RegistrationTypeDesignationStatusComparator())));
            }

            Collections.sort(baseRefsByHighestStatus);

            for(TypedEntityRefWithStatus typedEntityRefWithStatus : baseRefsByHighestStatus) {
                TypedEntityReference<? extends VersionableEntity> baseEntity = typedEntityRefWithStatus.typedEntity;
                TypeDesignationSet typeDesignationSet = typeDesignationSets.get(baseEntity);
                if (logger.isDebugEnabled()) {logger.debug("WorkingSet:" + typeDesignationSet.getWorkingsetType() + ">" + typeDesignationSet.getBaseEntity().toString());}
                String buttonLabel = SpecimenOrObservationBase.class.isAssignableFrom(baseEntity.getType()) ? "Type": "NameType";
                Button tdButton = new Button(buttonLabel + ":");
                tdButton.setDescription("Edit the type designation working set");
                boolean userHasUpdatePermission = userHelper.userHasPermission(baseEntity.getType(), baseEntity.getUuid(), CRUD.UPDATE, CRUD.DELETE);
                editButtons.add(new ButtonWithUserEditPermission(tdButton, userHasUpdatePermission));
                addComponent(tdButton);

                PermissionDebugUtils.addGainPerEntityPermissionButton(this, SpecimenOrObservationBase.class,
                        baseEntity.getUuid(), EnumSet.of(CRUD.UPDATE, CRUD.DELETE), RegistrationStatus.PREPARATION.name());

                typeDesignationButtons.add(new TypeDesignationSetButton(
                        typeDesignationSet.getWorkingsetType(),
                        typeDesignationSet.getBaseEntity(),
                        tdButton)
                        );

                String labelText = "<span class=\"field-unit-label\">" + baseEntity.getLabel() + "</span>"; // renders the FieldUnit label
                for(TypeDesignationStatusBase<?> typeStatus : typeDesignationSet.keySet()){
                    Collection<TypeDesignationDTO> tdPerStatus = typeDesignationSet.get(typeStatus);
                    labelText += " <strong>" + typeStatus.getLabel() +  (tdPerStatus.size() > 1 ? "s":"" ) + "</strong>: ";
                    boolean isFirst = true;
                    for(TypeDesignationDTO<?> dtDTO : tdPerStatus) {
                        labelText += ( isFirst ? "" : ", ") + TaggedCacheHelper.createString(
                                TaggedCacheHelper.cropAt(dtDTO.getTaggedText(), TagEnum.separator, "designated\\s+[bB]y"));
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

    public List<TypeDesignationSetButton> getTypeDesignationButtons() {
        return typeDesignationButtons;
    }

    public Button getAddTypeDesignationButton() {
        return addTypeDesignationButton;
    }

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

    public class TypeDesignationSetButton {

        private VersionableEntity baseEntity;
        private TypeDesignationSetType type;
        private Button button;

        public TypeDesignationSetButton(TypeDesignationSetType type, VersionableEntity baseEntity, Button button){
            this.type = type;
            this.baseEntity = baseEntity;
            this.button = button;
        }

        public VersionableEntity getBaseEntity() {
            return baseEntity;
        }

        public Button getButton() {
            return button;
        }

        public TypeDesignationSetType getType() {
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

        public UUID getUuid() {
            return uuid;
        }

        public Button getButton() {
            return button;
        }

        public Class<T> getType() {
            return entityType;
        }
    }

    public class ButtonWithUserEditPermission {

        Button button;
        boolean userCanEdit;
        public ButtonWithUserEditPermission(Button button, boolean userCanEdit) {
            super();
            this.button = button;
            this.userCanEdit = userCanEdit;
        }
    }

    public boolean isRegistrationLocked() {
        return isRegistrationLocked;
    }

    public boolean isLockOverride() {
        return isLockOverride;
    }

    public void setLockOverride(boolean isLockOverride) {
        if(this.isLockOverride != isLockOverride){
            this.isLockOverride = isLockOverride;
            updateEditorButtonReadonlyStates();
        }
    }

    private class TypedEntityRefWithStatus implements Comparable<TypedEntityRefWithStatus> {

        public TypedEntityReference<? extends VersionableEntity> typedEntity;
        public TypeDesignationStatusBase<?> status;
        private RegistrationTypeDesignationStatusComparator comparator = new RegistrationTypeDesignationStatusComparator();

        public TypedEntityRefWithStatus(TypedEntityReference<? extends VersionableEntity> typedEntity,
                TypeDesignationStatusBase<?> status) {
            this.typedEntity = typedEntity;
            this.status = status;
        }

        @Override
        public int compareTo(TypedEntityRefWithStatus o) {
            return comparator.compare(this.status, o.status);
        }
    }
}