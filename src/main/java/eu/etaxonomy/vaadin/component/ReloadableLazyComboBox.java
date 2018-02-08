/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import java.lang.reflect.Field;

import org.vaadin.viritin.fields.LazyComboBox;
import org.vaadin.viritin.fields.TypedSelect;

import com.vaadin.ui.AbstractField;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.ComboBox;

public class ReloadableLazyComboBox<T> extends LazyComboBox<T> implements ReloadableSelect, EntitySupport<T>{


    private static final long serialVersionUID = -4833661351090992884L;

    static Field lazySelectInternalValueField;
    static Field internalSelectField;
    static {
        try {
            lazySelectInternalValueField = AbstractField.class.getDeclaredField("value");
            lazySelectInternalValueField.setAccessible(true);
            lazySelectInternalValueField.setAccessible(true);

            internalSelectField = TypedSelect.class.getDeclaredField("select");
            internalSelectField.setAccessible(true);

        } catch (NoSuchFieldException | SecurityException e) {
            // NONE of these should ever happen, maybe only after dependency upgrades
            throw new RuntimeException(e);
        }
    }

    /**
     * @param itemType
     */
    public ReloadableLazyComboBox(Class<T> itemType) {
        super(itemType);
        // in the LazyComboBox.initList() scrollToSelectedItem is set to false for better performance
        // but this breaks the refresh, so we need to set it true
        // (temporarily setting it true in reload() does not work)
        ((ComboBox)getSelect()).setScrollToSelectedItem(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload() {
        refresh(); // reload from persistence
        discard(); // reload from data source
    }

    /**
     * This method allows updating the value even if the equals check done
     * in {@link com.vaadin.ui.AbstractField#setValue(Object)} would return true.
     * This is important when working with entity beans which have been modified.
     * For entity beans equality given when the type and id are equal, so data
     * modification is not respected by this concept of equality. Such entities
     * would be skipped in the {@link com.vaadin.ui.AbstractField#setValue(Object) setValue()}
     * method and {@link ValueChangeListener ValueChangeListeners} like the
     * {@link eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader ToOneRelatedEntityReloader}
     *  would not be triggered.
     *  <p>
     *  To circumvent this problem this method checks for object identity with the internal value of
     *  the select field and resets the internal select fields to cause the equality check to fail during
     *  the subsequent execution of the setValue() method. By this it is guaranteed the the value will be
     *  updated and that
     *  the {@link eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader ToOneRelatedEntityReloaders}
     *  will be triggered.
     *
     * @param bean
     */
    @Override
    public void replaceEntityValue(T bean){

        if(bean != internalValueByFieldAccess()){
            resetInternalValue();
        }
        setValue(bean);
    }

    /**
     *
     */
    private T internalValueByFieldAccess(){
        try {
            return (T) lazySelectInternalValueField.get(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // NONE of these should ever happen, maybe only after dependency upgrades
            throw new RuntimeException(e);
        }
    }

    /**
     * Directly sets the <b>internal value fields</b> of the nested <code>LazyComboBox</code> to <code>null</code>.
     *  <b>This method short circuits the setValue() method execution cascade completely</b>
     *  <p>
     *  See {@link #refreshSelectedValue(Object)} more more background information.
     */
    public void resetInternalValue() {

        try {
            AbstractSelect internalSelect = (AbstractSelect) internalSelectField.get(this);
            lazySelectInternalValueField.set(this, (Object)null);
            lazySelectInternalValueField.set(internalSelect, (Object)null);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException  e) {
            // NONE of these should ever happen, maybe only after dependency upgrades
            throw new RuntimeException(e);
        }
    }

}