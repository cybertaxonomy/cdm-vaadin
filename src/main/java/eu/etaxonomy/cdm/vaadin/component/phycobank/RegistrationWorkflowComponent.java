/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.phycobank;

import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.vaadin.design.phycobank.RegistrationWorkflowDesign;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public class RegistrationWorkflowComponent extends RegistrationWorkflowDesign {

    private static final long serialVersionUID = 7196791015737342650L;

    /**
     * @return the title
     */
    public Label getTitle() {
        return title;
    }

    /**
     * @return the workflow
     */
    public CssLayout getWorkflow() {
        return workflow;
    }

}
