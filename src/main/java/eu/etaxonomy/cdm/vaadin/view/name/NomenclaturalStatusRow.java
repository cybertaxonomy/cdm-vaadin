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
 *
 */
public class NomenclaturalStatusRow extends CollectionRowItemCollection implements CollectionRow {


    private static final long serialVersionUID = -4088064849794846181L;

    /*
     * CONVENTION!
     *
     * The fieldname must match the properties of the SpecimenTypeDesignationDTO
     */
    NativeSelect type = new NativeSelect();
    ToOneRelatedEntityCombobox<Reference> citation = new ToOneRelatedEntityCombobox<Reference>(null, Reference.class);
    TextField citationMicroReference = new TextFieldNFix();
    TextField ruleConsidered = new TextFieldNFix();

    public NomenclaturalStatusRow() {

        citation.setWidth(200, Unit.PIXELS);
        citation.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(citation));
        citationMicroReference.setWidth(200, Unit.PIXELS);
        ruleConsidered.setWidth(200, Unit.PIXELS);
    }

    /**
     * @return
     */
    public Component[] components() {
        Component[] components = new Component[]{
                type,
                citation,
                citationMicroReference,
                ruleConsidered
            };
        addAll(Arrays.asList(components));
        return components;
    }

    public static List<String> visibleFields() {
        List<String> visibleFields = Arrays.asList(new String[]{
            "type",
            "citation",
            "citationMicroReference",
            "ruleConsidered"
            });
        return visibleFields;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateRowItemsEnablement() {
        // nothing to do
    }

}
