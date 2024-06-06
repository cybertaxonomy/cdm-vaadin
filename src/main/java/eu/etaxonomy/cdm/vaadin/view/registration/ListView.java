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

import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 */
public interface ListView extends ApplicationView<ListView,ListPresenter>{

    enum Mode {
        all,
        inProgress
    }

    void populate(Pager<RegistrationWrapperDTO> registrations);

    void openDetailsPopup(String caption, List<String> messages);

    public TextField getIdentifierFilter();

    public TextField getTaxonNameFilter();

    public TextField getReferenceFilter();

    public NativeSelect getRegistrationStatusFilter();

    public NativeSelect getSubmitterFilter();

    void setViewMode(Mode mode);

    Mode getViewMode();

    public RegistrationItem getRegistrationItem(UUID registrationUuid);

    AbstractSelect getStatusTypeFilter();
}