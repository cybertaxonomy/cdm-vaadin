/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.session;

import java.util.List;

import com.vaadin.ui.Component;

/**
 * @author cmathew
 * @since 9 Apr 2015
 *
 */
public class SelectionEvent {

    private final List<Object> selectedObjects;
    private Component source;
    private Class<? extends Component> sourceType;

    public SelectionEvent(List<Object> selectedObjects, Class<? extends Component> sourceType) {
        this.selectedObjects = selectedObjects;
        if(selectedObjects == null || selectedObjects.isEmpty()) {
            throw new IllegalArgumentException("Changed objects cannot be empty");
        }
        this.sourceType = sourceType;
        if(sourceType == null) {
            throw new IllegalArgumentException("Source type cannot be null");
        }
    }

    public SelectionEvent(List<Object> selectedObjects, Component source) {
        this.selectedObjects = selectedObjects;
        if(selectedObjects == null || selectedObjects.isEmpty()) {
            throw new IllegalArgumentException("Changed objects cannot be empty");
        }
        this.source = source;
        if(source == null) {
            throw new IllegalArgumentException("Source  cannot be null");
        }
        this.sourceType = source.getClass();
    }

    /**
     * @return the selectedObjects
     */
    public List<Object> getSelectedObjects() {
        return selectedObjects;
    }

    /**
     * @return the source
     */
    public Component getSource() {
        return source;
    }

    /**
     * @return the sourceType
     */
    public Class<? extends Component> getSourceType() {
        return sourceType;
    }


}
