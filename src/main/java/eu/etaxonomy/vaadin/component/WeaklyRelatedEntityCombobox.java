/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.util.UUID;

import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Field;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.service.FilterableStringRepresentationPagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.event.NestedButtonStateUpdater;

/**
 * @author a.kohlbecker
 * @since May 24, 2017
 *
 */
public class WeaklyRelatedEntityCombobox<V extends IdentifiableEntity<?>> extends CompositeCustomField<String>
    implements WeaklyRelatedEntityField<V>, ReloadableSelect {

    private static final long serialVersionUID = 6277565876657520311L;

    public static final String PRIMARY_STYLE = "v-related-entity-combobox";

    private Class<V> type;

    private CssLayout container = new CssLayout();

    private ReloadableLazyComboBox<String> lazySelect;

    private Button addButton = ButtonFactory.CREATE_NEW.createButton();
    private Button editButton = ButtonFactory.EDIT_ITEM.createButton();

    private WeaklyRelatedEntityButtonUpdater buttonUpdater;

    private FilterableStringRepresentationPagingProvider<UUID> filterablePagingProvider;

    public WeaklyRelatedEntityCombobox(String caption, Class<V> type){
        this.type = type;
        setCaption(caption);
        lazySelect = new ReloadableLazyComboBox<String>(String.class);
        addStyledComponents(lazySelect, addButton, editButton);
        addSizedComponents(lazySelect, container);
        buttonUpdater = new WeaklyRelatedEntityButtonUpdater(this);
        lazySelect.addValueChangeListener(buttonUpdater);
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
    public Class<String> getType() {
        return String.class;
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
    public ReloadableLazyComboBox<String> getSelect() {
        return lazySelect;
    }

    /**
     * {@inheritDoc}
     */
    public void loadFrom(FilterablePagingProvider<String> filterablePagingProvider, FilterableCountProvider filterableCountProvider, int pageLength) {

        this.filterablePagingProvider = (FilterableStringRepresentationPagingProvider<UUID>) filterablePagingProvider;
        lazySelect.loadFrom(filterablePagingProvider, filterableCountProvider, pageLength);
        buttonUpdater.updateButtons(getValue());
    }

    /**
     * reload the selected entity from the persistent storage
     */
    @Override
    public void reload() {
        filterablePagingProvider.clearIdCache();
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
    public void selectNewItem(String bean){
        setValue(bean);
    }

    /**
     * Returns always currently selected item by
     *
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        lazySelect.commit();
        return lazySelect.getValue();
    }

    public UUID getIdForValue(){
        return filterablePagingProvider.idFor(getValue());
    }


    /**
     * sets the selection to the <code>newFieldValue</code> only if the value can
     * be provided by the FilterablePagingProvider
     *
     */
    @Override
    public void setValue(String newFieldValue) throws com.vaadin.data.Property.ReadOnlyException, ConversionException {
        lazySelect.refresh();
        if(lazySelect.getOptions().contains(newFieldValue)){
            lazySelect.setValue(newFieldValue);
        }
        lazySelect.markAsDirty();
    }

    @Override
    public boolean isValueInOptions(){
        return lazySelect.getOptions().contains(lazySelect.getValue());
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        lazySelect.setPropertyDataSource(newDataSource);
        if(buttonUpdater != null){
            buttonUpdater.updateButtons(lazySelect.getValue());
        }
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
        setDeepReadOnly(readOnly, getContent(), null);
        if(buttonUpdater != null){
         buttonUpdater.updateButtons(lazySelect.getValue());
        }
    }

    @Override
    public void updateButtons(){
        buttonUpdater.updateButtons(getValue());
    }

    /**
     * {@inheritDoc}
     * @deprecated NestedButtonStateUpdater should rather be instantiated in the RelatedEntityField instead of passing it as property
     */
    @Override
    @Deprecated
    public void setNestedButtonStateUpdater(NestedButtonStateUpdater<V> buttonUpdater) {
        // not needed
    }

    class WeaklyRelatedEntityButtonUpdater implements NestedButtonStateUpdater<String> {

        private static final long serialVersionUID = 4472031263172275012L;

        WeaklyRelatedEntityCombobox<V>  toOneRelatedEntityField;

        public WeaklyRelatedEntityButtonUpdater(WeaklyRelatedEntityCombobox<V> toOneRelatedEntityField){
            this.toOneRelatedEntityField = toOneRelatedEntityField;
            String stringValue = toOneRelatedEntityField.getValue();
            updateButtons(toOneRelatedEntityField.getValue());
            toOneRelatedEntityField.setEditButtonEnabled(false);
        }


        /**
         * {@inheritDoc}
         */
        @Override
        public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {

            String value = (String)event.getProperty().getValue();
            updateButtons(value);
        }

        /**
         * @param value
         */
        @Override
        public void updateButtons(String value) {

            UUID uuid = null;
            if(value != null && filterablePagingProvider != null){
                uuid = filterablePagingProvider.idFor(value);
            }
            boolean userIsAllowedToUpdate = uuid != null && UserHelperAccess.userHelper().userHasPermission(type, uuid, CRUD.UPDATE);
            boolean userIsAllowedToCreate = UserHelperAccess.userHelper().userHasPermission(type, CRUD.CREATE);
            boolean isReadOnlyField = ((Field)toOneRelatedEntityField).isReadOnly();

            toOneRelatedEntityField.setAddButtonEnabled(!isReadOnlyField && userIsAllowedToCreate);
            toOneRelatedEntityField.setEditButtonEnabled(!isReadOnlyField && userIsAllowedToUpdate);
        }
    }



}
