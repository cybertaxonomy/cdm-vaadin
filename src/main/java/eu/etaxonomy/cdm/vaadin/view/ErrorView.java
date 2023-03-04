/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ErrorView extends VerticalLayout implements View {

    public static final String NAME = "error";

    @Autowired
    private NavigationManager navigationManager;

    private static final long serialVersionUID = -8763747518841365925L;

    public ErrorView() {

        this.setWidth("100%");
        Label header = new Label("An error occurred");
        header.setStyleName(ValoTheme.LABEL_FAILURE);
        header.setWidthUndefined();

        addComponent(header);
        setComponentAlignment(header, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void enter(ViewChangeEvent event) {

    }
}