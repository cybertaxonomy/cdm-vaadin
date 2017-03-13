/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.phycobank;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * @author a.kohlbecker
 * @since Mar 6, 2017
 *
 */
public class RegistrationItemComponent extends CustomComponent {

    private static final long serialVersionUID = -3585430974956346927L;

    private HorizontalLayout container;
    private Label statusIcon = new Label();
    private Label summary = new Label();
    private Button editButton = new Button("Edit");

    /**
     * @return the statusIcon
     */
    public Label getStatusIcon() {
        return statusIcon;
    }
    /**
     * @return the summary
     */
    public Label getSummary() {
        return summary;
    }
    /**
     * @return the editButton
     */
    public Button getEdit() {
        return editButton;
    }

    /**
     *
     */
    public RegistrationItemComponent() {
        container.setSpacing(true);
        container.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(statusIcon);
        summary.setWidth(100, Unit.PERCENTAGE);
        container.addComponent(summary);
        container.addComponent(editButton);
    }

}
