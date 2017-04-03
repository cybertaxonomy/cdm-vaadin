/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.List;
import java.util.UUID;

import com.vaadin.ui.CssLayout;

import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO;
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
     */
    void openNameEditor(UUID nameUuid);

    /**
     * Open a popup editor for an existing Reference if the referenceUuid is
     * given otherwise a blank editor will open if the  referenceUuid is null.
     *
     * @param referenceUuid can be null
     */
    void openReferenceEditor(UUID referenceUuid);

    /**
     * @param subheaderText
     */
    void setSubheaderText(String subheaderText);

    /**
     * @param subheaderText
     */
    void setHeaderText(String subheaderText);

    /**
     * @param workingset
     */
    void setWorkingset(RegistrationWorkingSet workingset);

    void addBlockingRegistration(RegistrationDTO blocking);

    /**
     * @param messages
     */
    void openDetailsPopup(String caption, List<String> messages);



}
