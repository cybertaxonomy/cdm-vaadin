/**
* Copyright (C) 2020 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * Interface for classes converting {@link CdmBase) beans into DTO classes.
 * <p>
 * This interface is meant to be used in the context of {@link BeanItemContainer}s,
 *
 * @author a.kohlbecker
 * @since Sep 21, 2020
 */
public interface BeanToDTOConverter<CDM extends CdmBase, DTO extends Object> {

    public DTO toDTO(CDM bean);

    public Class<DTO> getDTOType();

}
