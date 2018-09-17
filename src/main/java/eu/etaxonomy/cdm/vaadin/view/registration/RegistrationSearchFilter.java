/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Set;

import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;

class RegistrationSearchFilter {
    String identifierPattern;
    String namePattern;
    User submitter;
    Set<TypeDesignationStatusBase> typeStatus;
    Set<RegistrationStatus> registrationStatus;
}