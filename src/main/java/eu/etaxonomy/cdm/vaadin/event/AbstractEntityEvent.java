/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public abstract class AbstractEntityEvent {


    private EntityEventType eventType;

    public AbstractEntityEvent(EntityEventType eventType) {
        this.eventType = eventType;
        if(eventType == null){
            throw new NullPointerException();
        }
    }

    public EntityEventType getEventType() {
        return eventType;
    }

    public boolean isAddEvent() {
        return eventType.equals(EntityEventType.ADD);
    }
    public boolean isEditEvent() {
        return eventType.equals(EntityEventType.EDIT);
    }
    public boolean isRemoveEvent() {
        return eventType.equals(EntityEventType.REMOVE);
    }

}
