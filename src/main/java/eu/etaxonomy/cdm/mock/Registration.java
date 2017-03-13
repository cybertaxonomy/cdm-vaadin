/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.mock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class Registration extends CdmBase {

    private static final long serialVersionUID = -7214477130043178680L;

    private String identifier;

    private String specificIdentifier;   //id without http-domain

    private DateTime registrationDate;

    private RegistrationStatus status;

    private Institution institution;

    private TaxonNameBase name;

    private Set<TypeDesignationBase> typeDesignations = new HashSet<>();

    private Set<Registration> blockedBy = new HashSet<>();

    private User submitter;

    static int idAutoincrement = 100000;


    /**
     * @param name
     */
    public Registration() {
        super();
        status = RegistrationStatus.values()[(int) (Math.random() * RegistrationStatus.values().length)];
        specificIdentifier = Integer.toString(idAutoincrement++);
        identifier = "http://pyhcobank.org/" + specificIdentifier;
        registrationDate = DateTime.now();
    }

    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * @return the specificIdentifier
     */
    public String getSpecificIdentifier() {
        return specificIdentifier;
    }

    /**
     * @param specificIdentifier the specificIdentifier to set
     */
    public void setSpecificIdentifier(String specificIdentifier) {
        this.specificIdentifier = specificIdentifier;
    }

    /**
     * @return the registrationDate
     */
    public org.joda.time.DateTime getRegistrationDate() {
        return registrationDate;
    }

    /**
     * @param registrationDate the registrationDate to set
     */
    public void setRegistrationDate(org.joda.time.DateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * @return the status
     */
    public RegistrationStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    /**
     * @return the institution
     */
    public Institution getInstitution() {
        return institution;
    }

    /**
     * @param institution the institution to set
     */
    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    /**
     * @return the name
     */
    public TaxonNameBase getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(TaxonNameBase name) {
        this.name = name;
    }

    /**
     * @return the submitter
     */
    public User getSubmitter() {
        return submitter;
    }

    /**
     * @param submitter the submitter to set
     */
    public void setSubmitter(User submitter) {
        this.submitter = submitter;
    }

    public boolean addTypeDesignation(TypeDesignationBase typeDesignation){
        return this.typeDesignations.add(typeDesignation);
    }

    public boolean addTypeDesignations(Collection<TypeDesignationBase> typeDesignations){
        return this.typeDesignations.addAll(typeDesignations);
    }

    public boolean addBlockedBy(Registration registration){
        return blockedBy.add(registration);
    }

    /**
     * @return the typeDesignations
     */
    public Set<TypeDesignationBase> getTypeDesignations() {
        return typeDesignations;
    }

    /**
     * @return the blockedBy
     */
    public Set<Registration> getBlockedBy() {
        return blockedBy;
    }






}
