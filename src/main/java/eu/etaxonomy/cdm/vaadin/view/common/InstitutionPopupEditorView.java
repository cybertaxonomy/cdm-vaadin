/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.common;

import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Dec 21, 2017
 *
 */
public interface InstitutionPopupEditorView extends ApplicationView<InstitutionPopupEditorView,InstitutionEditorPresenter> {

    ToOneRelatedEntityCombobox<Institution> getPartOfCombobox();

}
