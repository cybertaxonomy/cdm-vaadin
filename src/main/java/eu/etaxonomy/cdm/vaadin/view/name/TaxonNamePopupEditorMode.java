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
    AUTOFILL_AUTHORSHIP_DATA,

    /**
     * Editing of the nomenclatural reference is limited to editing the sub section of the
     * reference. Once the nomenclatural reference is set to a journal or book the user only can
     * change it to a reference of type {@link eu.etaxonomy.cdm.model.reference.ReferenceType.#Section Section} which
     * has the current reference as <code>inReference</code>. Whereas the book or journal can not be modified, the
     * section can be edited via a ReferencePopupEditor.
     */
    NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY,

    /**
     * setting the nomenclatural reference is required. This also accounts for names realted to the name
     * being edites in the TaxonNamePopupEditor (basionyms, validation, replaced synonmys, ...).
     */
    REQUIRE_NOMENCLATURALREFERENCE,

    /**
     * The next higher name must be in the system or it needs to be entered. For species the next higher name is
     * the genus, for sub-species it is the species, etc.
     */
    VALIDATE_AGAINST_HIGHER_NAME_PART,

    /**
     * The name relation type {@link eu.etaxonomy.cdm.model.name.NameRelationshipType#ORTHOGRAPHIC_VARIANT} will be treated more
     * strictly if this mode is activated. The related name must have the same nomenclatural reference as the name being edited.
     * see https://dev.e-taxonomy.eu/redmine/issues/7899 for more details.
     *
     * @deprecated see https://dev.e-taxonomy.eu/redmine/issues/7961
     */
    @Deprecated
    ORTHOGRAPHIC_CORRECTION,

}
