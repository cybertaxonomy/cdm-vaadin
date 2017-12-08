/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Dec 7, 2017
 *
 */
public class UpdateResultsEvent {

    private AbstractView sourceView = null;


    /**
     * @param sourceView
     */
    public UpdateResultsEvent(AbstractView sourceView) {
        super();
        this.sourceView = sourceView;
    }

    /**
     * @return the sourceView
     */
    public AbstractView getSourceView() {
        return sourceView;
    }

    /**
     * @param sourceView the sourceView to set
     */
    public void setSourceView(AbstractView sourceView) {
        this.sourceView = sourceView;
    }



}
