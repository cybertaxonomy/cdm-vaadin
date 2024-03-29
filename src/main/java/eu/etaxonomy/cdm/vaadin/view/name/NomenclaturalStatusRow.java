/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.CollectionRow;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowItemCollection;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;

/**
 * @author a.kohlbecker
 * @since Apr 29, 2019
 */
public class NomenclaturalStatusRow extends CollectionRowItemCollection implements CollectionRow {

    private static final long serialVersionUID = -4088064849794846181L;

    /*
     * CONVENTION!
     *
     * The fieldname must match the properties of the NomenclaturalStatusDTO
     */
    NativeSelect type = new NativeSelect();
    ToOneRelatedEntityCombobox<Reference> citation = new ToOneRelatedEntityCombobox<>(null, Reference.class);
    TextField citationMicroReference = new TextFieldNFix();
    TextField ruleConsidered = new TextFieldNFix();
    NativeSelect codeEdition = new NativeSelect();

    public NomenclaturalStatusRow() {

        citation.setWidth(250, Unit.PIXELS);
        citation.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(citation));
        citationMicroReference.setWidth(100, Unit.PIXELS);
        ruleConsidered.setWidth(100, Unit.PIXELS);
        codeEdition.setWidth(110, Unit.PIXELS);
    }

    /**
     * @return the components of this NomenclaturalStatusRow
     *         in the order to display
     */
    public Component[] components() {
        Component[] components = new Component[]{
                type,
                ruleConsidered,
                codeEdition,
                citation,
                citationMicroReference,
            };
        addAll(Arrays.asList(components));
        return components;
    }

    public static List<String> visibleFields() {
        List<String> visibleFields = Arrays.asList(new String[]{
            "type",
            "ruleConsidered",
            "codeEdition",
            "citation",
            "citationMicroReference",
            });
        return visibleFields;
    }

    @Override
    public void updateRowItemsEnablement() {
        // nothing to do
    }
}