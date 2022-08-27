/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.component.taxon.StatusComposite;

/**
 * @author cmathew
 * @since 11 Mar 2015
 */
@Theme("edit")
@SpringUI(path=StatusEditorUI.NAME)
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class StatusEditorUI extends AbstractAuthenticatedUI {

    private static final long serialVersionUID = 7979880076776241573L;
    @SuppressWarnings("unused")
    private final Logger logger = LogManager.getLogger();

    public static final String NAME = "status";
    private static final String FIRST_VIEW = "editstatus";

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