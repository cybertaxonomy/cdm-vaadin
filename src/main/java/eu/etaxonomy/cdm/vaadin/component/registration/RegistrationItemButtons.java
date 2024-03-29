/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.component.BadgeButton;

/**
 * @author a.kohlbecker
 * @since Feb 27, 2018
 *
 */
public class RegistrationItemButtons extends CssLayout {

    private static final long serialVersionUID = 6807283645907730475L;

    private Button blockingRegistrationButton;

    private BadgeButton validationProblemsButton;

    public RegistrationItemButtons() {

          setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

          blockingRegistrationButton = new Button(FontAwesome.BAN);
          blockingRegistrationButton.setEnabled(false);

          validationProblemsButton = new BadgeButton(FontAwesome.WARNING);
          validationProblemsButton.setEnabled(false);

          addComponents(blockingRegistrationButton, validationProblemsButton);
    }

    public Button getBlockingRegistrationButton() {
        return blockingRegistrationButton;
    }

    public BadgeButton getValidationProblemsButton() {
        return validationProblemsButton;
    }
}