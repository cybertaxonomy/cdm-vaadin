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

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.INomenclaturalReference;
import eu.etaxonomy.cdm.model.reference.IReference;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationConverter;

public class RegistrationDTO{

    private static final Logger logger = Logger.getLogger(RegistrationDTO.class);

    private String summary = "";

    private RegistrationType registrationType;

    private IReference citation = null;

    private String citationDetail = null;

    private Registration reg;

    private List<String> messages = new ArrayList<>();

    private Set<eu.etaxonomy.cdm.model.name.Registration> blockedBy = new HashSet<>();

    private TaxonNameBase<?, ?> typifiedName;

    /**
     * @param reg
     * @param typifiedName should be provided in for Registrations for TypeDesignations
     */
    public RegistrationDTO(Registration reg) {

         this.reg = reg;

         registrationType = RegistrationType.from(reg);

        if(hasName(reg)){
            citation = reg.getName().getNomenclaturalReference();
            citationDetail = reg.getName().getNomenclaturalMicroReference();
        }
        if(hasTypifications(reg)){
            try {
                typifiedName = findTypifiedName();
            } catch (RegistrationValidationException e) {
                messages.add("Validation errors: " + e.getMessage());
            }
            if(!reg.getTypeDesignations().isEmpty()){
                if(citation == null) {
                    TypeDesignationBase first = reg.getTypeDesignations().iterator().next();
                    citation = first.getCitation();
                    citationDetail = first.getCitationMicroReference();
                }
            }
        }
        switch(registrationType) {
        case EMPTY:
            summary = "BLANK REGISTRATION";
            break;
        case NAME:
            summary = reg.getName().getTitleCache();
            break;
        case NAME_AND_TYPIFICATION:
        case TYPIFICATION:
        default:
            summary = new TypeDesignationConverter(reg.getTypeDesignations(), typifiedName).buildString().print();
            break;
        }

        // trigger initialization of the reference
        getNomenclaturalCitationString();

    }

    /**
     * @param reg
     * @return
     */
    private boolean hasTypifications(Registration reg) {
        return reg.getTypeDesignations() != null && reg.getTypeDesignations().size() > 0;
    }

    /**
     * @param reg
     * @return
     */
    private boolean hasName(Registration reg) {
        return reg.getName() != null;
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
     *IReference
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
        return citation == null ? null : citation.getDatePublished();
    }

    /**
     * @return the created
     */
    public DateTime getCreated() {
        return reg.getCreated();
    }

    public IReference getCitation() {
        return citation;
    }

    /**
     * @return the citationID
     */
    public Integer getCitationID() {
        return citation == null ? null : citation.getId();
    }

    /**
     * @return the citationString
     */
    public String getNomenclaturalCitationString() {
        if(citation == null){
            return null;
        }
        if(INomenclaturalReference.class.isAssignableFrom(citation.getClass())){
            return ((INomenclaturalReference)citation).getNomenclaturalCitation(citationDetail);
        } else {
            logger.error("The citation is not a NomenclaturalReference");
            return citation.generateTitle();
        }
    }

    /**
     * @return the citationString
     */
    public String getBibliographicCitationString() {
        if(citation == null){
            return null;
        } else {
            if(StringUtils.isNotEmpty(citationDetail)){
                return citation.generateTitle().replaceAll("\\.$", "") + (StringUtils.isNotEmpty(citationDetail) ? ": " + citationDetail : "");
            } else {
                return citation.generateTitle();

            }

        }
    }

    /**
     * @return the blockedBy
     */
    public Set<Registration> getBlockedBy() {
        return blockedBy;
    }

    /**
     * @return
     */
    public List<String> getMessages() {
        return messages;
    }

}