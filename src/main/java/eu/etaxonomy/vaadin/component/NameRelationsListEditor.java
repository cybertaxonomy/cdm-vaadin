/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.model.name.NameRelationshipDTO;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

/**
 * @author a.kohlbecker
 * @since May 3, 2018
 */
public class NameRelationsListEditor extends ToManyRelatedEntitiesListSelect<NameRelationshipDTO, NameRelationField> {

    private static final long serialVersionUID = 6295557881702890211L;

    private ReloadableLazyComboBoxInstantiator<TaxonName> nameSelectInstantiator;

    private ReloadableLazyComboBoxInstantiator<Reference> referenceSelectInstantiator;

    public NameRelationsListEditor(String caption) {
        super(NameRelationshipDTO.class, null, caption);
        this.fieldType = NameRelationField.class;
        // addEmptyRowOnInitContent is false in this class since adding row is only possible after setting the PagingProviders
        addEmptyRowOnInitContent = false;
        nameSelectInstantiator = new ReloadableLazyComboBoxInstantiator<TaxonName>(TaxonName.class);
        referenceSelectInstantiator = new ReloadableLazyComboBoxInstantiator<Reference>(Reference.class);
    }

//    @Override
//    protected NameRelationField newFieldInstance(NameRelationshipDTO val) throws InstantiationException, IllegalAccessException {
//
//        NameRelationField field = new NameRelationField(nameSelectInstantiator, referenceSelectInstantiator, val);
//        field.setWidth(100, Unit.PERCENTAGE);
//        return field;
//    }

    public void setTaxonNamePagingProviders(FilterablePagingProvider<TaxonName> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength,
            CachingPresenter cachingPresenter){
        nameSelectInstantiator.setPagingProviders(filterablePagingProvider, filterableCountProvider, pageLength, cachingPresenter);
        setInternalValue(null);
    }

    public void setTaxonNameCaptionGenerator(CaptionGenerator<TaxonName> captionGenerator) {
        nameSelectInstantiator.setCaptionGenerator(captionGenerator);
    }

    public void setReferencePagingProviders(FilterablePagingProvider<Reference> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength,
            CachingPresenter cachingPresenter){
        referenceSelectInstantiator.setPagingProviders(filterablePagingProvider, filterableCountProvider, pageLength, cachingPresenter);
        setInternalValue(null);
    }

    public void setReferenceCaptionGenerator(CaptionGenerator<Reference> captionGenerator) {
        referenceSelectInstantiator.setCaptionGenerator(captionGenerator);
    }
}