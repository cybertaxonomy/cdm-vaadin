/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.utility.RoleProber;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
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
public class NameTypeDesignationPopupEditor extends AbstractCdmPopupEditor<NameTypeDesignation, NameTypeDesignationPresenter>
    implements NameTypeDesignationEditorView {

    private static final int GRID_COLS = 4;
    private static final int GRID_ROWS = 7;

    private CheckBox conservedTypeField;
    private CheckBox rejectedTypeField;
    private CheckBox notDesignatedField;

    private ToOneRelatedEntityCombobox<TaxonName> typeNameField;

    private ToManyRelatedEntitiesComboboxSelect<TaxonName> typifiedNamesComboboxSelect;

    private NativeSelect typeStatusSelect;

    private ToOneRelatedEntityCombobox<Reference> citationCombobox;

    private TextField citationDetailField;

    private boolean showTypeFlags = true;


    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;


    /**
     * @param layout
     * @param dtoType
     */
    public NameTypeDesignationPopupEditor() {
        super(new GridLayout(GRID_COLS, GRID_ROWS), NameTypeDesignation.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Name type designation editor";
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
            originalNameString : String
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
            rejectedTypeField = addCheckBox("Rejected type", "rejectedType", 1, row);
            notDesignatedField = addCheckBox("Not designated", "notDesignated", 2, row);
            row++;
        }

        typeStatusSelect = new NativeSelect("Type status");
        typeStatusSelect.setNullSelectionAllowed(false);
        typeStatusSelect.setWidth(100, Unit.PERCENTAGE);
        addField(typeStatusSelect, "typeStatus", 0, row, 1, row);
        grid.setComponentAlignment(typeStatusSelect, Alignment.TOP_RIGHT);

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
        typifiedNamesComboboxSelect = new ToManyRelatedEntitiesComboboxSelect<TaxonName>(TaxonName.class, "Typified names");
        typifiedNamesComboboxSelect.setConverter(new SetToListConverter<TaxonName>());
        typifiedNamesComboboxSelect.setEditPermissionTester(new CdmEditDeletePermissionTester());
        addField(typifiedNamesComboboxSelect, "typifiedNames", 0, row, 3, row);
        typifiedNamesComboboxSelect.setReadOnly(false); // FIXME this does not help, see #7389

        row++;
        citationCombobox = new ToOneRelatedEntityCombobox<Reference>("Citation", Reference.class);
        addField(citationCombobox, "citation", 0, row, 2, row);
        citationCombobox.setWidth(400, Unit.PIXELS);
        citationDetailField = addTextField("Citation detail", "citationMicroReference", 3, row);

        row++;
        annotationsListField = new FilterableAnnotationsField("Editorial notes");
        annotationsListField.setWidth(100, Unit.PERCENTAGE);
        boolean isCurator = UserHelperAccess.userHelper().userIs(new RoleProber(RolesAndPermissions.ROLE_CURATION));
        boolean isAdmin = UserHelperAccess.userHelper().userIsAdmin();
        if(isCurator || isAdmin){
            annotationsListField.withNewButton(true);
        } else {
            annotationsListField.setAnnotationTypesVisible(editableAnotationTypes);
        }
        addField(annotationsListField, "annotations", 0, row, 3, row);
    }

    /**
     * @return the typeNameField
     */
    @Override
    public ToOneRelatedEntityCombobox<TaxonName> getTypeNameField() {
        return typeNameField;
    }

    /**
     * @return the typifiedNamesComboboxSelect
     */
    @Override
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getTypifiedNamesComboboxSelect() {
        return typifiedNamesComboboxSelect;
    }

    /**
     * @return the typeStatusSelect
     */
    @Override
    public NativeSelect getTypeStatusSelect() {
        return typeStatusSelect;
    }

    /**
     * @return the citationCombobox
     */
    @Override
    public ToOneRelatedEntityCombobox<Reference> getCitationCombobox() {
        return citationCombobox;
    }


    /**
     * @return the showTypeFlags
     */
    @Override
    public boolean isShowTypeFlags() {
        return showTypeFlags;
    }

    /**
     * @param showTypeFlags the showTypeFlags to set
     */
    @Override
    public void setShowTypeFlags(boolean showTypeFlags) {
        this.showTypeFlags = showTypeFlags;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnnotationType[] getEditableAnotationTypes() {
        return editableAnotationTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditableAnotationTypes(AnnotationType... editableAnotationTypes) {
        this.editableAnotationTypes = editableAnotationTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }
}
