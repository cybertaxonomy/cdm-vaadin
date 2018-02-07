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

import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public interface ListView extends ApplicationView<ListPresenter>{

    enum Mode {
        all,
        inProgress
    }

    /**
     * @param page
     */
    void populate(Pager<RegistrationDTO> registrations);

    /**
     * @param messages
     */
    void openDetailsPopup(String caption, List<String> messages);

    /**
     * @return the identifierFilter
     */
    public TextField getIdentifierFilter();

    /**
     * @return the taxonNameFilter
     */
    public TextField getTaxonNameFilter();

    /**
     * @return the referenceFilter
     */
    public TextField getReferenceFilter();

    /**
     * @return the statusFilter
     */
    public ListSelect getStatusFilter();

    /**
     * @return the submitterFilter
     */
    public ListSelect getSubmitterFilter();

    /**
     * @param optionInProgress
     */
    void setViewMode(Mode mode);

    /**
     * @return
     */
    Mode getViewMode();

    public RegistrationItem getRegistrationItem(int registrationId);


}
