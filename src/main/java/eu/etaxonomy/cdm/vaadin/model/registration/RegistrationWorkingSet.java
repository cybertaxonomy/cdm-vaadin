/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationStatus;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationValidationException;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class RegistrationWorkingSet {

    private List<RegistrationDTO> registrationDTOs = new ArrayList<>();

    private int citationId = -1;

    private String citation = null;

    /**
     * Creates an empty working set
     */
    public RegistrationWorkingSet() {

    }

    public RegistrationWorkingSet(List<RegistrationDTO> registrationDTOs) throws RegistrationValidationException {
        validateAndAddDTOs(registrationDTOs, null);
    }

    /**
     * @param candidated
     * @throws RegistrationValidationException
     *
     */
    private void validateAndAdd(Set<Registration> candidates) throws RegistrationValidationException {
        List<RegistrationDTO> dtos = new ArrayList<>(registrationDTOs.size());
        candidates.forEach(reg -> dtos.add(new RegistrationDTO(reg)));
        validateAndAddDTOs(dtos, null);
    }

    /**
     * Validate and add all Registrations to the working set which are referring to the same publication
     * which is either the citation of the nomenclatural reference of the {@link TaxonNameBase} or the
     * citation of the {@link TypeDesignations}. Registration with a differing publication are not added to
     * the working set, instead a {@link RegistrationValidationException} is thrown which is a container for
     * all validation problems.
     *
     * @param candidates
     * @param problems Problems detected in prior validation and processing passed to this method to be completed.
     * @throws RegistrationValidationException
     */
    private void validateAndAddDTOs(List<RegistrationDTO> candidates, List<String> problems) throws RegistrationValidationException {
        if(problems == null){
            problems = new ArrayList<>();
        }
        for(RegistrationDTO regDto : candidates){
                if(citationId == -1){
                    citationId = regDto.getCitationID();
                    citation = regDto.getCitation();
                } else {
                    if(regDto.getCitationID() != citationId){
                        problems.add("Removing Registration " + regDto.registration().toString() + " from set since this refers to a different citation.");
                        continue;
                    }
                }
                this.registrationDTOs.add(regDto);
        }

        if(!problems.isEmpty()){
            throw new RegistrationValidationException("", problems);
        }

    }

    /**
     * @param reg
     * @throws RegistrationValidationException
     */
    public void add(Registration reg) throws RegistrationValidationException {
        Set<Registration> candidates = new HashSet<>();
        candidates.add(reg);
        validateAndAdd(candidates);
    }

    /**
     * @return the registrations
     */
    public List<Registration> getRegistrations() {
        List<Registration> regs = new ArrayList<>(registrationDTOs.size());
        registrationDTOs.forEach(dto -> regs.add(dto.registration()));
        return regs;
    }

    /**
     * Calculates the total count of messages in the registrations contained
     * in the working set.
     *
     * @return
     */
    public int messagesCount() {
        int messagesCount = 0;
        for(RegistrationDTO dto : getRegistrationDTOs()) {
            messagesCount = messagesCount + dto.getMessages().size();
        }
        return messagesCount;
    }

    /**
     * Finds the lowest status in the registrations contained
     * in the working set.
     *
     * @return
     */
    public RegistrationStatus lowestStatus() {
        RegistrationStatus status = RegistrationStatus.REJECTED;
        for(RegistrationDTO dto : getRegistrationDTOs()) {
            if(dto.getStatus().compareTo(status) < 0){
                status = dto.getStatus();
            }
        }
        return status;
    }


    /**
     * @return the registrations
     */
    public List<RegistrationDTO> getRegistrationDTOs() {
        return registrationDTOs;
    }

    /**
     * @return the citationId
     */
    public int getCitationId() {
        return citationId;
    }

    /**
     * @return the citation
     */
    public String getCitation() {
        return citation;
    }

}
