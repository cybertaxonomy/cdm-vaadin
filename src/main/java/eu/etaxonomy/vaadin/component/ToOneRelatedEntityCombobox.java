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
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.server.AbstractErrorMessage.ContentMode;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.ErrorMessage.ErrorLevel;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.component.ButtonFactory;
import eu.etaxonomy.cdm.vaadin.event.NestedButtonStateUpdater;

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

    private Button addButton = ButtonFactory.CREATE_NEW.createButton();
    private Button editButton = ButtonFactory.EDIT_ITEM.createButton();

    private NestedButtonStateUpdater<V> buttonUpdater;

    public ToOneRelatedEntityCombobox(String caption, Class<V> type){
        this.type = type;
        setCaption(caption);
        lazySelect = new ReloadableLazyComboBox<V>(type);
        lazySelect.setRequiredError("Must be given");
        setRequiredError("Must be given");
        addStyledComponents(lazySelect, addButton, editButton);
        addSizedComponents(lazySelect, container);
        // lazySelect.setImmediate(true); // should cause immediate validation, however,
        // it does not work here, therefore we validate in the commitSelect() method during the commit
        lazySelect.addValueChangeListener(e -> {
            // update the itemContainer immediately so that the edit button acts on the chosen item
            commitSelect();
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
        commitSelect();
        return lazySelect.getValue();
    }




    /**
     *
     */
    public void commitSelect() {
        try {
            setComponentError(null);
            lazySelect.commit();
        } catch (InvalidValueException ex){
            UserError componentError = new UserError(ex.getHtmlMessage(), ContentMode.HTML, ErrorLevel.ERROR);
            lazySelect.setComponentError(componentError);
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void setNestedButtonStateUpdater(NestedButtonStateUpdater<V> buttonUpdater) {
        this.buttonUpdater = buttonUpdater;
        lazySelect.addValueChangeListener(buttonUpdater);
    }


    @Override
    public void validate() throws InvalidValueException {
        super.validate();
        lazySelect.validate();
    }


    @Override
    public ErrorMessage getErrorMessage() {
        ErrorMessage errorMessage = lazySelect.getErrorMessage();
        return errorMessage;
    }


    @Override
    public boolean isValid() {
        return lazySelect.isValid();
    }

    @Override
    public void setRequired(boolean required) {
        super.setRequired(required);
        lazySelect.setRequired(required);
    }


    @Override
    public boolean isRequired() {
        return lazySelect.isRequired();
    }

    @Override
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        lazySelect.setImmediate(immediate);
    }


    @Override
    public void commit() throws SourceException, InvalidValueException {
        lazySelect.commit(); // we must not use the commitSelect() here to allow InvalidValueException to be handled by the caller
        super.commit();
    }


    @Override
    public void setComponentError(ErrorMessage componentError) {
        lazySelect.setComponentError(componentError);
        super.setComponentError(componentError);
    }



}
