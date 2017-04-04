/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.model.common.TimePeriod;
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

    private List<String> messages = new ArrayList<>();

    private Set<Registration> blockedBy = new HashSet<>();

    private TaxonNameBase<?, ?> typifiedName;

    private TimePeriod datePublished;

    /**
     * @param reg
     * @param typifiedName should be provided in for Registrations for TypeDesignations
     */
    public RegistrationDTO(Registration reg) {

         this.reg = reg;

        registrationType = RegistrationType.from(reg);
        if(registrationType.isName()){
            summary = reg.getName().getTitleCache();
            INomenclaturalReference citation = reg.getName().getNomenclaturalReference();
            if(citation != null){
                citationString = citation.generateTitle();
                citationID = citation.getId();
                datePublished = citation.getDatePublished();
            }
        } else if(registrationType.isTypification()){
            try {
                typifiedName = findTypifiedName();
            } catch (RegistrationValidationException e) {
                messages.add("Validation errors: " + e.getMessage());
            }
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

        messages.add("dummy");
    }



    /**
     * FIXME use the validation framework validators and to store the validation problems!!!
     *
     * @return
     * @throws RegistrationValidationException
     */
    private TaxonNameBase<?,?> findTypifiedName() throws RegistrationValidationException {

        List<String> problems = new ArrayList<>();

        TaxonNameBase<?,?> typifiedName = null;

        for(TypeDesignationBase<?> typeDesignation : reg.getTypeDesignations()){
            typeDesignation.getTypifiedNames();
            if(typeDesignation.getTypifiedNames().isEmpty()){

                //TODO instead throw RegistrationValidationException()
                problems.add("Missing typifiedName in " + typeDesignation.toString());
                continue;
            }
            if(typeDesignation.getTypifiedNames().size() > 1){
              //TODO instead throw RegistrationValidationException()
                problems.add("Multiple typifiedName in " + typeDesignation.toString());
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
                    problems.add("Multiple typifiedName in " + typeDesignation.toString());
                }
            }

        }
        if(!problems.isEmpty()){
            // FIXME use the validation framework
            throw new RegistrationValidationException("Inconsistent Registration entity. " + reg.toString(), problems);
        }

        return typifiedName;
    }

    /**
     * Provides access to the Registration entity this DTO has been build from.
     * This method is purposely not a getter to hide the original Registration
     * from generic processes which are exposing, binding bean properties.
     *
     * @return
     */
    public Registration registration() {
        return reg;
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
     * @return the identifier
     */
    public String getIdentifier() {
        return reg.getIdentifier();
    }


    public int getId() {
        return reg.getId();
    }


    public UUID getUuid() {
        return reg.getUuid();
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
     * @return the registrationDate
     */
    public TimePeriod getDatePublished() {
        return datePublished;
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



    /**
     * @return
     */
    public List<String> getMessages() {
        return messages;
    }

}