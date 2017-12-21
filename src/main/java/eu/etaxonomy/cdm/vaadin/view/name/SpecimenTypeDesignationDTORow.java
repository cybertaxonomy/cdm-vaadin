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

import eu.etaxonomy.cdm.model.common.DefinedTerm;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.component.CollectionRow;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowRepresentative;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.model.registration.KindOfUnitTerms;
import eu.etaxonomy.cdm.vaadin.util.converter.UriConverter;
import eu.etaxonomy.vaadin.component.ToOneRelatedEntityCombobox;

/**
 * CollectionRow for {@link SpecimenTypeDesignationDTO}
 * SpecimenTypeDesignationDTORow needs to be instantiated by a EditorInstantiator which can only be provided by the presenter
 *  {@link SpecimenTypeDesignationWorkingsetEditorPresenter#handleViewEntered()}
 *
 * @author a.kohlbecker
 * @since Jun 22, 2017
 *
 */
public class SpecimenTypeDesignationDTORow implements CollectionRow {

    /* CONVENTION!
     *
     * The fieldname must match the properties of the SpecimenTypeDesignationDTO
     */
    RowListSelect kindOfUnit = new RowListSelect();
    ListSelect typeStatus = new ListSelect();
    ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection> collection =
            new ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection>(null, eu.etaxonomy.cdm.model.occurrence.Collection.class);
    TextField accessionNumber = new TextFieldNFix();
    TextField preferredStableUri = new TextFieldNFix();
    TextField mediaUri = new TextFieldNFix();
    ToOneRelatedEntityCombobox<Reference> mediaSpecimenReference =
            new ToOneRelatedEntityCombobox<Reference>(null, Reference.class);
    TextField mediaSpecimenReferenceDetail = new TextFieldNFix(); //"Image reference detail");

    public SpecimenTypeDesignationDTORow(){
        kindOfUnit.setRows(1);
        kindOfUnit.setRequired(true);
        kindOfUnit.setRow(this);
        typeStatus.setRows(1);
        typeStatus.setRequired(true);
        accessionNumber.setWidth(100, Unit.PIXELS);
        preferredStableUri.setWidth(150, Unit.PIXELS);
        preferredStableUri.setConverter(new UriConverter());
        collection.setWidth(150, Unit.PIXELS);
        mediaUri.setWidth(150, Unit.PIXELS);
        mediaUri.setConverter(new UriConverter());
        mediaSpecimenReference.setWidth(200, Unit.PIXELS);
        mediaSpecimenReferenceDetail.setWidth(200, Unit.PIXELS);

        kindOfUnit.addValueChangeListener(e ->
                updateRowItemsEnablement()
        );

    }

    /**
     * @return
     */
    public Component[] components() {
        return new Component[]{
                kindOfUnit, typeStatus,
                collection, accessionNumber,
                preferredStableUri,
                mediaUri, mediaSpecimenReference,
                mediaSpecimenReferenceDetail, mediaSpecimenReferenceDetail
                };
    }

    @Override
    public void updateRowItemsEnablement() {

        DefinedTerm kindOfUnitTerm = (DefinedTerm)kindOfUnit.getValue();

        boolean publishedImageType = kindOfUnitTerm != null && kindOfUnitTerm.equals(KindOfUnitTerms.PUBLISHED_IMAGE());
        boolean unPublishedImageType = kindOfUnitTerm != null && kindOfUnitTerm.equals(KindOfUnitTerms.UNPUBLISHED_IMAGE());

        mediaSpecimenReference.setEnabled(publishedImageType);
        mediaSpecimenReferenceDetail.setEnabled(publishedImageType);
        mediaUri.setEnabled(publishedImageType || unPublishedImageType);

    }

    class RowListSelect extends ListSelect implements CollectionRowRepresentative {

        private static final long serialVersionUID = 3235653923633494213L;

        CollectionRow row;

        protected void setRow(CollectionRow row){
            this.row = row;
        }

        @Override
        public void updateRowItemsEnabledStates() {
            row.updateRowItemsEnablement();

        }

    }

}