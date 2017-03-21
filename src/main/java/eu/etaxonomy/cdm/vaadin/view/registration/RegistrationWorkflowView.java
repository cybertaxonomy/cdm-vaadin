/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.UUID;

import com.vaadin.ui.CssLayout;

import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationType;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public interface RegistrationWorkflowView extends ApplicationView{


    CssLayout getWorkflow();


    /**
     * Open a popup editor for an existing TaxonName if the nameUuid is
     * given otherwise a blank editor will open if the  nameUuid is null.
     *
     * @param nameUuid can be null
     * @deprecated will be handled by the WorkflowItem
     */
    @Deprecated
    void openNameEditor(UUID nameUuid);

    public void makeWorflow(RegistrationType type);

}
