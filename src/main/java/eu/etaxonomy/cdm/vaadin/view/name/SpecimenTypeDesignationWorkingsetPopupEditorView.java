/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import org.vaadin.viritin.fields.ElementCollectionField;

import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.model.registration.SpecimenTypeDesignationDTO;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Jun 13, 2017
 *
 */
public interface SpecimenTypeDesignationWorkingsetPopupEditorView extends ApplicationView<SpecimenTypeDesignationWorkingsetEditorPresenter> {

    public ListSelect getCountrySelectField();

    ElementCollectionField<SpecimenTypeDesignationDTO> getTypeDesignationsCollectionField();

    void applyDefaultComponentStyle(Component ... components);

    /**
     *  Disable the delete button if there is only one typeDesignation
     *  if this typeDesignation is deleted the fieldUnit would become orphan in the
     *  TypeDesignationWorkingSet
     */
    public void updateAllowDelete();

    TeamOrPersonField getCollectorField();
}
