/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationStatus;

public class RegistrationDTO{

    private String summary = "";

    private RegistrationType registrationType;

    private Registration reg;

    static int idAutoincrement = 100000;

    /**
     * @param reg
     */
    public RegistrationDTO(Registration reg) {

         this.reg = reg;

        registrationType = RegistrationType.from(reg);
        if(registrationType.isName()){
            summary = reg.getName().getTitleCache();
        } else if(registrationType.isTypification()){
            StringBuffer sb = new StringBuffer();
            reg.getTypeDesignations().forEach(td -> sb.append(td.toString()).append(' '));
            summary = sb.toString();
        } else {
            summary = "- INVALID REGISTRATION -";
        }
    }

    /**
     * @return the summary
     */
    public String getSummary() {
        return summary;
    }


    /**
     * @return the registrationType
     */
    public RegistrationType getRegistrationType() {
        return registrationType;
    }


    /**
     * @return the status
     */
    public RegistrationStatus getStatus() {
        return reg.getStatus();
    }


    /**
     * @return the registrationId
     */
    public String getRegistrationId() {
        return reg.getIdentifier();
    }


    /**
     * @return the internalRegId
     */
    public String getInternalRegId() {
        return reg.getSpecificIdentifier();
    }

    /**
     * @return the registrationDate
     */
    public DateTime getRegistrationDate() {
        return reg.getRegistrationDate();
    }

    /**
     * @return the created
     */
    public DateTime getCreated() {
        return reg.getCreated();
    }

}