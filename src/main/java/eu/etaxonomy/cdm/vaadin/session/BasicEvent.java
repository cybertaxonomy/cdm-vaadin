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

import com.vaadin.ui.Component;

/**
 * @author cmathew
 * @date 21 Apr 2015
 *
 */
public class BasicEvent {

    private final String eventId;
    private final Class<? extends Component> sourceType;

    public BasicEvent(String eventId, Class sourceType) {
        this.eventId = eventId;
        if(eventId == null || eventId.isEmpty()) {
            throw new IllegalArgumentException("Event id cannot be null or empty");
        }
        this.sourceType = sourceType;
        if(sourceType == null) {
            throw new IllegalArgumentException("Source type cannot be null");
        }
    }

    /**
     * @return the eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     * @return the sourceType
     */
    public Class<? extends Component> getSourceType() {
        return sourceType;
    }

}
