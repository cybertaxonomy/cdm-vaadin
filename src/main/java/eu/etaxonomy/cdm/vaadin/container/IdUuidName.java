/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.container;

import java.util.UUID;

/**
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class IdUuidName {

    private final Object id;
    private final UUID uuid;
    private final String name;

    public IdUuidName(Object id, UUID uuid, String name) {

        if(id == null || uuid == null) {
            throw new IllegalArgumentException("Neither Id nor Uuid can be null");
        }
        this.id = id;
        this.uuid = uuid;
        this.name = name;
    }

    /**
     * @return the id
     */
    public Object getId() {
        return id;
    }

    /**
     * @return the uuid
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

}
