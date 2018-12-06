/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui.config;

import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditorMode;
import eu.etaxonomy.cdm.vaadin.view.name.TaxonNamePopupEditorView;

/**
 * @author a.kohlbecker
 * @since Sep 25, 2018
 *
 */
public class TaxonNamePopupEditorConfig {

    /**
     * Configures the TaxonNamePopupEditorView for nomenclatural act editing.
     *
     * TODO consider putting this into a Configurer Bean per UIScope.
     * In the configurator bean this methods popup parameter should be of the type
     * AbstractPopupEditor
     *
     * @param popup
     */
    public static void configureForNomenclaturalAct(TaxonNamePopupEditorView popup) {
        popup.enableMode(TaxonNamePopupEditorMode.AUTOFILL_AUTHORSHIP_DATA);
        popup.enableMode(TaxonNamePopupEditorMode.NOMENCLATURALREFERENCE_SECTION_EDITING_ONLY);
        popup.enableMode(TaxonNamePopupEditorMode.VALIDATE_AGAINST_HIGHER_NAME_PART);
        popup.enableMode(TaxonNamePopupEditorMode.REQUIRE_NOMENCLATURALREFERENCE);
    }

    /**
     * Configures the TaxonNamePopupEditorView for general purpose editing.
     *
     * @param popup
     */
    public static void configure(TaxonNamePopupEditorView popup) {
        popup.enableMode(TaxonNamePopupEditorMode.AUTOFILL_AUTHORSHIP_DATA);
        popup.enableMode(TaxonNamePopupEditorMode.VALIDATE_AGAINST_HIGHER_NAME_PART);
        popup.enableMode(TaxonNamePopupEditorMode.REQUIRE_NOMENCLATURALREFERENCE);
    }

}
