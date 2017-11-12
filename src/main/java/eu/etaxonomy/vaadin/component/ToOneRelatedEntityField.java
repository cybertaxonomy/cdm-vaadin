/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

import com.vaadin.ui.Button.ClickListener;

/**
 * @author a.kohlbecker
 * @since May 25, 2017
 *
 */
public interface ToOneRelatedEntityField<V extends Object> {

    /**
     * Set the enabled state of the edit button
     *
     * @param enabled
     */
    public void setEditButtonEnabled(boolean enabled);

    /**
     * Adds the click listener to the add-entity-button.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addClickListenerAddEntity(ClickListener listener);

    /**
     * Set the enabled state of the add button
     *
     * @param enabled
     */
    public void setAddButtonEnabled(boolean enabled);

    /**
     * Adds the click listener to the edit-entity-button.
     *
     * @param listener
     *            the Listener to be added.
     */
    public void addClickListenerEditEntity(ClickListener listener);

    public void selectNewItem(V bean);

    public Class<? extends V> getType();

}
