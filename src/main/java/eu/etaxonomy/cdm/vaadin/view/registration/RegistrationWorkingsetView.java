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
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.ui.Button;

import eu.etaxonomy.cdm.api.service.dto.RegistrationDTO;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStatusFieldInstantiator;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 */
public interface RegistrationWorkingsetView
        extends ApplicationView<RegistrationWorkingsetView,RegistrationWorkingsetPresenter> {

    public static final String ACTION_NEW = "new";

    public static final String ACTION_EDIT = "edit";

    /**
     * Open a popup editor for an existing TaxonName if the nameUuid is
     * given otherwise a blank editor will open if the  nameUuid is null.
     *
     * @param nameUuid can be null
     */
    public void openNameEditor(UUID nameUuid);

    /**
     * Open a popup editor for an existing Reference if the referenceUuid is
     * given otherwise a blank editor will open if the  referenceUuid is null.
     *
     * @param referenceUuid can be null
     */
    public void openReferenceEditor(UUID referenceUuid);

    public void setSubheaderText(String subheaderText);

    public void setHeaderText(String subheaderText);

    public void setWorkingset(RegistrationWorkingSet workingset);

    public void openDetailsPopup(String caption, List<String> messages);

    public Button getAddNewNameRegistrationButton();

    public Button getAddExistingNameRegistrationButton();

    public LazyComboBox<TaxonName> getExistingNameCombobox();

    public UUID getCitationUuid();

    public void setBlockingRegistrations(UUID registrationUuid, Set<RegistrationDTO> blockingRegDTOs);

    /**
     * Returns the registrationItemMap as unmodifiableMap.
     */
    public Map<UUID, RegistrationDetailsItem> getRegistrationItemMap();

    public void setStatusComponentInstantiator(RegistrationStatusFieldInstantiator statusComponentInstantiator);

}