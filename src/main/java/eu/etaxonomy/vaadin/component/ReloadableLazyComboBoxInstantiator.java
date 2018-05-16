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

import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;

/**
 * @author a.kohlbecker
 * @since May 3, 2018
 *
 */
public class ReloadableLazyComboBoxInstantiator<V extends Object> {

    private FilterablePagingProvider<V> filterablePagingProvider = null;
    private FilterableCountProvider filterableCountProvider = null;
    private Integer pageLength = null;

    private CaptionGenerator<V> captionGenerator;

    private CachingPresenter cachingPresenter;
    private Class<V> itemType;

    public ReloadableLazyComboBoxInstantiator(Class<V> itemType){
        this.itemType = itemType;
    }

    /**
     * {@inheritDoc}
     */
    protected ReloadableLazyComboBox<V> newInstance(V val) throws InstantiationException, IllegalAccessException {


        ReloadableLazyComboBox<V> field = new ReloadableLazyComboBox<V>(itemType);
        // FIXME using the ToOneRelatedEntityReloader created a dependency to the cdm packages, this should be relaxed!!!
        field.addValueChangeListener(new ToOneRelatedEntityReloader(field, cachingPresenter));

        if(filterablePagingProvider == null || filterableCountProvider == null ||  pageLength == null) {
            throw new RuntimeException("The filterablePagingProvider, filterableCountProvider and pageLength must be set, use setPagingProviders().");
        }
        field.loadFrom(filterablePagingProvider, filterableCountProvider, pageLength);
        if(captionGenerator != null){
            field.setCaptionGenerator(captionGenerator);
        }
        field.setValue(val);
        return field;
    }

    public void setPagingProviders(FilterablePagingProvider<V> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength,
            CachingPresenter cachingPresenter){
        this.filterablePagingProvider = filterablePagingProvider;
        this.filterableCountProvider = filterableCountProvider;
        this.pageLength = pageLength;
        this.cachingPresenter = cachingPresenter;
    }

    /**
     * @param cdmTitleCacheCaptionGenerator
     */
    public void setCaptionGenerator(CaptionGenerator<V> captionGenerator) {
        this.captionGenerator = captionGenerator;
    }



}
