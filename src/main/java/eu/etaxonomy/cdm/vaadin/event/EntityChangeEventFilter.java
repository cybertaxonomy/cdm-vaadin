/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import java.util.Optional;

import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBusListenerMethodFilter;

import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;

/**
 * @author a.kohlbecker
 * @since Jan 31, 2018
 *
 */
public class EntityChangeEventFilter implements EventBusListenerMethodFilter {


    private Optional<Class<?>> entityType = null;

    private Optional<Type> eventType = null;

    private EntityChangeEventFilter(Class<?> entityType, Type eventType){
        this.entityType = Optional.of(entityType);
        this.eventType = Optional.of(eventType);
    }

    private EntityChangeEventFilter(Type eventType){
        this.eventType = Optional.of(eventType);
    }

    private EntityChangeEventFilter(Class<?> entityType){
        this.entityType = Optional.of(entityType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean filter(Event<?> event) {
        if(event.getPayload() instanceof EntityChangeEvent){
            EntityChangeEvent testEvent = (EntityChangeEvent)event.getPayload();
            boolean match = true;
            if(this.entityType != null){
                match &= this.entityType.get().equals(testEvent.getEntityType());
            }
            if(this.eventType != null){
                match &= this.eventType.get().equals(testEvent.getType());
            }
            return match;
        }
        return false;
    }


    public static class OccurrenceCollectionFilter extends EntityChangeEventFilter{

        public OccurrenceCollectionFilter(){
            super(eu.etaxonomy.cdm.model.occurrence.Collection.class);
        }
    }

    public static class ReferenceFilter extends EntityChangeEventFilter {

        public ReferenceFilter() {
            super(eu.etaxonomy.cdm.model.reference.Reference.class);
        }
    }

}
