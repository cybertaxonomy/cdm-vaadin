/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.registration;

import java.util.UUID;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public class RegistrationWorkingsetAction {

    private Action action;
    private UUID citationUuid = null;

    /**
     *
     * @param citationID the id of a {@link Reference} denoting a
     * complete registration working set.
     */
    public RegistrationWorkingsetAction(UUID citationUuid, Action action){
        this.action = action;
        this.citationUuid = citationUuid;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }

    /**
     * @return the citationUuid
     */
    public UUID getCitationUuid() {
        return citationUuid;
    }

    public boolean isStart() {
        return action.equals(Action.start);
    }


    public enum Action {
        start, open;
    }

}
