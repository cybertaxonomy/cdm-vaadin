/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.reference.INomenclaturalReference;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.util.TypeDesignationConverter;

public class RegistrationDTO{

    private String summary = "";

    private String citationString = "";

    private int citationID;

    private RegistrationType registrationType;

    private Registration reg;

    static int idAutoincrement = 100000;

    private Set<Registration> blockedBy = new HashSet<>();

    /**
     * @param reg
     * @param typifiedName should be provided in for Registrations for TypeDesignations
     */
    public RegistrationDTO(Registration reg, TaxonNameBase typifiedName) {

         this.reg = reg;

        registrationType = RegistrationType.from(reg);
        if(registrationType.isName()){
            summary = reg.getName().getTitleCache();
            INomenclaturalReference citation = reg.getName().getNomenclaturalReference();
            if(citation != null){
                citationString = citation.generateTitle();
                citationID = citation.getId();
            }
        } else if(registrationType.isTypification()){
            summary = new TypeDesignationConverter(reg.getTypeDesignations(), typifiedName)
                    .buildString().print();
            if(!reg.getTypeDesignations().isEmpty()){
                Reference citation = reg.getTypeDesignations().iterator().next().getCitation();
                if(citation != null) {
                    citationString = citation.generateTitle();
                    citationID = citation.getId();
                }
            }
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
     * @return the specificIdentifier
     */
    public String getSpecificIdentifier() {
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

    public String getCitation() {
        return citationString;
    }

    /**
     * @return the blockedBy
     */
    public Set<Registration> getBlockedBy() {
        return blockedBy;
    }

    /**
     * @return the citationString
     */
    public String getCitationString() {
        return citationString;
    }

    /**
     * @return the citationID
     */
    public int getCitationID() {
        return citationID;
    }

}