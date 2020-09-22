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

import org.vaadin.viritin.fields.ElementCollectionField;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.common.TeamOrPersonField;
import eu.etaxonomy.cdm.vaadin.model.name.NomenclaturalStatusDTO;
import eu.etaxonomy.cdm.vaadin.view.AnnotationsEditor;
import eu.etaxonomy.vaadin.component.NameRelationField;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public interface TaxonNamePopupEditorView extends ApplicationView<TaxonNameEditorPresenter>, AnnotationsEditor {

    /**
     * @return
     */
    public ToOneRelatedEntityCombobox<Reference> getNomReferenceCombobox();

    /**
     * @return
     */
    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getBasionymComboboxSelect();

    public NativeSelect getRankSelect();

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

    /**
     * @return
     */
    NameRelationField getValidationField();

    /**
     * @return
     */
    AbstractField<String> getGenusOrUninomialField();

    /**
     * @return the infraGenericEpithetField
     */
    public AbstractField<String> getInfraGenericEpithetField();

    /**
     * @return the specificEpithetField
     */
    public AbstractField<String> getSpecificEpithetField();

    /**
     * @return the infraSpecificEpithetField
     */
    public AbstractField<String> getInfraSpecificEpithetField();

    public NameRelationField getOrthographicVariantField();

    CheckBox getOrthographicVariantToggle();

    /**
     * @return
     */
    TextField getNomenclaturalReferenceDetail();

    ElementCollectionField<NomenclaturalStatusDTO> getNomStatusCollectionField();

    /**
     * @param components
     */
    void applyDefaultComponentStyle(Component[] components);


}
