/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.util.RoleProberImpl;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.TaxonNameEditorAction;
import eu.etaxonomy.cdm.vaadin.permission.CdmEditDeletePermissionTester;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.converter.SetToListConverter;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 */
@SpringComponent
@Scope("prototype")
public class NameTypeDesignationPopupEditor
        extends AbstractCdmPopupEditor<NameTypeDesignation,NameTypeDesignationPresenter,NameTypeDesignationEditorView>
        implements NameTypeDesignationEditorView {

    private static final String TYPE_STATUS_OR_FLAG_MUST_BE_SET = "Either \"Type status\" must be set or any of the \"Conserved type\", \"Rejected type\" or \"Not designated\" flags must be set.";

    private static final String TYPE_STATUS_MUST_BE_SET = "\"Type status\" must be set.";

    private static final long serialVersionUID = 8233876984579344340L;

    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 7;


    private CheckBox conservedTypeField;
    private CheckBox rejectedTypeField;
    private CheckBox notDesignatedField;
    List<CheckBox> typeStatusFlagFields;

    private ToOneRelatedEntityCombobox<TaxonName> typeNameField;

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> typifiedNamesComboboxSelect;

    private NativeSelect typeStatusSelect;

    private ToOneRelatedEntityCombobox<Reference> designationReferenceCombobox;

    private TextField designationReferenceDetailField;

    private boolean showTypeFlags = true;

    private Optional<Boolean> inTypedesignationOnlyAct = Optional.empty();

    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;


    public NameTypeDesignationPopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), NameTypeDesignation.class);
    }

    @Override
    public String getWindowCaption() {
        return "Name type designation editor";
    }

    @Override
    public void focusFirst() {
        // none
    }

    @Override
    protected String getDefaultComponentStyles() {
        return "tiny";
    }

    @Override
    protected void initContent() {
        /*
            conservedType : boolean
            rejectedType : boolean
            typeName : TaxonName

            typifiedNames
            notDesignated : boolean
            registrations : Set<Registration>
            typeStatus : T

            citation : Reference
            citationMicroReference : String
            originalInfo : String
         */

        GridLayout grid = (GridLayout)getFieldLayout();
        // grid.setSizeFull();
        grid.setSpacing(true);
        grid.setColumnExpandRatio(0, 0.25f);
        grid.setColumnExpandRatio(1, 0.25f);
        grid.setColumnExpandRatio(2, 0.25f);
        grid.setColumnExpandRatio(3, 0.25f);

        int row = 0;

        if(showTypeFlags){
            conservedTypeField = addCheckBox("Conserved type", "conservedType", 0, row);
            conservedTypeField.addValueChangeListener(e -> updateDesignationReferenceFields());
            conservedTypeField.addValueChangeListener(e -> handleTypeStatusFlagChange(conservedTypeField));
            rejectedTypeField = addCheckBox("Rejected type", "rejectedType", 1, row);
            rejectedTypeField.addValueChangeListener(e -> updateDesignationReferenceFields());
            rejectedTypeField.addValueChangeListener(e -> handleTypeStatusFlagChange(rejectedTypeField));
            notDesignatedField = addCheckBox("Not designated", "notDesignated", 2, row);
            notDesignatedField.addValueChangeListener(e -> updateDesignationReferenceFields());
            notDesignatedField.addValueChangeListener(e -> handleTypeStatusFlagChange(notDesignatedField));
            notDesignatedField.addValueChangeListener(e -> updateTypeNameField());
            typeStatusFlagFields = Arrays.asList(conservedTypeField, rejectedTypeField, notDesignatedField);
            row++;
        }

        typeStatusSelect = new NativeSelect("Type status");
        typeStatusSelect.setNullSelectionAllowed(false);
        typeStatusSelect.setWidth(100, Unit.PERCENTAGE);
        typeStatusSelect.setRequired(true);
        typeStatusSelect.setRequiredError(TYPE_STATUS_OR_FLAG_MUST_BE_SET);
        addField(typeStatusSelect, "typeStatus", 0, row, 1, row);
        grid.setComponentAlignment(typeStatusSelect, Alignment.TOP_RIGHT);
        typeStatusSelect.addValueChangeListener(e -> {
            updateDesignationReferenceFields();
        });

        row++;
        typeNameField = new ToOneRelatedEntityCombobox<TaxonName>("Type name", TaxonName.class);
        addField(typeNameField, "typeName", 0, row, 3, row);
        typeNameField.addClickListenerAddEntity(e -> getViewEventBus().publish(this,
                new TaxonNameEditorAction(
                        EditorActionType.ADD,
                        e.getButton(),
                        typeNameField,
                        this))
        );
        typeNameField.addClickListenerEditEntity(e -> {
            if(typeNameField.getValue() != null){
                getViewEventBus().publish(this,
                    new TaxonNameEditorAction(
                            EditorActionType.EDIT,
                            typeNameField.getValue().getUuid(),
                            e.getButton(),
                            typeNameField,
                            this)
                );
            }
        });

        row++;
        typifiedNamesComboboxSelect = new ToManyRelatedEntitiesComboboxSelect<>(TaxonName.class, "Typified names");
        typifiedNamesComboboxSelect.setConverter(new SetToListConverter<>());
        typifiedNamesComboboxSelect.setEditPermissionTester(new CdmEditDeletePermissionTester());
        addField(typifiedNamesComboboxSelect, "typifiedNames", 0, row, 3, row);
        typifiedNamesComboboxSelect.setReadOnly(false); // FIXME this does not help, see #7389

        row++;
        designationReferenceCombobox = new ToOneRelatedEntityCombobox<Reference>("Designation reference", Reference.class);
        addField(designationReferenceCombobox, "citation", 0, row, 2, row);
        designationReferenceCombobox.setWidth(400, Unit.PIXELS);
        designationReferenceDetailField = addTextField("Reference detail", "citationMicroReference", 3, row);
        designationReferenceCombobox.addClickListenerAddEntity(e -> getViewEventBus().publish(
                this,
                new ReferenceEditorAction(EditorActionType.ADD, null, designationReferenceCombobox, this)
                ));
        designationReferenceCombobox.addClickListenerEditEntity(e -> {
            if(designationReferenceCombobox.getValue() != null){
                getViewEventBus().publish(this,
                    new ReferenceEditorAction(
                            EditorActionType.EDIT,
                            designationReferenceCombobox.getValue().getUuid(),
                            e.getButton(),
                            designationReferenceCombobox,
                            this)
                );
            }
            });

        row++;
        annotationsListField = new FilterableAnnotationsField("Editorial notes");
        annotationsListField.setWidth(100, Unit.PERCENTAGE);
        boolean isCurator = UserHelperAccess.userHelper().userIs(new RoleProberImpl(RolesAndPermissions.ROLE_CURATION));
        boolean isAdmin = UserHelperAccess.userHelper().userIsAdmin();
        if(isCurator || isAdmin){
            annotationsListField.withNewButton(true);
        } else {
            annotationsListField.setAnnotationTypesVisible(editableAnotationTypes);
        }
        addField(annotationsListField, "annotations", 0, row, 3, row);
    }

    @Override
    protected void afterItemDataSourceSet() {
        super.afterItemDataSourceSet();
        updateDesignationReferenceFields();
    }

    protected void updateDesignationReferenceFields() {
        boolean hasDesignationSource = typeStatusSelect.getValue() != null && ((NameTypeDesignationStatus)typeStatusSelect.getValue()).hasDesignationSource();
        designationReferenceDetailField.setVisible(hasDesignationSource);
        designationReferenceCombobox.setVisible(hasDesignationSource);
        designationReferenceCombobox.setRequired(hasDesignationSource);
        // NOTE: For better usability we only hide these fields here,
        // NameTypeDesignationPresenter.preSaveBean(NameTypeDesignation bean) will empty them in needed

        boolean isInTypedesignationOnlyAct = !isInTypedesignationOnlyAct().isPresent() || isInTypedesignationOnlyAct().get();
        boolean typeStatusRequired = !(typeStatusFlagFields.stream().anyMatch(cb -> cb.getValue().booleanValue()));
        // need to check for isInTypedesignationOnlyAct also, otherwise the reference field will not show up
        // and the type designation might not be associated with the registration
        // TODO discuss with Henning
        typeStatusSelect.setRequired(typeStatusRequired || checkInTypeDesignationOnlyAct());
        if(typeStatusRequired && checkInTypeDesignationOnlyAct()) {
            designationReferenceCombobox.setRequiredError(TYPE_STATUS_MUST_BE_SET);
        } else {
            designationReferenceCombobox.setRequiredError(TYPE_STATUS_OR_FLAG_MUST_BE_SET);
        }
    }

    protected void updateTypeNameField() {
        typeNameField.setVisible(!notDesignatedField.getValue().booleanValue());
    }

    protected void handleTypeStatusFlagChange(CheckBox typeStatusFlagField) {
        if(typeStatusFlagField.getValue().booleanValue()) {
            typeStatusFlagFields.stream().filter(cb -> !cb.equals(typeStatusFlagField)).forEach(cb -> cb.setValue(false));
        }
    }

    @Override
    public ToOneRelatedEntityCombobox<TaxonName> getTypeNameField() {
        return typeNameField;
    }

    @Override
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getTypifiedNamesComboboxSelect() {
        return typifiedNamesComboboxSelect;
    }

    @Override
    public NativeSelect getTypeStatusSelect() {
        return typeStatusSelect;
    }

    @Override
    public ToOneRelatedEntityCombobox<Reference> getDesignationReferenceCombobox() {
        return designationReferenceCombobox;
    }

    @Override
    public boolean isShowTypeFlags() {
        return showTypeFlags;
    }

    @Override
    public void setShowTypeFlags(boolean showTypeFlags) {
        this.showTypeFlags = showTypeFlags;
    }

    @Override
    public AnnotationType[] getEditableAnotationTypes() {
        return editableAnotationTypes;
    }


    @Override
    public void setEditableAnotationTypes(AnnotationType... editableAnotationTypes) {
        this.editableAnotationTypes = editableAnotationTypes;
    }


    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }

    @Override
    public void setInTypedesignationOnlyAct(Optional<Boolean> isInTypedesignationOnlyAct) {
        this.inTypedesignationOnlyAct = isInTypedesignationOnlyAct;
    }

    @Override
    public Optional<Boolean> isInTypedesignationOnlyAct() {
        return inTypedesignationOnlyAct;
    }
}
