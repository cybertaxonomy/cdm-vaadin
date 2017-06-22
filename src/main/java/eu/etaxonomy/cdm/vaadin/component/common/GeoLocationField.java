/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
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

    TextField longitudeField = new TextField("Long.");
    TextField latitudeField = new TextField("Lat.");
    TextField errorRadiusField = new TextField("Error radius (m)");
    TextField referenceSystemField = new TextField("ReferenceSystem");

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
        root.setColumns(2);
        root.setStyleName("wrapper");
        root.addComponent(longitudeField);
        root.addComponent(latitudeField);
        root.addComponent(errorRadiusField);
        root.addComponent(referenceSystemField);

        addStyledComponents(longitudeField, latitudeField, errorRadiusField, referenceSystemField);
        addSizedComponent(root);

        fieldGroup.bind(longitudeField, "longitude");
        fieldGroup.bind(latitudeField, "latitude");
        fieldGroup.bind(errorRadiusField, "errorRadius");
        fieldGroup.bind(referenceSystemField, "referenceSystem");

        referenceSystemField.setEnabled(false); // disabled since not fully implemented

        return root;
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
