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
 * Usually a notification to a presenter to
 * prepare for a save operation by starting a transaction
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public class EditorPreSaveEvent {

    private CommitEvent commitEvent;

    // FIXME this is only a iterim solution for the problem described in https://dev.e-taxonomy.eu/redmine/issues/6562
    private AbstractView view;

    /**
     * @param commitEvent
     */
    public EditorPreSaveEvent(CommitEvent commitEvent, AbstractView view) {
        this.commitEvent = commitEvent;
        this.view = view;
    }

    public CommitEvent getCommitEvent(){
        return commitEvent;
    }

    /**
     * @return the view
     */
    public AbstractView getView() {
        return view;
    }

}
