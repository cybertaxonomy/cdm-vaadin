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

import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.vaadin.event.NestedButtonStateUpdater;

/**
 * @author a.kohlbecker
 * @since May 25, 2017
 *
 */
public interface WeaklyRelatedEntityField<V extends IdentifiableEntity> {

    public void setNestedButtonStateUpdater(NestedButtonStateUpdater<V> buttonUpdater);

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

    public void selectNewItem(String stringRepresentation);

    public Class<String> getType();

    /**
     * Update the enabled state of the add and edit buttons
     */
    public void updateButtons();

}
