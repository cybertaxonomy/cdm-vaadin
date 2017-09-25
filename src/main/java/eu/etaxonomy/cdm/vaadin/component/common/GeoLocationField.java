/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import java.text.ParseException;

import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.UserError;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.location.Point;
import eu.etaxonomy.vaadin.component.CompositeCustomField;

/**
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class GeoLocationField extends CompositeCustomField<Point> {


    private static final long serialVersionUID = 1122123034547920390L;

    private static final String PRIMARY_STYLE = "v-geolocation-field";

    private BeanFieldGroup<Point> fieldGroup = new BeanFieldGroup<>(Point.class);

    Point parsedPoint = Point.NewInstance();

    private TextField longitudeField = new TextField("Long.");
    TextField latitudeField = new TextField("Lat.");
    Label longLatParsed = new Label();
    TextField errorRadiusField = new TextField("Error radius (m)");
    TextField referenceSystemField = new TextField("ReferenceSystem");

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

        GridLayout root = new GridLayout();
        root.setRows(2);
        root.setColumns(3);
        root.setStyleName("wrapper");
        root.addComponent(longitudeField, 0, 0);
        root.addComponent(latitudeField, 1, 0);
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

        longitudeField.addTextChangeListener(e -> updateParsedValue(longitudeField, e.getText()));
        latitudeField.addTextChangeListener(e -> updateParsedValue(latitudeField, e.getText()));

        addStyledComponents(longitudeField, latitudeField, errorRadiusField, referenceSystemField, longLatParsed);
        addSizedComponent(root);

        fieldGroup.bind(longitudeField, "longitude");
        fieldGroup.bind(latitudeField, "latitude");
        fieldGroup.bind(errorRadiusField, "errorRadius");
        fieldGroup.bind(referenceSystemField, "referenceSystem");

        return root;
    }

    /**
     * @param longitudeField2
     * @param value
     * @return
     */
    private void updateParsedValue(TextField field, String value) {
        field.setComponentError(null);
        if(value != null){
            try {
            if(field == longitudeField){

                parsedPoint.setLongitudeByParsing(value);
            } else {
                parsedPoint.setLatitudeByParsing(value);
            }
            } catch (ParseException e) {
                field.setComponentError(new UserError(e.getMessage()));
            }
        }

        updateMap();
    }

    /**
     *
     */
    protected void updateMap() {
        longLatParsed.setValue(parsedPoint.getLongitudeSexagesimal() + "/" + parsedPoint.getLatitudeSexagesimal());
        map.removeComponent(mapMarker);
        if(parsedPoint.getLongitude() != null && parsedPoint.getLatitude() != null){
            map.setZoomLevel(10);
            mapMarker.setPoint(new org.vaadin.addon.leaflet.shared.Point(parsedPoint.getLatitude(), parsedPoint.getLongitude()));
            map.addComponents(mapMarker);
            map.setCenter(parsedPoint.getLatitude(), parsedPoint.getLongitude());
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

        parsedPoint = newValue;
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
