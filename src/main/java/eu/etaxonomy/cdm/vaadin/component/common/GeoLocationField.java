/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import org.apache.log4j.Logger;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.util.converter.GeoLocationConverterValidator;
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

    private TextField longitudeField = new TextFieldNFix("Longitude");
    TextField latitudeField = new TextFieldNFix("Latitude");
    Label longLatParsed = new Label();
    TextField errorRadiusField = new TextFieldNFix("Error radius (m)");
    TextField referenceSystemField = new TextFieldNFix("ReferenceSystem");

    private LMap map = new LMap();
    private LMarker mapMarker = new LMarker();

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

        GridLayout root = new GridLayout();
        root.setRows(2);
        root.setColumns(3);
        root.setStyleName("wrapper");
        root.addComponent(latitudeField, 0, 0);
        root.addComponent(longitudeField, 1, 0);
        root.addComponent(errorRadiusField, 0, 1);
        root.addComponent(referenceSystemField, 1, 1);

        map = new LMap();
        map.addBaseLayer(new LOpenStreetMapLayer(), null);
        map.setDraggingEnabled(false);
        map.setScrollWheelZoomEnabled(false);
        map.removeControl(map.getLayersControl());

        root.setColumnExpandRatio(2, 1.0f);
        root.setRowExpandRatio(1, 1.0f);

        root.addComponent(map, 2, 1);
        mapWrapper = new CssLayout(longLatParsed, map);
        root.addComponent(mapWrapper, 2, 0, 2, 1);
        mapWrapper.setSizeFull();
        mapWrapper.setStyleName("map-wrapper");
        longLatParsed.setWidthUndefined();

        longitudeField.setConverter(new GeoLocationConverterValidator(Axis.LONGITUDE));
        longitudeField.addValidator(new GeoLocationConverterValidator(Axis.LONGITUDE));
        longitudeField.setBuffered(false);
        longitudeField.addValueChangeListener(e -> updateMap());

        latitudeField.setConverter(new GeoLocationConverterValidator(Axis.LATITUDE));
        latitudeField.addValidator(new GeoLocationConverterValidator(Axis.LATITUDE));
        latitudeField.setBuffered(false);
        latitudeField.addValueChangeListener(e -> updateMap());

        addStyledComponents(longitudeField, latitudeField, errorRadiusField, referenceSystemField, longLatParsed);
        addSizedComponent(root);

        fieldGroup.bind(longitudeField, "longitude");
        fieldGroup.bind(latitudeField, "latitude");
        fieldGroup.bind(errorRadiusField, "errorRadius");
        fieldGroup.bind(referenceSystemField, "referenceSystem");

        return root;
    }

//    /**
//     * @param longitudeField2
//     * @param value
//     * @return
//     */
//    private void updateParsedValue(TextField field, String value) {
//        field.setComponentError(null);
//        updateMap();
//    }

    /**
     *
     */
    protected void updateMap() {
        // using the string representations for UI display
        longLatParsed.setValue(longitudeField.getValue() + "/" + latitudeField.getValue());
        map.removeComponent(mapMarker);
        Double longitude = (Double) longitudeField.getConverter().convertToModel(longitudeField.getValue(), Double.class, null);
        Double latitude = (Double) latitudeField.getConverter().convertToModel(latitudeField.getValue(), Double.class, null);
        logger.debug("panning map to " + longitude + "/" + latitude);
        if(longitude != null && latitude != null){
            map.setZoomLevel(10);
            mapMarker.setPoint(new org.vaadin.addon.leaflet.shared.Point(latitude, longitude));
            map.addComponents(mapMarker);
            map.setCenter(latitude, longitude);
        } else {
            map.setZoomLevel(1);
            map.setCenter(40, 0);
        }
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
        referenceSystemField.setEnabled(false); // disabled since not fully implemented
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



}
