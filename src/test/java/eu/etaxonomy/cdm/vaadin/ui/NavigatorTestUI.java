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

import eu.etaxonomy.cdm.vaadin.view.NaviTestView;

@Theme("edit")
@SpringUI(path="navi")
@Widgetset("eu.etaxonomy.cdm.vaadin.AppWidgetSet")
public class NavigatorTestUI extends AbstractAuthenticatedUI {

	private static final long serialVersionUID = 4959469489638235995L;
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger();

	private static final String FIRST_VIEW = "firstView";
	public static final String SECOND_VIEW = "secondView";

	@Override
	protected void doInit(VaadinRequest request) {
		getPage().setTitle("Navigation Example");
		NaviTestView ntv1 = new NaviTestView();
		ntv1.setText("Congratulations! you have reached the first view. If you have got here without logging in there we are in trouble :)");

		NaviTestView ntv2 = new NaviTestView();
		ntv2.setText("Wow! you made it to the second view. Get yourself a beer - preferably a Krusovice Cerne");

		ntv2.removeButton();

        UI.getCurrent().getNavigator().addView(FIRST_VIEW, ntv1);
        UI.getCurrent().getNavigator().addView(SECOND_VIEW, ntv2);
	}

	@Override
	public String getFirstViewName() {
		return FIRST_VIEW;
	}
}