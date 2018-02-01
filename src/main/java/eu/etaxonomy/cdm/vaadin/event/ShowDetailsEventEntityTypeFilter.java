/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBusListenerMethodFilter;

/**
 * @author a.kohlbecker
 * @since Jan 31, 2018
 *
 */
public class ShowDetailsEventEntityTypeFilter implements EventBusListenerMethodFilter {


    Class entityType;

    private ShowDetailsEventEntityTypeFilter(Class entityType){
        this.entityType = entityType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean filter(Event<?> event) {

        if(event.getPayload() instanceof ShowDetailsEvent){
            ShowDetailsEvent detailsEvent = (ShowDetailsEvent)event.getPayload();
            return this.entityType.equals(detailsEvent.getEntityType());
        }
        return false;
    }

    public static class RegistrationWorkingSet extends ShowDetailsEventEntityTypeFilter{

        public RegistrationWorkingSet(){
            super(RegistrationWorkingSet.class);
        }
    }

    public static class RegistrationDTO extends ShowDetailsEventEntityTypeFilter{

        public RegistrationDTO(){
            super(RegistrationDTO.class);
        }
    }
}
