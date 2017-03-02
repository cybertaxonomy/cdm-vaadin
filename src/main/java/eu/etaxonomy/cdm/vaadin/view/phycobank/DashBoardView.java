/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.phycobank;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.Responsive;
import com.vaadin.spring.annotation.SpringView;

import eu.etaxonomy.cdm.vaadin.design.phycobank.DashBoardDesign;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=DashBoardView.NAME)
public class DashBoardView extends DashBoardDesign implements View {

    public static final String NAME = "dashboard";

    private static final long serialVersionUID = -6172448806905158782L;

    public DashBoardView() {
        Responsive.makeResponsive(dashboard);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {
        // TODO Auto-generated method stub

    }

}
