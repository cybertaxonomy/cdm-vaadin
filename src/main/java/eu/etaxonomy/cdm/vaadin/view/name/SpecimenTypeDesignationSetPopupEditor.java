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

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;

import eu.etaxonomy.cdm.api.util.RoleProberImpl;
import eu.etaxonomy.cdm.model.common.AnnotationType;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowRepresentative;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.GeoLocationField;
import eu.etaxonomy.cdm.vaadin.component.common.MinMaxTextField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.component.common.TimePeriodField;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationSetDTO;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.permission.RolesAndPermissions;
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
 * TODO as subclass of AbstractCdmPopupEditor?
 */
@SpringComponent
@Scope("prototype")
public class SpecimenTypeDesignationSetPopupEditor
    extends AbstractPopupEditor<SpecimenTypeDesignationSetDTO, SpecimenTypeDesignationSetEditorPresenter>
    implements SpecimenTypeDesignationSetPopupEditorView, AccessRestrictedView, PerEntityAuthorityGrantingEditor {

    private static final String CAN_T_SAVE_AS_LONG_AS_TYPE_DESIGNATIONS_ARE_MISSING = "Can't save as long as type designations are missing.";

    public SpecimenTypeDesignationSetPopupEditor() {
        super(new GridLayout(), SpecimenTypeDesignationSetDTO.class);
        GridLayout grid = (GridLayout) getFieldLayout();
        grid.setMargin(true);
        grid.setSpacing(true);
    }

    private static final long serialVersionUID = 5418275817834009509L;

    private NativeSelect countrySelectField;

    private ElementCollectionField<SpecimenTypeDesignationDTO> typeDesignationsCollectionField;

    private TeamOrPersonField collectorField;

    private FilterableAnnotationsField annotationsListField;

    private AnnotationType[] editableAnotationTypes = RegistrationUIDefaults.EDITABLE_ANOTATION_TYPES;

    private GeoLocationField exactLocationField;

    private Panel typeDesignationsScrollPanel;

    private String accessDeniedMessage;


    @Override
    public NativeSelect getCountrySelectField() {
        return countrySelectField;
    }

    @Override
    protected void initContent() {

        GridLayout grid = (GridLayout)getFieldLayout();
        grid.setSpacing(true);
        grid.setMargin(true);
        grid.setColumns(3);
        grid.setRows(10);
        int endColumnIndex = 2;

        //TODO typifyingAuthors

        // FieldUnit + GatheringEvent

        int row = 0;
        countrySelectField = new NativeSelect("Country");
        addField(countrySelectField, "country", 1, row , endColumnIndex, row);
        countrySelectField.setWidth("100%");
        countrySelectField.setItemCaptionMode(ItemCaptionMode.PROPERTY);
        countrySelectField.setItemCaptionPropertyId("label");

        row++;
        TextArea localityField = new TextArea("Locality");
        localityField.setNullRepresentation("");
        addField(localityField, "locality", 0, row , endColumnIndex, row);
        localityField.setWidth("100%");
        // NOTE: setRows and SetCold breaks he width setting,
        //        see https://github.com/vaadin/framework/issues/3617

        row++;
        exactLocationField = new GeoLocationField("Geo location");
        addField(exactLocationField, "exactLocation", 0, row, endColumnIndex, row);
        exactLocationField.setWidth("100%");

        row++;
        MinMaxTextField absElevationMinMax = new MinMaxTextField("Altitude", "m");
        absElevationMinMax.setWidth("100%");
        absElevationMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(absElevationMinMax, 0, row, endColumnIndex, row);

        bindField(absElevationMinMax.getMinField(), "absoluteElevation");
        bindField(absElevationMinMax.getMaxField(), "absoluteElevationMax");
        bindField(absElevationMinMax.getTextField(), "absoluteElevationText");

        absElevationMinMax.getMaxField().setConverter(new IntegerConverter());
        absElevationMinMax.getMinField().setConverter(new IntegerConverter());

        row++;
        MinMaxTextField distanceToWaterSurfaceMinMax = new MinMaxTextField("Distance to water surface", "m");
        distanceToWaterSurfaceMinMax.setWidth("100%");
        distanceToWaterSurfaceMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(distanceToWaterSurfaceMinMax, 0, row, endColumnIndex, row);

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
        grid.addComponent(distanceToGroundMinMax, 0, row, endColumnIndex, row);

        bindField(distanceToGroundMinMax.getMinField(), "distanceToGround");
        bindField(distanceToGroundMinMax.getMaxField(), "distanceToGroundMax");
        bindField(distanceToGroundMinMax.getTextField(), "distanceToGroundText");
        distanceToGroundMinMax.getMaxField().setConverter(new DoubleConverter());
        distanceToGroundMinMax.getMinField().setConverter(new DoubleConverter());

        row++;
        collectorField = new TeamOrPersonField("Collector", TeamOrPersonBaseCaptionGenerator.CacheType.COLLECTOR_TITLE);
        addField(collectorField, "collector", 0, row, endColumnIndex, row);

        row++;

        TimePeriodField collectionDateField = new TimePeriodField("Collection date");
        // collectionDateField.setInputPrompt("dd.mm.yyyy");
        addField(collectionDateField, "gatheringDate", 0, row, 1, row);
        addTextField("Field number", "fieldNumber", endColumnIndex, row);


        row++;

        // FIXME: can we use the Grid instead?
        typeDesignationsCollectionField = new ElementCollectionField<SpecimenTypeDesignationDTO>(
                SpecimenTypeDesignationDTO.class,
                SpecimenTypeDesignationDTORow.class
                ){

                    @Override
                    public void commit() throws SourceException, InvalidValueException {
                        validate(); // validate always so that empty rows are recognized
                        super.commit();
                    }

                    @Override
                    public boolean isEmpty() {
                        Collection value = getValue();
                        return value == null || value.isEmpty() ;
                    }

                    @Override
                    public void setComponentError(ErrorMessage componentError) {
                        typeDesignationsScrollPanel.setComponentError(componentError);
                    }

        };
        typeDesignationsCollectionField.withCaption("Types");
        typeDesignationsCollectionField.getLayout().setSpacing(false);
        typeDesignationsCollectionField.getLayout().setColumns(3);
        typeDesignationsCollectionField.setRequired(true); // only works with the above overwritten commit()
        typeDesignationsCollectionField.setRequiredError(CAN_T_SAVE_AS_LONG_AS_TYPE_DESIGNATIONS_ARE_MISSING);
        typeDesignationsCollectionField.setVisibleProperties(SpecimenTypeDesignationDTORow.visibleFields());

        typeDesignationsCollectionField.setPropertyHeader("accessionNumber", "Access. num.");
        typeDesignationsCollectionField.setPropertyHeader("preferredStableUri", "Stable URI");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReference", "Image reference");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReferenceDetail", "Reference detail");
        typeDesignationsCollectionField.addElementAddedListener( e -> typeDesignationsCollectionField.setComponentError(null));
        typeDesignationsCollectionField.getLayout().setMargin(new MarginInfo(false, true));

        // typeDesignationsCollectionField.getLayout().setMargin(false);
        // typeDesignationsCollectionField.addStyleName("composite-field-wrapper");
        // addField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs", 0, row, 2, row);

        typeDesignationsScrollPanel = new Panel(typeDesignationsCollectionField.getLayout());
        typeDesignationsScrollPanel.setCaption("Types");
        typeDesignationsScrollPanel.setWidth(800, Unit.PIXELS);

        bindField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs");
        addComponent(typeDesignationsScrollPanel, 0, row, endColumnIndex, row);

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
        addField(annotationsListField, "annotations", 0, row, endColumnIndex, row);

     }

    @Override
    public String getWindowCaption() {
        return "Specimen typedesignations editor";
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
    public boolean allowAnonymousAccess() {
        return false;
    }

    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }

    @Override
    public String getAccessDeniedMessage() {
        return accessDeniedMessage;
    }

    @Override
    public void setAccessDeniedMessage(String accessDeniedMessage) {
        this.accessDeniedMessage = accessDeniedMessage;
    }

    @Override
    public boolean isResizable() {
        return true;
    }


    // ------- SpecimenTypeDesignationSetPopupEditorView methods ---- //
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

    @Override
    public void grantToCurrentUser(EnumSet<CRUD> crud) {
        getPresenter().setGrantsForCurrentUser(crud);

    }

    @Override
    protected void afterItemDataSourceSet() {
        super.afterItemDataSourceSet();
        GridLayout gridLayout = this.typeDesignationsCollectionField.getLayout();
        for(int rowIndex = 1; rowIndex < gridLayout.getRows(); rowIndex++){ // first row is header
            Component item = gridLayout.getComponent(SpecimenTypeDesignationDTORow.rowListSelectColumn(), rowIndex);
            ((CollectionRowRepresentative)item).updateRowItemsEnabledStates();
        }
        updateAllowDeleteTypeDesignation();
    }

    @Override
    public void updateAllowDeleteTypeDesignation(){
        // disable the delete button if there is only one typeDesignation
        // if this typeDesignation is deleted the fieldUnit would become orphan in the
        // TypeDesignationSet
        GridLayout gridLayout = this.typeDesignationsCollectionField.getLayout();
        if(gridLayout.getRows() == 3){ // first row is header, last row is next new item
            gridLayout.getComponent(gridLayout.getColumns() - 1, 1).setEnabled(false);
        }
    }

    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        getFieldLayout().iterator().forEachRemaining(c -> c.setReadOnly(readOnly));
        collectorField.setEditorReadOnly(readOnly);
        typeDesignationsCollectionField.getLayout().iterator().forEachRemaining(c -> c.setReadOnly(readOnly));

    }

    @Override
    public TeamOrPersonField getCollectorField() {
        return collectorField;
    }

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     */
    @Override
    public AnnotationType[] getEditableAnotationTypes() {
        return editableAnotationTypes;
    }

    /**
     * By default  AnnotationType.EDITORIAL() is enabled.
     *
     * @param editableAnotationTypes the editableAnotationTypes to set
     */
    @Override
    public void setEditableAnotationTypes(AnnotationType ... editableAnotationTypes) {
        this.editableAnotationTypes = editableAnotationTypes;
    }


    @Override
    public FilterableAnnotationsField getAnnotationsField() {
        return annotationsListField;
    }

    @Override
    public GeoLocationField getExactLocationField() {
        return exactLocationField;
    }


}
