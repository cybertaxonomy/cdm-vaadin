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
 * @since May 10, 2017
 *
 */
public abstract class AbstractEntityEvent<T extends Enum> {

    private Integer entityId = null;

    protected T type;

    public AbstractEntityEvent(T type, Integer entityId) {
        this.entityId = entityId;
        this.type = type;
        if(type == null){
            throw new NullPointerException();
        }
    }

    /**
     * @return the entityId
     */
    public Integer getEntityId() {
        return entityId;
    }

}
