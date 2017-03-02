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
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
@SpringView(name=ListView.NAME)
public class ListView extends VerticalLayout implements View {

    private static final long serialVersionUID = 3543300933072824713L;

    public static final String NAME = "list";

    public ListView() {
        addComponent(new Label("List View"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {

    }

}
