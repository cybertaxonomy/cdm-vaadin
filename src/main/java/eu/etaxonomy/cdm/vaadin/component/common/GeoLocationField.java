/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.vaadin.addon.leaflet.LCircle;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.LeafletClickEvent;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.cdm.vaadin.component.LongLatField;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.util.converter.GeoLocationConverterValidator.Axis;
import eu.etaxonomy.cdm.vaadin.util.converter.IntegerConverter;
import eu.etaxonomy.vaadin.component.CompositeCustomField;

/**
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class GeoLocationField extends CompositeCustomField<Point> {

    private static final Logger logger = Logger.getLogger(GeoLocationField.class);

    private static final long serialVersionUID = 1122123034547920390L;

    private static final String PRIMARY_STYLE = "v-geolocation-field";

    private BeanFieldGroup<Point> fieldGroup = new BeanFieldGroup<>(Point.class);

    private TextField longitudeField = new LongLatField("Longitude", Axis.LONGITUDE);
    private TextField latitudeField = new LongLatField("Latitude", Axis.LATITUDE);
    private Label longLatParsed = new Label();
    private TextField errorRadiusField = new TextFieldNFix("Error radius (m)");
    private ListSelect referenceSystemSelect;

    private LMap map = new LMap();
    private LMarker mapMarker = new LMarker();
    private LCircle errorRadiusMarker = null;

    private CssLayout mapWrapper;


    /**
     *
     */
    public GeoLocationField() {
        super();
    }

    public GeoLocationField(String caption) {
        super();
        setCaption(caption);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {
        super.setPrimaryStyleName(PRIMARY_STYLE);

        errorRadiusField.setConverter(new IntegerConverter());


        map = new LMap();
        // LTileLayer baseLayer = new LOpenStreetMapLayer();
        LTileLayer baseLayer = new LTileLayer("https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png");
        baseLayer.setAttributionString("Map data © <a href=\"https://openstreetmap.org/copyright\">OpenStreetMap</a>-contributors, SRTM | Map style: © <a href=\"http://opentopomap.org\">OpenTopoMap</a> (<a href=\"https://creativecommons.org/licenses/by-sa/3.0/\">CC-BY-SA</a>)");
        map.addBaseLayer(baseLayer, null);
        map.setDraggingEnabled(true);
        map.setScrollWheelZoomEnabled(false);
        map.removeControl(map.getLayersControl());
        map.addClickListener(e -> refreshMap(e));
        // map.getZoomControl().addListener(ClickEvent.class, target, method);

        mapWrapper = new CssLayout(longLatParsed, map);
        mapWrapper.setSizeFull();
        mapWrapper.setStyleName("map-wrapper");
        longLatParsed.setWidthUndefined();

        longitudeField.setBuffered(false);
        longitudeField.addValueChangeListener(e -> updateMap());

        latitudeField.setBuffered(false);
        latitudeField.addValueChangeListener(e -> updateMap());

        errorRadiusField.addValueChangeListener( e -> updateMap());

        referenceSystemSelect = new ListSelect("Reference system");
        referenceSystemSelect.setNullSelectionAllowed(false);
        referenceSystemSelect.setRows(1);
        referenceSystemSelect.setWidth(100, Unit.PERCENTAGE);

        GridLayout root = new GridLayout();
        root.setRows(2);
        root.setColumns(3);
        root.setStyleName("wrapper");
        root.addComponent(longitudeField, 0, 0);
        root.addComponent(latitudeField, 1, 0);
        root.addComponent(errorRadiusField, 0, 1);
        root.addComponent(referenceSystemSelect, 1, 1);
        // root.addComponent(map, 2, 1);
        root.addComponent(mapWrapper, 2, 0, 2, 1);
        root.setColumnExpandRatio(2, 1.0f);
        root.setRowExpandRatio(1, 1.0f);

        addStyledComponents(longitudeField, latitudeField, errorRadiusField, referenceSystemSelect, longLatParsed);
        addSizedComponent(root);

        fieldGroup.bind(longitudeField, "longitude");
        fieldGroup.bind(latitudeField, "latitude");
        fieldGroup.bind(errorRadiusField, "errorRadius");
        fieldGroup.bind(referenceSystemSelect, "referenceSystem");

        return root;
    }

    /**
     *
     */
    protected void updateMap() {

        Double longitude  = null;
        Double latitude = null;

        // using the string representations for UI display
        longLatParsed.setValue(longitudeField.getValue() + "/" + latitudeField.getValue());
        map.removeComponent(mapMarker);
        if(errorRadiusMarker != null){
            map.removeComponent(errorRadiusMarker);
        }

        try {
            longitudeField.validate();
            longitude = (Double) longitudeField.getConverter().convertToModel(longitudeField.getValue(), Double.class, null);
            latitudeField.validate();
            latitude = (Double) latitudeField.getConverter().convertToModel(latitudeField.getValue(), Double.class, null);
        } catch (InvalidValueException | ConversionException e){
            // IGNORE validation error have been set in the UI in the validate() methods
        }

        logger.debug("panning map to " + longitude + "/" + latitude);
        if(longitude != null && latitude != null){
            map.setZoomLevel(10);
            if(!StringUtils.isEmpty(errorRadiusField.getValue())){
                try{
                    double errorRadius = Double.parseDouble(errorRadiusField.getValue());
                    if(errorRadius > 0){
                        errorRadiusMarker = new LCircle(latitude, longitude, errorRadius);
                        errorRadiusMarker.setColor("#ff0000");
                        errorRadiusMarker.setWeight(1);
                        map.addComponents(errorRadiusMarker);
                    }
                } catch(Exception e){ /* IGNORE */ }
            }
            mapMarker.setPoint(new org.vaadin.addon.leaflet.shared.Point(latitude, longitude));
            map.addComponents(mapMarker);
            map.setCenter(latitude, longitude);
        } else {
            map.setZoomLevel(1);
            map.setCenter(40, 0);
        }
    }

    protected void refreshMap(LeafletClickEvent e) {
        logger.debug("map click");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends Point> getType() {
        return Point.class;
    }

    @Override
    protected void setInternalValue(Point newValue) {
        if(newValue == null){
            newValue = Point.NewInstance();
        }
        super.setInternalValue(newValue);
        fieldGroup.setItemDataSource(new BeanItem<Point>(newValue));
        updateMap();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        // no default styles so far

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return fieldGroup;
    }

    public ListSelect getReferenceSystemSelect() {
        return referenceSystemSelect;
    }



}
