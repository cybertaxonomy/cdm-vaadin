/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Optional;

import com.vaadin.ui.NativeSelect;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TypeDesignationStatusBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.view.AnnotationsEditor;
import eu.etaxonomy.vaadin.component.ToManyRelatedEntitiesComboboxSelect;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 *
 */
public interface NameTypeDesignationEditorView extends ApplicationView<NameTypeDesignationPresenter>, AnnotationsEditor  {

    ToOneRelatedEntityCombobox<Reference> getDesignationReferenceCombobox();

    NativeSelect getTypeStatusSelect();

    ToManyRelatedEntitiesComboboxSelect<TaxonName> getTypifiedNamesComboboxSelect();

    ToOneRelatedEntityCombobox<TaxonName> getTypeNameField();

    void setShowTypeFlags(boolean showTypeFlags);

    boolean isShowTypeFlags();


    /**
     * possible values:
     *
     * <ul>
     * <li>NULL: undecided, should be treated like <code>false</code>. This can happen in cases when the typified name is missing the nomref</li>
     * <li>false: the typification is published in an nomenclatural act in which no new name or new combination is being published.
     * The available {@link TypeDesignationStatusBase} should be limited to those with
     * <code>{@link TypeDesignationStatusBase#hasDesignationSource() hasDesignationSource} == true</code></li>
     * <li>true: only status with <code>{@link TypeDesignationStatusBase#hasDesignationSource() hasDesignationSource} == true</li>
     * </ul>
     */
    public void setInTypedesignationOnlyAct(Optional<Boolean> isInTypedesignationOnlyAct);

    /**
     * possible values:
     *
     * <ul>
     * <li>NULL: undecided, should be treated like <code>false</code>. This can happen in cases when the typified name is missing the nomref</li>
     * <li>false: the typification is published in an nomenclatural act in which no new name or new combination is being published.
     * The available {@link TypeDesignationStatusBase} should be limited to those with
     * <code>{@link TypeDesignationStatusBase#hasDesignationSource() hasDesignationSource} == true</code></li>
     * <li>true: only status with <code>{@link TypeDesignationStatusBase#hasDesignationSource() hasDesignationSource} == true</li>
     * </ul>
     */
    public Optional<Boolean> isInTypedesignationOnlyAct();

}
