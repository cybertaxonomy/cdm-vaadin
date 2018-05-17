/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

/**
 * A ToManyRelatedEntitiesListSelect which uses a {@link LazyComboBox} as data field.
 *
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public class ToManyRelatedEntitiesComboboxSelect<V extends Object> extends ToManyRelatedEntitiesListSelect<V, ReloadableLazyComboBox<V>> {

    private static final long serialVersionUID = -4496067980953939548L;

    private ReloadableLazyComboBoxInstantiator<V> fieldInstantiator;

    /**
     * @param itemType
     * @param fieldType
     * @param caption
     */
    public ToManyRelatedEntitiesComboboxSelect(Class<V> itemType, String caption) {
        super(itemType, null, caption);
        // TODO this.fieldTyp = LazyComboBox.class does not work
        LazyComboBox<V> field = new LazyComboBox<V>(itemType);
        this.fieldType = (Class<ReloadableLazyComboBox<V>>) field.getClass();
        // addEmptyRowOnInitContent is false in this class since adding row is only possible after setting the PagingProviders
        addEmptyRowOnInitContent = false;
        fieldInstantiator = new ReloadableLazyComboBoxInstantiator<V>(itemType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ReloadableLazyComboBox<V> newFieldInstance(V val) throws InstantiationException, IllegalAccessException {

        // TODO use the setEntityFieldInstantiator(EntityFieldInstantiator) instead to inject as instantiator?
        ReloadableLazyComboBox<V> field = fieldInstantiator.newInstance(val);
        field.setWidth(100, Unit.PERCENTAGE);
        return field;
    }

    public void setPagingProviders(FilterablePagingProvider<V> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength,
            CachingPresenter cachingPresenter){
        fieldInstantiator.setPagingProviders(filterablePagingProvider, filterableCountProvider, pageLength, cachingPresenter);
        setInternalValue(null);
    }

    /**
     * @param cdmTitleCacheCaptionGenerator
     */
    public void setCaptionGenerator(CaptionGenerator<V> captionGenerator) {
        fieldInstantiator.setCaptionGenerator(captionGenerator);
    }

}
