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

import com.vaadin.ui.Button.ClickListener;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public class ToManyRelatedEntitiesComboboxSelect<V extends Object> extends ToManyRelatedEntitiesListSelect<V, LazyComboBox<V>> {

    private static final long serialVersionUID = -4496067980953939548L;

    private FilterablePagingProvider<V> filterablePagingProvider = null;
    private FilterableCountProvider filterableCountProvider = null;
    private Integer pageLength = null;

    private CaptionGenerator<V> captionGenerator;

    /**
     * @param itemType
     * @param fieldType
     * @param caption
     */
    public ToManyRelatedEntitiesComboboxSelect(Class<V> itemType, String caption) {
        super(itemType, null, caption);
        // TODO this.fieldTyp = LazyComboBox.class does not work
        LazyComboBox<V> field = new LazyComboBox<V>(itemType);
        this.fieldType = (Class<LazyComboBox<V>>) field.getClass();
        // addEmptyRowOnInitContent is false in this class since adding row is only possible after setting the PagingProviders
        addEmptyRowOnInitContent = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LazyComboBox<V> newFieldInstance(V val) throws InstantiationException, IllegalAccessException {
        LazyComboBox<V> field = new LazyComboBox<V>(itemType);

        if(filterablePagingProvider == null || filterableCountProvider == null ||  pageLength == null) {
            throw new RuntimeException("The filterablePagingProvider, filterableCountProvider and pageLength must be set, use setPagingProviders().");
        }
        field.loadFrom(filterablePagingProvider, filterableCountProvider, pageLength);
        if(captionGenerator != null){
            field.setCaptionGenerator(captionGenerator);
        }
        field.setValue(val);
        field.setWidth(100, Unit.PERCENTAGE);
        return field;
    }

    public void setPagingProviders(FilterablePagingProvider<V> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength){
        this.filterablePagingProvider = filterablePagingProvider;
        this.filterableCountProvider = filterableCountProvider;
        this.pageLength = pageLength;
        setInternalValue(null);
    }

    /**
     * @param cdmTitleCacheCaptionGenerator
     */
    public void setCaptionGenerator(CaptionGenerator<V> captionGenerator) {
        this.captionGenerator = captionGenerator;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected ClickListener newEditButtonClicklistener(LazyComboBox<V> field) {
        // TODO Auto-generated method stub
        return super.newEditButtonClicklistener(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ClickListener newAddButtonClicklistener(LazyComboBox<V> field) {
        // TODO Auto-generated method stub
        return super.newAddButtonClicklistener(field);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ClickListener newRemoveButtonClicklistener(LazyComboBox<V> field) {
        // TODO Auto-generated method stub
        return super.newRemoveButtonClicklistener(field);
    }



}
