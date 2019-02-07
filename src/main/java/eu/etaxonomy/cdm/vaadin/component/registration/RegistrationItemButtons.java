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

/**
 * @author a.kohlbecker
 * @since Feb 27, 2018
 *
 */
public class RegistrationItemButtons extends CssLayout {


    private static final long serialVersionUID = 6807283645907730475L;

    Button blockingRegistrationButton;

    Button validationProblemsButton;

    public RegistrationItemButtons() {

          setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);

          blockingRegistrationButton = new Button(FontAwesome.BAN);
          blockingRegistrationButton.setEnabled(false);

          validationProblemsButton = new Button(FontAwesome.WARNING);
          validationProblemsButton.setEnabled(false);

          addComponents(blockingRegistrationButton, validationProblemsButton);
    }

    /**
     * @return the blockingRegistrationButton
     */
    public Button getBlockingRegistrationButton() {
        return blockingRegistrationButton;
    }

    /**
     * @return the validationProblemsButton
     */
    public Button getValidationProblemsButton() {
        return validationProblemsButton;
    }

}
