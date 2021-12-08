/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.HashSet;
import java.util.Set;

import eu.etaxonomy.cdm.api.service.dto.TypeDesignationStatusFilter;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.permission.User;

class RegistrationSearchFilter {
    private String identifierPattern;
    private String namePattern;
    private String referencePattern;
    private User submitter;
    private Set<TypeDesignationStatusFilter> typeStatus = new HashSet<>();
    private Set<RegistrationStatus> registrationStatus;

    public String getIdentifierPattern() {
        return identifierPattern;
    }
    public void setIdentifierPattern(String identifierPattern) {
        this.identifierPattern = identifierPattern;
    }
    public String getNamePattern() {
        return namePattern;
    }
    public void setNamePattern(String namePattern) {
        this.namePattern = namePattern;
    }
    public String getReferencePattern() {
        return referencePattern;
    }
    public void setReferencePattern(String referencePattern) {
        this.referencePattern = referencePattern;
    }
    public User getSubmitter() {
        return submitter;
    }
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }
    /**
     *
     * @return the TypeDesignationStatusFilter set, never NULL
     */
    public Set<TypeDesignationStatusFilter> getTypeStatus() {
        return typeStatus;
    }
    public void setTypeStatus(Set<TypeDesignationStatusFilter> typeStatus) {
        this.typeStatus = typeStatus;
    }
    public Set<RegistrationStatus> getRegistrationStatus() {
        return registrationStatus;
    }
    public void setRegistrationStatus(Set<RegistrationStatus> registrationStatus) {
        if(registrationStatus != null) {
            this.registrationStatus.clear();
            this.registrationStatus.addAll(registrationStatus);
        }
    }


}