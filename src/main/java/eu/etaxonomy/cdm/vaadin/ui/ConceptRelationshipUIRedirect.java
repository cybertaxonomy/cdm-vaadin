/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;


import org.apache.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.view.ConceptRelationshipView;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
@Theme("edit")
// @SpringUI(path="concept") // not needed since this UI is used in the context of ConceptRelationshipUI
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class ConceptRelationshipUIRedirect extends AbstractAuthenticatedUI {

    Navigator navigator;

    private static final String FIRST_VIEW = "editcr";

    private final static Logger logger = Logger.getLogger(ConceptRelationshipUIRedirect.class);

    @Override
    protected void doInit(VaadinRequest request) {
        // FIXME: remove this when testing is done
        //setIgnoreAuthentication(true);

        getPage().setTitle("Concept Relationship Editor");
        logger.warn("original classification : " + request.getParameter("oc"));
        logger.warn("copy classification : " + request.getParameter("cc"));
        String oc = request.getParameter("oc");
        String cc = request.getParameter("cc");

        ConceptRelationshipView crEditor = new ConceptRelationshipView(oc,cc);
        UI.getCurrent().getNavigator().addView(FIRST_VIEW, crEditor);

    }

    @Override
    public String getFirstViewName() {
        return FIRST_VIEW;
    }

}
