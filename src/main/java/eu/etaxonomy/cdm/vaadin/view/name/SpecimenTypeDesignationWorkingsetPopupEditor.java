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

import org.springframework.security.core.GrantedAuthority;
import org.vaadin.viritin.fields.ElementCollectionField;

import com.vaadin.data.validator.DoubleRangeValidator;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.vaadin.component.PartialDateField;
import eu.etaxonomy.cdm.vaadin.component.common.GeoLocationField;
import eu.etaxonomy.cdm.vaadin.component.common.MinMaxTextField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
public class SpecimenTypeDesignationWorkingsetPopupEditor
    extends AbstractPopupEditor<SpecimenTypeDesignationWorkingSetDTO, SpecimenTypeDesignationWorkingsetEditorPresenter>
    implements SpecimenTypeDesignationWorkingsetPopupEditorView, AccessRestrictedView {

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
        grid.setRows(9);

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
        addField(localityField, "locality", 0, row , 2, row);
        localityField.setWidth("100%");
        // NOTE: setRows and SetCold breaks the width setting,
        //        see https://github.com/vaadin/framework/issues/3617

        row++;
        GeoLocationField exactLocation = new GeoLocationField("Geo location");
        addField(exactLocation, "exactLocation", 0, row, 2, row);
        exactLocation.setWidth("100%");

        row++;
        MinMaxTextField absElevationMinMax = new MinMaxTextField("Altitude", "m");
        absElevationMinMax.setWidth("100%");
        absElevationMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(absElevationMinMax, 0, row, 2, row);

        bindField(absElevationMinMax.getMinField(), "absoluteElevation");
        bindField(absElevationMinMax.getMaxField(), "absoluteElevationMax");
        bindField(absElevationMinMax.getTextField(), "absoluteElevationText");

        row++;
        MinMaxTextField distanceToWaterSurfaceMinMax = new MinMaxTextField("Distance to water surface", "m");
        distanceToWaterSurfaceMinMax.setWidth("100%");
        distanceToWaterSurfaceMinMax.addSubComponentsStyleName(getDefaultComponentStyles());
        grid.addComponent(distanceToWaterSurfaceMinMax, 0, row, 2, row);

        bindField(distanceToWaterSurfaceMinMax.getMinField(), "distanceToWaterSurface");
        bindField(distanceToWaterSurfaceMinMax.getMaxField(), "distanceToWaterSurfaceMax");
        bindField(distanceToWaterSurfaceMinMax.getTextField(), "distanceToWaterSurfaceText");
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

        row++;
        TeamOrPersonField collectorField = new TeamOrPersonField("Collector");
        addField(collectorField, "collector", 0, row, 2, row);

        row++;
        PartialDateField collectionDateField = new PartialDateField("Collection date");
        addField(collectionDateField, "gatheringDate", 0, row);
        TextField fieldNumberField = addTextField("Field number", "fieldNumber", 2, row);

        row++;

        // FIXME: can we use the Grid instaed?
        typeDesignationsCollectionField = new ElementCollectionField<>(
                SpecimenTypeDesignationDTO.class,
                SpecimenTypeDesignationDTORow.class
                );
        typeDesignationsCollectionField.withCaption("Types");
        typeDesignationsCollectionField.getLayout().setSpacing(false);
        typeDesignationsCollectionField.getLayout().setColumns(3);

        typeDesignationsCollectionField.setPropertyHeader("accessionNumber", "Access. num.");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReference", "Image reference");
        typeDesignationsCollectionField.setPropertyHeader("mediaSpecimenReferenceDetail", "Reference detail");

        // typeDesignationsCollectionField.getLayout().setMargin(false);
        // typeDesignationsCollectionField.addStyleName("composite-field-wrapper");
        // addField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs", 0, row, 2, row);

        Panel scrollPanel = new Panel(typeDesignationsCollectionField.getLayout());
        scrollPanel.setCaption("Types");
        scrollPanel.setWidth(800, Unit.PIXELS);

        bindField(typeDesignationsCollectionField, "specimenTypeDesignationDTOs");
        addComponent(scrollPanel, 0, row, 2, row);

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




}
