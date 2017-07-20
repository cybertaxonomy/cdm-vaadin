/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

public class RegistrationAndWorkingsetId {
    Integer registrationId;
    Integer workingsetId;
    /**
     * @param registrationId
     * @param specimentId
     */
    public RegistrationAndWorkingsetId(Integer registrationId, Integer specimentId) {
        super();
        this.registrationId = registrationId;
        this.workingsetId = specimentId;
    }

}