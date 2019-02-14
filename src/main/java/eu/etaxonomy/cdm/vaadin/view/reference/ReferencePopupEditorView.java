/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.reference;

import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.common.FilterableAnnotationsField;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
public interface ReferencePopupEditorView extends ApplicationView<ReferenceEditorPresenter> {

    public ListSelect getTypeSelect();

    public ToOneRelatedEntityCombobox<Reference> getInReferenceCombobox();

    public TeamOrPersonField getAuthorshipField();

    public FilterableAnnotationsField getAnnotationsField();

    public ToOneRelatedEntityCombobox<Institution> getInstitutionCombobox();

    public ToOneRelatedEntityCombobox<Institution> getSchoolCombobox();

}
