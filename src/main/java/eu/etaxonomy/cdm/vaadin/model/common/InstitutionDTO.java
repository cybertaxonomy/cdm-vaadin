/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.common;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;

/**
 * @author a.kohlbecker
 * @since Jul 25, 2018
 *
 */
public class InstitutionDTO extends CdmEntityAdapterDTO<Institution> {

    private static final long serialVersionUID = 8457280951445449327L;

    /**
     * @param entity
     */
    public InstitutionDTO(Institution entity) {
        super(entity);
    }

    public void setCode(String code){
        entity.setCode(code);
    }

    public String getCode(){
        return entity.getCode();
    }

    /**
     * Returns the full name, as distinct from a code, an acronym or initials,
     * by which this institution is generally known.
     */
    public String getName(){
        return entity.getName();
    }
    /**
     * @see    #getName()
     */
    public void setName(String name){
        entity.setName(name);
    }

    /**
     * Returns the parent institution of this institution.
     * This is for instance the case when this institution is a herbarium
     * belonging to a parent institution such as a museum.
     */
    public Institution getIsPartOf(){
        return entity.getIsPartOf();
    }

    /**
     * Assigns a parent institution to which this institution belongs.
     *
     * @param  isPartOf  the parent institution
     * @see    #getIsPartOf()
     */
    public void setIsPartOf(Institution parentInstitution){
        entity.setIsPartOf(parentInstitution);
    }


}
