/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.ui.Component.Event;

/**
 * A request to display the details of the <code>property</code> for an object of
 * the class <code>entityType</code> which is uniquely identified by the <code>identifier</code>
 * This event is usually passed from a view to the according presenter implementation
 * which is capable of retrieving the details based on the <code>entityType</code> +
 * <code>identifier</code> + <code>property</code> and which should in turn pass the
 * extracted details data to the view for display.
 *
 * @author a.kohlbecker
 * @since Mar 24, 2017
 *
 */
public class ShowDetailsEvent<T extends Object, I extends Object> {

    private Event source;
    private Class<T> entityType;
    private I identifier;
    private String property;

    /**
     *
     * @param e
     * @param identifier
     * @param property
     */
    public ShowDetailsEvent(Event e, Class<T> entityType , I identifier, String property) {
        this.source = e;
        this.entityType = entityType;
        this.identifier = identifier;
        this.property = property;
    }

    /**
     * @return the source
     */
    public Event getSource() {
        return source;
    }

    /**
     * @return the type
     */
    public Class<T> getType() {
        return entityType;
    }

    /**
     * @return the identifier
     */
    public I getIdentifier() {
        return identifier;
    }

    /**
     * @return the property
     */
    public String getProperty() {
        return property;
    }



}
