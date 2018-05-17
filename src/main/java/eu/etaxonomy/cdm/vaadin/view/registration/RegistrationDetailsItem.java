/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import com.vaadin.ui.CssLayout;

import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemButtons;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItemNameAndTypeButtons;

class RegistrationDetailsItem {

    RegistrationItemNameAndTypeButtons registrationItemEditButtonGroup;
    RegistrationItemButtons regItemButtons;
    CssLayout itemFooter;

    public RegistrationDetailsItem(RegistrationItemNameAndTypeButtons registrationItemEditButtonGroup, RegistrationItemButtons regItemButtons, CssLayout itemFooter){
        this.registrationItemEditButtonGroup = registrationItemEditButtonGroup;
        this.regItemButtons = regItemButtons;
        this.itemFooter = itemFooter;
    }


}