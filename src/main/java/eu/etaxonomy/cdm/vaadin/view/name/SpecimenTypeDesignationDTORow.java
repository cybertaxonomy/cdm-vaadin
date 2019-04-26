/**
* Copyright (C) 2017 EDIT
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

import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.term.DefinedTerm;
import eu.etaxonomy.cdm.vaadin.component.CollectionRow;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowItemCollection;
import eu.etaxonomy.cdm.vaadin.component.CollectionRowRepresentative;
import eu.etaxonomy.cdm.vaadin.component.TextFieldNFix;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
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
public class SpecimenTypeDesignationDTORow extends CollectionRowItemCollection implements CollectionRow {

    private static final long serialVersionUID = -5637834800897331072L;

    /* CONVENTION!
     *
     * The fieldname must match the properties of the SpecimenTypeDesignationDTO
     */
    TextField associatedTypeDesignationCount = new TextField();
    RowListSelect kindOfUnit = new RowListSelect(); // position is IMPORTANT, see rowListSelectColumn()
    NativeSelect typeStatus = new NativeSelect();
    ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection> collection =
            new ToOneRelatedEntityCombobox<eu.etaxonomy.cdm.model.occurrence.Collection>(null, eu.etaxonomy.cdm.model.occurrence.Collection.class);
    TextField accessionNumber = new TextFieldNFix();
    TextField preferredStableUri = new TextFieldNFix();
    TextField mediaUri = new TextFieldNFix();
    ToOneRelatedEntityCombobox<Reference> mediaSpecimenReference =
            new ToOneRelatedEntityCombobox<Reference>(null, Reference.class);
    TextField mediaSpecimenReferenceDetail = new TextFieldNFix(); //"Image reference detail");
    private ToOneRelatedEntityButtonUpdater<Reference> mediaSpecimenReferenceUpdater;
    private ToOneRelatedEntityButtonUpdater<Collection> collectionFieldUpdater;

    public SpecimenTypeDesignationDTORow(){

        kindOfUnit.setRequired(true);
        kindOfUnit.setRow(this);
        typeStatus.setRequired(true);

        accessionNumber.setWidth(100, Unit.PIXELS);
        preferredStableUri.setWidth(150, Unit.PIXELS);
        preferredStableUri.setConverter(new UriConverter());
        collection.setWidth(200, Unit.PIXELS);
        collection.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Collection>(collection));
        mediaUri.setWidth(150, Unit.PIXELS);
        mediaUri.setConverter(new UriConverter());
        mediaSpecimenReference.setWidth(200, Unit.PIXELS);
        mediaSpecimenReference.setNestedButtonStateUpdater(new ToOneRelatedEntityButtonUpdater<Reference>(mediaSpecimenReference));
        mediaSpecimenReferenceDetail.setWidth(200, Unit.PIXELS);

        kindOfUnit.addValueChangeListener(e ->
                updateRowItemsEnablement()
        );

    }

    /**
     * @return
     */
    public Component[] components() {
        Component[] components = new Component[]{
            associatedTypeDesignationCount,
            kindOfUnit, typeStatus,
            collection, accessionNumber,
            preferredStableUri,
            mediaUri, mediaSpecimenReference,
            mediaSpecimenReferenceDetail
            };
        addAll(Arrays.asList(components));
        return components;
    }

    public static List<String> visibleFields() {
        List<String> visibleFields = Arrays.asList(new String[]{
            "kindOfUnit", "typeStatus",
            "collection", "accessionNumber",
            "preferredStableUri",
            "mediaUri", "mediaSpecimenReference",
            "mediaSpecimenReferenceDetail"
            });
        return visibleFields;
    }

    @Override
    public void updateRowItemsEnablement() {

        DefinedTerm kindOfUnitTerm = (DefinedTerm)kindOfUnit.getValue();

        boolean publishedImageType = kindOfUnitTerm != null && kindOfUnitTerm.equals(KindOfUnitTerms.PUBLISHED_IMAGE());
        boolean unPublishedImageType = kindOfUnitTerm != null && kindOfUnitTerm.equals(KindOfUnitTerms.UNPUBLISHED_IMAGE());

        boolean kindOfUnitLocked = !associatedTypeDesignationCount.getValue().isEmpty() && Integer.valueOf(associatedTypeDesignationCount.getValue()) > 1;
        kindOfUnit.setEnabled(!kindOfUnitLocked);
        kindOfUnit.setDescription(kindOfUnitLocked ?
                "Can not be changed since the type specimen is associated with multiple type designations" : "");

        mediaSpecimenReference.setEnabled(publishedImageType || unPublishedImageType);
        mediaSpecimenReferenceDetail.setEnabled(publishedImageType || unPublishedImageType);
        mediaUri.setEnabled(publishedImageType || unPublishedImageType);

    }

    /**
     *
     * @return the 0-based position index of the <code>kindOfUnit</code> field in this class
     * which are visible according to {@link #visibleFields())
     */
    public static int rowListSelectColumn(){
        return 0;
    }

    class RowListSelect extends NativeSelect implements CollectionRowRepresentative {

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