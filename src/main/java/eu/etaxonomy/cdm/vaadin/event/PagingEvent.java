/**
* Copyright (C) 2018 EDIT
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
 * @since Jul 3, 2018
 *
 */
public class PagingEvent {


    private AbstractView sourceView = null;
    private Integer pageIndex = null;
    /**
     * @param sourceView
     * @param pageIndex
     */
    public PagingEvent(AbstractView sourceView, Integer pageIndex) {
        super();
        this.setSourceView(sourceView);
        this.setPageIndex(pageIndex);
    }
    /**
     * @return the pageIndex
     */
    public Integer getPageIndex() {
        return pageIndex;
    }
    /**
     * @param pageIndex the pageIndex to set
     */
    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
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
