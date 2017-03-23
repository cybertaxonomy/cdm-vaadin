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
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationValidationException;

/**
 * @author a.kohlbecker
 * @since Mar 22, 2017
 *
 */
public class RegistrationWorkingSet {

    private Set<Registration> registrations = new HashSet<>();

    private int citationId = -1;

    private String citation = null;

    public RegistrationWorkingSet(Set<Registration> registrations) throws RegistrationValidationException {

        validateAndAdd(registrations);
    }

    /**
     * @param candidated
     * @throws RegistrationValidationException
     *
     */
    private void validateAndAdd(Set<Registration> candidated) throws RegistrationValidationException {
        List<String> problems = new ArrayList<>();
        for(Registration reg : candidated){
            try {
                RegistrationDTO regDto = new RegistrationDTO(reg);
                if(citationId == -1){
                    citationId = regDto.getCitationID();
                    citation = regDto.getCitation();
                } else {
                    if(regDto.getCitationID() != citationId){
                        problems.add("Removing Registration " + reg.toString() + " from set since this refers to a different citation.\n");
                        continue;
                    }
                }
                this.registrations.add(reg);

            } catch (RegistrationValidationException e) {
                problems.add(e.getMessage());
            }
        }

        if(!problems.isEmpty()){
            throw new RegistrationValidationException(problems.toString());
        }

    }

    public boolean add(Registration registration){
        return registrations.add(registration);
    }

    /**
     * @return the registrations
     */
    public Set<Registration> getRegistrations() {
        return registrations;
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
