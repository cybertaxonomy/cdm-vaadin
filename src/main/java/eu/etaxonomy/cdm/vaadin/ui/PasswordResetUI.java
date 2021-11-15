/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator.SingleComponentContainerViewDisplay;
import com.vaadin.navigator.ViewDisplay;
import com.vaadin.spring.annotation.SpringUI;

import eu.etaxonomy.cdm.vaadin.view.PasswordResetViewBean;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
@Theme("edit-valo")
@Title("Password Reset")
@SpringUI(path=PasswordResetUI.PATH)
@Viewport("width=device-width, initial-scale=1")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class PasswordResetUI extends AbstractUI {

    private static final long serialVersionUID = 8553850288038649061L;

    public static final String PATH = "passwordReset";

    private ViewDisplay viewDisplay = null;

    @Override
    protected ViewDisplay getViewDisplay() {
        if(viewDisplay == null) {
            viewDisplay = new SingleComponentContainerViewDisplay(this);
        }
        return viewDisplay;
    }

    @Override
    protected String getInitialViewName() {
        return PasswordResetViewBean.NAME;
    }

    @Override
    protected void initAdditionalContent() {
        // no additional content
    }

}
