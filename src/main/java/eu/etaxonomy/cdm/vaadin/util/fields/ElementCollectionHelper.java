/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util.fields;

import java.awt.Point;
import java.util.List;
import java.util.Objects;

import org.vaadin.viritin.fields.AbstractElementCollection;
import org.vaadin.viritin.fields.ElementCollectionField;
import org.vaadin.viritin.fields.ElementCollectionTable;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Layout;

/**
 * @author a.kohlbecker
 * @since Feb 25, 2021
 */
public class ElementCollectionHelper<T extends AbstractElementCollection<?>> {

    private T elementColection;

    public ElementCollectionHelper(T elementColection) {
        this.elementColection = elementColection;
    }

    private Layout getLayout() {
        if(elementColection instanceof ElementCollectionField) {
            return ((ElementCollectionField)elementColection).getLayout();
        }
        if(elementColection instanceof ElementCollectionTable) {
            return ((ElementCollectionTable)elementColection).getLayout();
        }
        throw new RuntimeException("Unsuppoerted type " + elementColection.getClass());
    }

    /**
     * Provides the Property name of the field to which the <code>component</code> is mapped.
     */
    public String properyFor(Component component) {
        Point xy = locateComponent(component);
        List<String> visProps = elementColection.getVisibleProperties();
        return visProps.get(xy.x);
    }

    protected Point locateComponent(Component component) {
        if(elementColection instanceof ElementCollectionField) {
            GridLayout gridLayout = ((ElementCollectionField)elementColection).getLayout();
            for(int x = 0; x < gridLayout.getColumns(); x++) {
                for(int y = 0; y < gridLayout.getRows(); y++) {
                    Component gridComponent = gridLayout.getComponent(x, y);
                    if(Objects.equals(gridComponent, component)) {
                        return new Point(x, y);
                    }
                }
            }
            return null;
        } else {
            throw new RuntimeException("Unsuppoerted type " + elementColection.getClass());
        }
    }

}
