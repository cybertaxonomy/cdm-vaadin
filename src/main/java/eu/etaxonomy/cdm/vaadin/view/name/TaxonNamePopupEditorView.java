/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.EnumSet;

import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public interface TaxonNamePopupEditorView extends ApplicationView<TaxonNameEditorPresenter> {

    /**
     * @return
     */
    public ToOneRelatedEntityCombobox<Reference> getNomReferenceCombobox();

    /**
     * @return
     */
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getBasionymComboboxSelect();

    public ListSelect getRankSelect();

    /**
     * @return the exBasionymAuthorshipField
     */
    public TeamOrPersonField getExBasionymAuthorshipField();

    /**
     * @return the basionymAuthorshipField
     */
    public TeamOrPersonField getBasionymAuthorshipField();

    /**
     * @return the combinationAuthorshipField
     */
    public TeamOrPersonField getCombinationAuthorshipField();

    /**
     * @return the exCombinationAuthorshipField
     */
    public TeamOrPersonField getExCombinationAuthorshipField();

    void disableMode(TaxonNamePopupEditorMode mode);

    /**
     * Modes must be enabled before calling {@link AbstractPopupEditor#loadInEditor(Object identifier)}.
     *
     * @param mode
     */
    void enableMode(TaxonNamePopupEditorMode mode);

    /**
     * @param mode
     * @return
     */
    boolean isModeEnabled(TaxonNamePopupEditorMode mode);

    public EnumSet<TaxonNamePopupEditorMode> getModesActive();

    /**
     * @return
     */
    CheckBox getBasionymToggle();

    void updateAuthorshipFields();

    /**
     * @return
     */
    ToManyRelatedEntitiesComboboxSelect<TaxonName> getReplacedSynonymsComboboxSelect();

}
