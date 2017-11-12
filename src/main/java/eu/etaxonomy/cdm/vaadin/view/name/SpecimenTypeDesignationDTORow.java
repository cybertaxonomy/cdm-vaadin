/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;

/**
 * SpecimenTypeDesignationDTORow needs to be instantiated by a EditorInstantiator which can only be provided by the presenter
 *  {@link SpecimenTypeDesignationWorkingsetEditorPresenter#handleViewEntered()}
 *
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class SpecimenTypeDesignationDTORow {

    /* CONVENTION!
     *
     * The fieldname must match the properties of the SpecimenTypeDesignationDTO
     */
    ListSelect kindOfUnit = new ListSelect(); // "Kind of unit");
    ListSelect typeStatus = new ListSelect();
    ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection> collection =
            new ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection>(null, eu.etaxonomy.cdm.model.occurrence.Collection.class);
    TextField accessionNumber = new TextField(); // "Accession number");
    TextField mediaUri = new TextField(); // "Image URI");
    ToOneRelatedEntityCombobox<Reference> mediaSpecimenReference =
            new ToOneRelatedEntityCombobox<Reference>(null, Reference.class);
    TextField mediaSpecimenReferenceDetail = new TextField(); //"Image reference detail");

    public SpecimenTypeDesignationDTORow(){
        kindOfUnit.setRows(1);
        kindOfUnit.setRequired(true);
        typeStatus.setRows(1);
        typeStatus.setRequired(true);
        accessionNumber.setWidth(100, Unit.PIXELS);
        collection.setWidth(150, Unit.PIXELS);
        mediaUri.setWidth(150, Unit.PIXELS);
        mediaSpecimenReference.setWidth(200, Unit.PIXELS);
        mediaSpecimenReferenceDetail.setWidth(200, Unit.PIXELS);
    }

    /**
     * @return
     */
    public Component[] components() {
        return new Component[]{
                kindOfUnit, typeStatus,
                collection, accessionNumber,
                mediaUri, mediaSpecimenReference,
                mediaSpecimenReferenceDetail, mediaSpecimenReferenceDetail
                };
    }
}