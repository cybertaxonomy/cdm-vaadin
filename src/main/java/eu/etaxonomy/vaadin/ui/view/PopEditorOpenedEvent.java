/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.view;

import org.springframework.context.ApplicationEvent;

/**
 * @author a.kohlbecker
 * @since Jan 22, 2018
 *
 */
public class PopEditorOpenedEvent extends ApplicationEvent {

    private static final long serialVersionUID = -1258659977737677080L;

    private PopupView popupView;

    /**
     * @param source
     */
    public PopEditorOpenedEvent(Object source, PopupView popupView) {
        super(source);
        this.popupView = popupView;
    }

    public PopupView getPopupView(){
        return popupView;
    }

}
