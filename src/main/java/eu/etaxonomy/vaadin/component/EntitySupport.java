/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

/**
 * Interface to ship around equality problems of entities which have modified data.
 *
 * TODO as inner type of ToOneRelatedEntityReloader?
 *
 * @author a.kohlbecker
 * @since Feb 8, 2018
 *
 * @param <V>
 */
public interface EntitySupport<V> {

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
    void replaceEntityValue(V bean);

}