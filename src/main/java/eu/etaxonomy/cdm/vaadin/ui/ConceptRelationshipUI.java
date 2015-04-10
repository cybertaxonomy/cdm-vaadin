// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import java.util.logging.Logger;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.ConceptRelationshipView;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
@Theme("edit")
public class ConceptRelationshipUI extends AbstractAuthenticatedUI {

    Navigator navigator;

    private static final String FIRST_VIEW = "editcr";

    private final static Logger logger =
            Logger.getLogger(ConceptRelationshipUI.class.getName());

    @WebServlet(value = {"/app/editcr/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = ConceptRelationshipUI.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void doInit() {
        // FIXME: remove this when testing is done
        //setIgnoreAuthentication(true);

        getPage().setTitle("Concept Relationship Editor");
        ConceptRelationshipView crEditor = new ConceptRelationshipView();
        UI.getCurrent().getNavigator().addView(FIRST_VIEW, crEditor);

    }

    @Override
    public String getFirstViewName() {
        return FIRST_VIEW;
    }

}
