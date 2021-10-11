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
import com.vaadin.ui.Button;
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

    public ToOneRelatedEntityCombobox<Reference> getNomReferenceCombobox();

    public ToManyRelatedEntitiesComboboxSelect<TaxonName> getBasionymComboboxSelect();

    public NativeSelect getRankSelect();

    public Button getRankSelectFullListToggle();

    public TeamOrPersonField getExBasionymAuthorshipField();

    public TeamOrPersonField getBasionymAuthorshipField();

    public TeamOrPersonField getCombinationAuthorshipField();

    public TeamOrPersonField getExCombinationAuthorshipField();

    void disableMode(TaxonNamePopupEditorMode mode);

    /**
     * Modes must be enabled before calling {@link AbstractPopupEditor#loadInEditor(Object identifier)}.
     *
     * @param mode
     */
    void enableMode(TaxonNamePopupEditorMode mode);

    boolean isModeEnabled(TaxonNamePopupEditorMode mode);

    public EnumSet<TaxonNamePopupEditorMode> getModesActive();

    CheckBox getBasionymToggle();

    void updateAuthorshipFields();

    ToManyRelatedEntitiesComboboxSelect<TaxonName> getReplacedSynonymsComboboxSelect();

    NameRelationField getValidationField();

    AbstractField<String> getGenusOrUninomialField();

    public AbstractField<String> getInfraGenericEpithetField();

    public AbstractField<String> getSpecificEpithetField();

    public AbstractField<String> getInfraSpecificEpithetField();

    public NameRelationField getOrthographicVariantField();

    CheckBox getOrthographicVariantToggle();

    TextField getNomenclaturalReferenceDetail();

    ElementCollectionField<NomenclaturalStatusDTO> getNomStatusCollectionField();

    void applyDefaultComponentStyle(Component[] components);

    /**
     * @return a flag indicating if the rank select should offer the full list of ranks or a compact list
     */
    public boolean isRanksFullList();

}
