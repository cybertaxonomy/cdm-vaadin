/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import java.io.Serializable;

import com.vaadin.ui.AbstractField;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;

/**
 * @author a.kohlbecker
 * @since Jul 4, 2018
 */
public interface RegistrationStatusFieldInstantiator<T> extends Serializable {

    public AbstractField<T> create(RegistrationDTO regDto);

}