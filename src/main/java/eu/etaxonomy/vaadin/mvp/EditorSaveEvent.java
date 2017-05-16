/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public class EditorSaveEvent {

    private CommitEvent commitEvent;

    /**
     * @param commitEvent
     */
    public EditorSaveEvent(CommitEvent commitEvent) {
        this.commitEvent = commitEvent;
    }

    public CommitEvent getCommitEvent(){
        return commitEvent;
    }

}
