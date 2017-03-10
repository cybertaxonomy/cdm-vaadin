/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.phycobank;

import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public class RegistrationWorkflowEvent {

    private RegistrationType type = null;
    private Action action;
    private Integer registrationID = null;

    public RegistrationWorkflowEvent(int registrationID){
        this.action = Action.open;
        this.registrationID = registrationID;
    }

    public RegistrationWorkflowEvent(RegistrationType type){
        this.type = type;
        this.action = Action.start;
    }

    /**
     * @return the type
     */
    public RegistrationType getType() {
        return type;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the registrationID
     */
    public Integer getRegistrationID() {
        return registrationID;
    }

    public boolean isStart() {
        return action.equals(Action.start);
    }


    enum Action {
        start, open;
    }

}