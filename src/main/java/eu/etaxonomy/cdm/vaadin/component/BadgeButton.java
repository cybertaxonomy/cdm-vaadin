/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.server.Resource;
import com.vaadin.ui.Button;

import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles;

/**
 * A button with badge. The caption of the Button is shown inside the badge.
 *
 * @author a.kohlbecker
 * @since Feb 7, 2019
 *
 */
public class BadgeButton extends Button {

    private static final long serialVersionUID = -6408645355930129935L;

    String badgeCaption = null;


    /**
     *
     */
    public BadgeButton() {
        super();
        setCaptionAsHtml(true);
    }

    /**
     * @param icon
     */
    public BadgeButton(Resource icon) {
        super(icon);
    }

    /**
     * @param caption
     * @param listener
     */
    public BadgeButton(String caption, ClickListener listener) {
        super(caption, listener);
    }

    /**
     * @param caption
     * @param icon
     */
    public BadgeButton(String caption, Resource icon) {
        super(caption, icon);
    }

    /**
     * @param caption
     */
    public BadgeButton(String caption) {
        super(caption);
    }

    @Override
    public String getCaption() {
        return badgeCaption;
    }

    @Override
    public void setCaption(String caption) {
        setCaptionAsHtml(true);
        super.setCaption("<span class=\"" + RegistrationStyles.BUTTON_BADGE +"\"> " + caption + "</span>");
    }



}
