/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;


import static eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles.STYLE_LABEL_NOWRAP;

import org.apache.commons.lang.StringUtils;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationType;

/**
 * @author a.kohlbecker
 * @since Mar 30, 2017
 *
 */
public class TypeStateLabel extends Label {


    /**
     *
     */
    private static final long serialVersionUID = -7462726193336938780L;

    /**
     *
     */
    public TypeStateLabel() {
        setStyleName(STYLE_LABEL_NOWRAP);
    }

    /**
    *
    */
   public TypeStateLabel update(RegistrationType type, RegistrationStatus status) {

       FontAwesome icon;
       if(type.equals(RegistrationType.NAME)) {
           icon = FontAwesome.TAG;
       } else if(type.equals(RegistrationType.TYPIFICATION)) {
           icon = FontAwesome.TAGS;
       } else {
           icon = FontAwesome.WARNING;
       }
       setContentMode(ContentMode.HTML);
       setValue(icon.getHtml() + "&nbsp;" + StringUtils.capitalize((status.name().toLowerCase())));
       addStyleName("status-" + status.name());
       return this;
   }




}
