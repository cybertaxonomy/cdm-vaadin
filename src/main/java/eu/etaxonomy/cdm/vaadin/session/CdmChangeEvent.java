// $Id$
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
 * @date 7 Apr 2015
 *
 */
public class CdmChangeEvent {

    public enum Action {
        Create,
        Update,
        Delete
    }

    private final Action action;
    private final List<Object> changedObjects;
    private Class<? extends Component> sourceType;
    private Object source;

    public CdmChangeEvent(Action action, List<Object> changedObjects, Class<? extends Component> sourceType) {
        this.action = action;
        this.changedObjects = changedObjects;
        if(changedObjects == null || changedObjects.isEmpty()) {
            throw new IllegalArgumentException("Changed objects cannot be empty");
        }
        this.sourceType = sourceType;
        if(sourceType == null) {
            throw new IllegalArgumentException("Source type cannot be null");
        }
    }

    public CdmChangeEvent(Action action, List<Object> changedObjects, Component source) {
        this.action = action;
        this.changedObjects = changedObjects;
        if(changedObjects == null || changedObjects.isEmpty()) {
            throw new IllegalArgumentException("Changed objects cannot be empty");
        }
        this.source= source;
        if(source == null) {
            throw new IllegalArgumentException("Source cannot be null");
        }
        this.sourceType = source.getClass();
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the changedObjects
     */
    public List<Object> getChangedObjects() {
        return changedObjects;
    }

    /**
     * @return the sourceType
     */
    public Class<? extends Component> getSourceType() {
        return sourceType;
    }


    /**
     * @return the source
     */
    public Object getSource() {
        return source;
    }

}
