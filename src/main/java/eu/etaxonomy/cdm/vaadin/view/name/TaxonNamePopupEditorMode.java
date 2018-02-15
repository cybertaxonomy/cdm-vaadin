/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

/**
 * @author a.kohlbecker
 * @since Jan 11, 2018
 *
 */
public enum TaxonNamePopupEditorMode {

    /**
     * CombinationAuthorship BasionymAuthorship fields only visible if
     * not empty
     */
    suppressReplacementAuthorshipData,

    /**
     * Editing of the nomenclatural reference is limited to editing the sub section of the
     * reference. Once the nomenclatural reference is set to a journal or book the user only can
     * change it to a reference of type {@link eu.etaxonomy.cdm.model.reference.ReferenceType.#Section Section} which
     * has the current reference as <code>inReference</code>. Whereas the book or journal can not be modified, the
     * section can be edited via a ReferencePopupEditor.
     *
     */
    nomenclaturalReferenceSectionEditingOnly,

    /**
     * setting the nomenclatural reference is required with the exception
     * that existing data is considered complete if the combination
     * authors are set.
     */
    requireNomenclaturalReference

}
