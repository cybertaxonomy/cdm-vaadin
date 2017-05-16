/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.model.registration;

import org.apache.commons.lang.StringEscapeUtils;

import eu.etaxonomy.cdm.mock.RegistrationStatus;

/**
 * @author a.kohlbecker
 * @since Mar 28, 2017
 *
 */
public enum WorkflowStep {

    PUBLICATION_DETAILS(0, "Publication"),
    NAMES_N_TYPES(1, "Names & Types"),
    CURATION(2, "Curation"),
    AWAITING_PUBLICATION(3, "Awaiting publication"),
    PUBLISHED(3, "Awaiting publication"),
    REJECTED(3, "Rejected");

    private String representation;
    private int stepIndex = -1;

    private WorkflowStep(int stepIndex, String representation){
        this.stepIndex = stepIndex;
        this.representation = representation;
    }

    /**
     * @return the representation
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * @return the stepIndex
     */
    public int getStepIndex() {
        return stepIndex;
    }

    public String getHtml(){
        return StringEscapeUtils.escapeHtml(getRepresentation());
    }

    public static WorkflowStep from(RegistrationStatus status, boolean citationDetailsReady) {
        if(!citationDetailsReady){
            return PUBLICATION_DETAILS;
        } else {
            switch(status) {
            case CURATION:
                return CURATION;
            case PUBLISHED:
                return PUBLISHED;
            case REJECTED:
                return REJECTED;
            case PREPARATION:
                return PUBLICATION_DETAILS;
            case READY:
                return AWAITING_PUBLICATION;
            default:
                /* this should never happen */
                return null;
            }
        }
    }
}
