/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 *
 */
public interface NameTypeDesignationEditorView extends ApplicationView<NameTypeDesignationPresenter> {

    ToOneRelatedEntityCombobox<Reference> getCitationCombobox();

    ListSelect getTypeStatusSelect();

    ToManyRelatedEntitiesComboboxSelect<TaxonName> getTypifiedNamesComboboxSelect();

    ToOneRelatedEntityCombobox<TaxonName> getTypeNameField();

    void setShowTypeFlags(boolean showTypeFlags);

    boolean isShowTypeFlags();

}
