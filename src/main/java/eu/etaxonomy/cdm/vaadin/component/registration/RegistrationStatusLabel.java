/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;


import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.model.name.RegistrationStatus;

/**
 * @author a.kohlbecker
 * @since Mar 30, 2017
 *
 */
public class RegistrationStatusLabel extends Label {


    /**
     *
     */
    private static final long serialVersionUID = -7462726193336938780L;

    /**
    *
    */
   public RegistrationStatusLabel update(RegistrationStatus status) {

       setValue(StringUtils.capitalize((status.name().toLowerCase())));
       addStyleName("registration-status registration-status-" + status.name());
       setWidth("90px");
       return this;
   }




}
