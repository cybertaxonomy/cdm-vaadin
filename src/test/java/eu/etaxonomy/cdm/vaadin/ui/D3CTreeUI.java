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
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

import eu.etaxonomy.cdm.vaadin.jscomponent.D3CTree;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
@Theme("mytheme")
public class D3CTreeUI extends UI {

    private final static Logger logger =
            Logger.getLogger(D3CTreeUI.class.getName());

    @WebServlet(value = {"/app-test/d3ctree/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = D3CTreeUI.class, widgetset = "eu.etaxonomy.cdm.vaadin.AppWidgetSet")
    public static class Servlet extends VaadinServlet {

    }

    final VerticalLayout layout = new VerticalLayout();
    final D3CTree d3ctree = new D3CTree();


    @Override
    protected void init(VaadinRequest request) {
        layout.addComponent(d3ctree);     //add the diagram like any other vaadin component, cool!
        setContent(layout);
    }


}
