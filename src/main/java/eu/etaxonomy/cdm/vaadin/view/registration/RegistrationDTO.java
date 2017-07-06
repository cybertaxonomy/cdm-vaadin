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
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.SpecimenTypeDesignation;
import eu.etaxonomy.cdm.model.name.TypeDesignationBase;
import eu.etaxonomy.cdm.model.reference.INomenclaturalReference;
import eu.etaxonomy.cdm.model.reference.IReference;
import eu.etaxonomy.cdm.vaadin.model.EntityReference;
import eu.etaxonomy.cdm.vaadin.model.TypedEntityReference;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationWorkingSetDTO;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager;
import eu.etaxonomy.cdm.vaadin.util.converter.TypeDesignationSetManager.TypeDesignationWorkingSet;

public class RegistrationDTO{

    private static final Logger logger = Logger.getLogger(RegistrationDTO.class);

    private String summary = "";

    private RegistrationType registrationType;

    private IReference citation = null;

    private String citationDetail = null;

    private String submitterUserName = null;

    private EntityReference name = null;

    private TypeDesignationSetManager typeDesignationManager;

    private Registration reg;

    private List<String> messages = new ArrayList<>();

    private Set<eu.etaxonomy.cdm.model.name.Registration> blockedBy = new HashSet<>();


    /**
     * @param reg
     * @param typifiedName should be provided in for Registrations for TypeDesignations
     * @throws RegistrationValidationException
     */
    public RegistrationDTO(Registration reg) {

         this.reg = reg;

         registrationType = RegistrationType.from(reg);

         if(reg.getSubmitter() != null ){
             submitterUserName = reg.getSubmitter().getUsername();
         }

        if(hasName(reg)){
            citation = reg.getName().getNomenclaturalReference();
            citationDetail = reg.getName().getNomenclaturalMicroReference();
            name = new EntityReference(reg.getName().getId(), reg.getName().getTitleCache());
        }
        if(hasTypifications(reg)){
            if(!reg.getTypeDesignations().isEmpty()){
                for(TypeDesignationBase td : reg.getTypeDesignations()){
                    if(citation == null) {
                        citation = td.getCitation();
                        citationDetail = td.getCitationMicroReference();
                    }
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
            try {
                typeDesignationManager = new TypeDesignationSetManager(reg, reg.getTypeDesignations());
                summary = typeDesignationManager.buildString().print();
            } catch (RegistrationValidationException e) {
                messages.add("Validation errors: " + e.getMessage());
            }
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

    public String getSubmitterUserName(){
        return submitterUserName;
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


    /**
     * The entity ID of the Registration Item
     * @return
     */
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

    public EntityReference getTypifiedName() {
        return typeDesignationManager != null ? typeDesignationManager.getTypifiedName() : null;
    }

    public EntityReference getName() {
        return name;
    }

    public LinkedHashMap<TypedEntityReference, TypeDesignationWorkingSet> getOrderdTypeDesignationWorkingSets() {
        return typeDesignationManager != null ? typeDesignationManager.getOrderdTypeDesignationWorkingSets() : null;
    }

    /**
     * @param baseEntityReference
     */
    public TypeDesignationWorkingSet getTypeDesignationWorkingSet(TypedEntityReference baseEntityReference) {
        return typeDesignationManager != null ? typeDesignationManager.getOrderdTypeDesignationWorkingSets().get(baseEntityReference) : null;

    }

    /**
     * @param baseEntityReference
     */
    public Set<TypeDesignationBase> getTypeDesignationsInWorkingSet(TypedEntityReference baseEntityReference) {
        Set<TypeDesignationBase> typeDesignations = new HashSet<>();
        TypeDesignationWorkingSet workingSet = getTypeDesignationWorkingSet(baseEntityReference);
        for(EntityReference ref :  workingSet.getTypeDesignations()){
            typeDesignations.add(findTypeDesignation(ref));
        }
        return typeDesignations;
    }

    public SpecimenTypeDesignationWorkingSetDTO getSpecimenTypeDesignationWorkingSetDTO(TypedEntityReference baseEntityReference) {
        Set<TypeDesignationBase> typeDesignations = getTypeDesignationsInWorkingSet(baseEntityReference);
        List<SpecimenTypeDesignation> specimenTypeDesignations = new ArrayList<>(typeDesignations.size());
        typeDesignations.forEach(td -> specimenTypeDesignations.add((SpecimenTypeDesignation)td));
        IdentifiableEntity<?> baseEntity = getTypeDesignationWorkingSet(baseEntityReference).getBaseEntity();
        SpecimenTypeDesignationWorkingSetDTO dto = new SpecimenTypeDesignationWorkingSetDTO(reg, baseEntity, specimenTypeDesignations);
        return dto;
    }

    /**
     *
     * @param workingSetId
     * @return the TypeDesignationWorkingSet in this DTO with the matching workingSetId or NULL
     */
    public TypeDesignationWorkingSet getTypeDesignationWorkingSet(int workingSetId) {
        Optional<TypeDesignationWorkingSet> workingSetOptional = getOrderdTypeDesignationWorkingSets().values().stream().filter(workingSet -> workingSet.getWorkingSetId() == workingSetId).findFirst();
        if(workingSetOptional.isPresent()){
            return workingSetOptional.get();
        }
        return null;

    }

    /**
     * @param ref
     * @return
     */
    private TypeDesignationBase findTypeDesignation(EntityReference ref) {
        return typeDesignationManager != null ? typeDesignationManager.findTypeDesignation(ref) : null;
    }

    public Collection<TypeDesignationBase> getTypeDesignations() {
        return typeDesignationManager != null ? typeDesignationManager.getTypeDesignations() : null;
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
                // TODO see https://dev.e-taxonomy.eu/redmine/issues/6623
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