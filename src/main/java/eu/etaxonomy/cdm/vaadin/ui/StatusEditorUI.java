/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

/**
 * @author cmathew
 * @date 11 Mar 2015
 *
 */


import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.server.SpringVaadinServlet;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.component.StatusComposite;


@Theme("edit")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class StatusEditorUI extends AbstractAuthenticatedUI {

    Navigator navigator;

    private static final String FIRST_VIEW = "editstatus";

    private final static Logger logger =
            Logger.getLogger(StatusEditorUI.class.getName());
    /*
     * NOTE: I it necessary to map the URLs starting with /VAADIN/* since none of the
     * @WebServlets is mapped to the root path. It is sufficient to configure one of the
     * servlets with this path see BookOfVaadin 5.9.5. Servlet Mapping with URL Patterns
     */
    @WebServlet(value = {"/app/editstatus/*", "/VAADIN/*"}, asyncSupported = true)
    public static class Servlet extends SpringVaadinServlet {
    }

    @Override
    protected void doInit(VaadinRequest request) {
        // FIXME: remove this when testing is done
        //setIgnoreAuthentication(true);

        getPage().setTitle("Status Editor");
        StatusComposite statusEditor = new StatusComposite();
        UI.getCurrent().getNavigator().addView(FIRST_VIEW, statusEditor);

    }

    @Override
    public String getFirstViewName() {
        return FIRST_VIEW;
    }

}
