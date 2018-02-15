/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author a.kohlbecker
 * @since May 24, 2017
 *
 */
public class ToOneRelatedEntityCombobox<V extends Object> extends CompositeCustomField<V>
    implements ToOneRelatedEntityField<V>, ReloadableSelect, EntitySupport<V> {

    private static final long serialVersionUID = 6277565876657520311L;

    public static final String PRIMARY_STYLE = "v-related-entity-combobox";

    private Class<V> type;

    private CssLayout container = new CssLayout();

    private ReloadableLazyComboBox<V> lazySelect;

    private Button addButton = new Button(FontAwesome.PLUS);
    private Button editButton  = new Button(FontAwesome.EDIT);

    public ToOneRelatedEntityCombobox(String caption, Class<V> type){
        this.type = type;
        setCaption(caption);
        lazySelect = new ReloadableLazyComboBox<V>(type);
        addStyledComponents(lazySelect, addButton, editButton);
        addSizedComponents(lazySelect, container);
        lazySelect.addValueChangeListener(e -> {
            // update the itemContainer immediately so that the edit button acts on the chosen item
            lazySelect.commit();
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected Component initContent() {
        container.addComponents(lazySelect, addButton, editButton);
        setPrimaryStyleName(PRIMARY_STYLE);
        addDefaultStyles();
        return container;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends V> getType() {
        return type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addDefaultStyles() {
        container.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FieldGroup getFieldGroup() {
        return null;
    }

    /**
     * @return the select
     */
    public ReloadableLazyComboBox<V> getSelect() {
        return lazySelect;
    }

    /**
     * {@inheritDoc}
     */
    public void loadFrom(FilterablePagingProvider<V> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength) {
        lazySelect.loadFrom(filterablePagingProvider, filterableCountProvider, pageLength);

    }

    /**
     * reload the selected entity from the persistent storage
     */
    @Override
    public void reload() {
        getSelect().reload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAddButtonEnabled(boolean enabled) {
        addButton.setEnabled(enabled);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addClickListenerAddEntity(ClickListener listener) {
        addButton.addClickListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditButtonEnabled(boolean enabled) {
        editButton.setEnabled(enabled);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addClickListenerEditEntity(ClickListener listener) {
        editButton.addClickListener(listener);
    }


    @Override
    public void replaceEntityValue(V bean){
        lazySelect.replaceEntityValue(bean);
    }

    @Override
    public void selectNewItem(V bean){
        setValue(bean);
    }

    /**
     * Returns always currently selected item by
     *
     * {@inheritDoc}
     */
    @Override
    public V getValue() {
        lazySelect.commit();
        return lazySelect.getValue();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(V newFieldValue) throws com.vaadin.data.Property.ReadOnlyException, ConversionException {
        lazySelect.refresh();
        lazySelect.setValue(newFieldValue);
        lazySelect.markAsDirty();
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        lazySelect.setPropertyDataSource(newDataSource);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property getPropertyDataSource() {
        return lazySelect.getPropertyDataSource();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        super.setReadOnly(readOnly);
        setDeepReadOnly(readOnly, getContent());
    }


}
