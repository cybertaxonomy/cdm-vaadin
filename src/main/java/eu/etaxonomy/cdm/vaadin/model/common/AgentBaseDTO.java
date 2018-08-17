/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.common;

import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.vaadin.model.CdmEntityAdapterDTO;

/**
 * @author a.kohlbecker
 * @since Jul 25, 2018
 *
 */
public class AgentBaseDTO extends CdmEntityAdapterDTO<AgentBase<?>> {

    private static final long serialVersionUID = 8457280951445449327L;

    /**
     * @param entity
     */
    public AgentBaseDTO(AgentBase<?> entity) {
        super(entity);
        // TODO Auto-generated constructor stub
    }

}
