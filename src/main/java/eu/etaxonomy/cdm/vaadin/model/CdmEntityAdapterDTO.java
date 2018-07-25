/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model;

import java.io.Serializable;
import java.util.UUID;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;

/**
 * A DTO for cdm entities which itself implements the
 * <a href="https://en.wikipedia.org/wiki/Adapter_pattern#Object_Adapter_pattern">Object Adaptor Pattern</a>.
 *
 *
 * @author a.kohlbecker
 * @since Apr 23, 2018
 *
 */
public class CdmEntityAdapterDTO<CDM extends CdmBase> implements Serializable {

    private static final long serialVersionUID = 1715911851453178727L;

    protected CDM entity;

    public CdmEntityAdapterDTO(CDM entity){
        this.entity = entity;
    }

    public CDM cdmEntity(){
        return entity;
    }

    public void setCreated(DateTime created) {
        entity.setCreated(created);
    }

    public void setCreatedBy(User createdBy) {
        entity.setCreatedBy(createdBy);
    }

    public DateTime getCreated() {
        return entity.getCreated();
    }

    public User getCreatedBy() {
        return entity.getCreatedBy();
    }

    public int getId() {
        return entity.getId();
    }

    public void setId(int id) {
        entity.setId(id);
    }

    public UUID getUuid() {
        return entity.getUuid();
    }

    public void setUuid(UUID uuid) {
        entity.setUuid(uuid);
    }

}
