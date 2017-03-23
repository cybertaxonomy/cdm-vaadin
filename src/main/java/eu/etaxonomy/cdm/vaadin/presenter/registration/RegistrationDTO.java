/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.registration;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
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

    private TaxonNameBase<?, ?> typifiedName;

    /**
     * @param reg
     * @param typifiedName should be provided in for Registrations for TypeDesignations
     * @throws RegistrationValidationException in case of inconsistencies in the Registration
     */
    public RegistrationDTO(Registration reg) throws RegistrationValidationException {

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
            typifiedName = findTypifiedName();
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
     * @return
     * @throws RegistrationValidationException
     */
    private TaxonNameBase<?,?> findTypifiedName() throws RegistrationValidationException {

        StringBuffer problems = new StringBuffer();

        TaxonNameBase<?,?> typifiedName = null;

        for(TypeDesignationBase<?> typeDesignation : reg.getTypeDesignations()){
            typeDesignation.getTypifiedNames();
            if(typeDesignation.getTypifiedNames().isEmpty()){

                //TODO instead throw RegistrationValidationException()
                problems.append(" - Missing typifiedName in " + typeDesignation.toString()).append("\n");
                continue;
            }
            if(typeDesignation.getTypifiedNames().size() > 1){
              //TODO instead throw RegistrationValidationException()
                problems.append(" - Multiple typifiedName in " + typeDesignation.toString()).append("\n");
                continue;
            }
            if(typifiedName == null){
                // remember
                typifiedName = typeDesignation.getTypifiedNames().iterator().next();
            } else {
                // compare
                TaxonNameBase<?,?> otherTypifiedName = typeDesignation.getTypifiedNames().iterator().next();
                if(typifiedName.getId() != otherTypifiedName.getId()){
                  //TODO instead throw RegistrationValidationException()
                    problems.append(" - Multiple typifiedName in " + typeDesignation.toString()).append("\n");
                }
            }

        }
        if(problems.length() > 0){
            throw new RegistrationValidationException("Inconsistent Registration entity. " + reg.toString() + " Problems:\n" + problems.toString());
        }

        return typifiedName;
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