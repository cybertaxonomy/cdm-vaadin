/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Collection;
import java.util.EnumSet;

import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;
import org.vaadin.viritin.fields.ElementCollectionField;

import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowRepresentative;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.GeoLocationField;
import eu.etaxonomy.cdm.vaadin.component.common.MinMaxTextField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.TimePeriodField;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;
import eu.etaxonomy.cdm.vaadin.util.TeamOrPersonBaseCaptionGenerator;
import eu.etaxonomy.cdm.vaadin.util.converter.DoubleConverter;
import eu.etaxonomy.cdm.vaadin.util.converter.IntegerConverter;
import eu.etaxonomy.cdm.vaadin.view.PerEntityAuthorityGrantingEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class SpecimenTypeDesignationWorkingsetPopupEditor
    extends AbstractPopupEditor<SpecimenTypeDesignationWorkingSetDTO, SpecimenTypeDesignationWorkingsetEditorPresenter>
    implements SpecimenTypeDesignationWorkingsetPopupEditorView, AccessRestrictedView, PerEntityAuthorityGrantingEditor {
    /**
     *
     */
    private static final String CAN_T_SAVE_AS_LONG_AS_TYPE_DESIGNATIONS_ARE_MISSING = "Can't save as long as type designations are missing.";

    /**
     * @param layout
     * @param dtoType
     */
    public SpecimenTypeDesignationWorkingsetPopupEditor() {
        super(new GridLayout(), SpecimenTypeDesignationWorkingSetDTO.class);
        GridLayout grid = (GridLayout) getFieldLayout();
        grid.setMargin(true);
        grid.setSpacing(true);
    }

    private static final long serialVersionUID = 5418275817834009509L;

    private ListSelect countrySelectField;

    private ElementCollectionField<SpecimenTypeDesignationDTO> typeDesignationsCollectionField;

    private EnumSet<CRUD> crud;

    private TeamOrPersonField collectorField;

    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;

    private GeoLocationField exactLocationField;

    /**
     * @return the countrySelectField
     */
    @Override
    public ListSelect getCountrySelectField() {
        return countrySelectField;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initContent() {

        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSpacing(true);
        grid.setMargin(true);
        grid.setColumns(3);
        grid.setRows(10);

        //TODO typifyingAuthors

        // FieldUnit + GatheringEvent

        int row = 0;
        countrySelectField = new ListSelect("Country");
        addField(countrySelectField, "country", 1, row , 2, row);
        countrySelectField.setWidth("100%");
        countrySelectField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        countrySelectField.setItemCaptionPropertyId("label");
        countrySelectField.setRows(1);

        row++;
        TextArea localityField = new TextArea("Locality");
        localityField.setNullRepresentation("");
        addField(localityField, "locality", 0, row , 2, row);
        localityField.setWidth("100%");
        // NOTE: setRows and SetCold breaks he width setting,
        //        see https://github.com/vaadin/framework/issues/3617

        row++;
        exactLocationField = new GeoLocationField("Geo location");
        addField(exactLocationField, "exactLocation", 0, row, 2, row);
        exactLocationField.setWidth("100%");

        row++;
        MinMaxTextField absElevationMinMax = new MinMaxTextField("Altitude", "m");
        absElevationMinMax.setWidth("100%");
        absElevationMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(absElevationMinMax, 0, row, 2, row);

        bindField(absElevationMinMax.getMinField(), "absoluteElevation");
        bindField(absElevationMinMax.getMaxField(), "absoluteElevationMax");
        bindField(absElevationMinMax.getTextField(), "absoluteElevationText");

        absElevationMinMax.getMaxField().setConverter(new IntegerConverter());
        absElevationMinMax.getMinField().setConverter(new IntegerConverter());

        row++;
        MinMaxTextField distanceToWaterSurfaceMinMax = new MinMaxTextField("Distance to water surface", "m");
        distanceToWaterSurfaceMinMax.setWidth("100%");
        distanceToWaterSurfaceMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(distanceToWaterSurfaceMinMax, 0, row, 2, row);

        bindField(distanceToWaterSurfaceMinMax.getMinField(), "distanceToWaterSurface");
        bindField(distanceToWaterSurfaceMinMax.getMaxField(), "distanceToWaterSurfaceMax");
        bindField(distanceToWaterSurfaceMinMax.getTextField(), "distanceToWaterSurfaceText");
        distanceToWaterSurfaceMinMax.getMaxField().setConverter(new DoubleConverter());
        distanceToWaterSurfaceMinMax.getMinField().setConverter(new DoubleConverter());
        distanceToWaterSurfaceMinMax.getMaxField().addValidator(new DoubleRangeValidator("Negative values are not allowed here.", 0.0, Double.MAX_VALUE));
        distanceToWaterSurfaceMinMax.getMinField().addValidator(new DoubleRangeValidator("Negative values are not allowed here.", 0.0, Double.MAX_VALUE));

        row++;
        MinMaxTextField distanceToGroundMinMax = new MinMaxTextField("Distance to substrate", "m");
        distanceToGroundMinMax.setWidth("100%");
        distanceToGroundMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(distanceToGroundMinMax, 0, row, 2, row);

        bindField(distanceToGroundMinMax.getMinField(), "distanceToGround");
        bindField(distanceToGroundMinMax.getMaxField(), "distanceToGroundMax");
        bindField(distanceToGroundMinMax.getTextField(), "distanceToGroundText");
        distanceToGroundMinMax.getMaxField().setConverter(new DoubleConverter());
        distanceToGroundMinMax.getMinField().setConverter(new DoubleConverter());

        row++;
        collectorField = new TeamOrPersonField("Collector", TeamOrPersonBaseCaptionGenerator.CacheType.COLLECTOR_TITLE);
        addField(collectorField, "collector", 0, row, 2, row);

        row++;

        TimePeriodField collectionDateField = new TimePeriodField("Collection date");
        // collectionDateField.setInputPrompt("dd.mm.yyyy");
        addField(collectionDateField, "gatheringDate", 0, row, 1, row);
        addTextField("Field number", "fieldNumber", 2, row);


        row++;

        // FIXME: can we use the Grid instead?
        typeDesignationsCollectionField = new ElementCollectionField<>(
                SpecimenTypeDesignationDTO.class,
                SpecimenTypeDesignationDTORow.class
                );
        typeDesignationsCollectionField.withCaption("Types");
        typeDesignationsCollectionField.getLayout().setSpacing(false);
        typeDesignationsCollectionField.getLayout().setColumns(3);

        typeDesignationsCollectionField.setVisibleProperties(SpecimenTypeDesignationDTORow.visibleFields());

        typeDesignationsCollectionField.setPropertyHeader("accessionNumber", "Access. num.");
        typeDesignationsCollectionField.setPropertyHeader("preferredStableUri", "Stable URI");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReference", "Image reference");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReferenceDetail", "Reference detail");
        typeDesignationsCollectionField.addElementAddedListener( e -> updateAllowSave());
        typeDesignationsCollectionField.addElementRemovedListener( e -> updateAllowSave());

        // typeDesignationsCollectionField.getLayout().setMargin(false);
        // typeDesignationsCollectionField.addStyleName("composite-field-wrapper");
        // addField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs", 0, row, 2, row);

        Panel scrollPanel = new Panel(typeDesignationsCollectionField.getLayout());
        scrollPanel.setCaption("Types");
        scrollPanel.setWidth(800, Unit.PIXELS);

        bindField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs");
        addComponent(scrollPanel, 0, row, 2, row);

        row++;
        annotationsListField = new FilterableAnnotationsField("Editorial notes");
        annotationsListField.setWidth(100, Unit.PERCENTAGE);
        annotationsListField.setAnnotationTypesVisible(editableAnotationTypes);
        addField(annotationsListField, "annotations", 0, row, 2, row);

     }


    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Specimen typedesignations editor";
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isResizable() {
        return true;
    }


    // ------- SpecimenTypeDesignationWorkingsetPopupEditorView methods ---- //
    @Override
    public ElementCollectionField<SpecimenTypeDesignationDTO> getTypeDesignationsCollectionField() {
        return typeDesignationsCollectionField;
    }

    @Override
    public void applyDefaultComponentStyle(Component ... components){
        for(int i = 0; i <components.length; i++){
            components[i].setStyleName(getDefaultComponentStyles());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void grantToCurrentUser(EnumSet<CRUD> crud) {
        getPresenter().setGrantsForCurrentUser(crud);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void afterItemDataSourceSet() {
        super.afterItemDataSourceSet();
        GridLayout gridLayout = this.typeDesignationsCollectionField.getLayout();
        for(int rowIndex = 1; rowIndex < gridLayout.getRows(); rowIndex++){ // first row is header
            Component item = gridLayout.getComponent(SpecimenTypeDesignationDTORow.rowListSelectColumn(), rowIndex);
            ((CollectionRowRepresentative)item).updateRowItemsEnabledStates();
        }
        updateAllowDelete();
        updateAllowSave();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAllowDelete(){
        // disable the delete button if there is only one typeDesignation
        // if this typeDesignation is deleted the fieldUnit would become orphan in the
        // TypeDesignationWorkingSet
        GridLayout gridLayout = this.typeDesignationsCollectionField.getLayout();
        if(gridLayout.getRows() == 3){ // first row is header, last row is next new item
            gridLayout.getComponent(gridLayout.getColumns() - 1, 1).setEnabled(false);
        }
    }

    public void updateAllowSave(){
        boolean hasTypeDesignations = getBean().getSpecimenTypeDesignationDTOs().size() > 0;
        setSaveButtonEnabled(hasTypeDesignations);
        if(!hasTypeDesignations){
            addStatusMessage(CAN_T_SAVE_AS_LONG_AS_TYPE_DESIGNATIONS_ARE_MISSING);
        } else {
            removeStatusMessage(CAN_T_SAVE_AS_LONG_AS_TYPE_DESIGNATIONS_ARE_MISSING);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        getFieldLayout().iterator().forEachRemaining(c -> c.setReadOnly(readOnly));
        collectorField.setEditorReadOnly(readOnly);
        typeDesignationsCollectionField.getLayout().iterator().forEachRemaining(c -> c.setReadOnly(readOnly));

    }

    /**
     * @return the collectorField
     */
    @Override
    public TeamOrPersonField getCollectorField() {
        return collectorField;
    }

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     *
     * @return the editableAnotationTypes
     */
    @Override
    public AnnotationType[] getEditableAnotationTypes() {
        return editableAnotationTypes;
    }

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     *
     *
     * @param editableAnotationTypes the editableAnotationTypes to set
     */
    @Override
    public void setEditableAnotationTypes(AnnotationType ... editableAnotationTypes) {
        this.editableAnotationTypes = editableAnotationTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }

    @Override
    public GeoLocationField getExactLocationField() {
        return exactLocationField;
    }



}
