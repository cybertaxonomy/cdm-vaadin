/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

/**
 * Can be implemented by Fields that are being used in {@link AbstractElementCollection} {@link CollectionRow rows}.
 * A Field implementing this interface is the the representative for the whole row and can
 * trigger the update of the enabled state of the other fields in the same row.
 * <p>
 * There is apparently no other way to set the enabled state after the field group has been bound:
 * The value  changed listeners are triggered before the setValue() method sets the enabled state to true
 * overriding any disabled states set by the listeners before.
 *
 * @author a.kohlbecker
 * @since Nov 16, 2017
 *
 */
public interface CollectionRowRepresentative {

    public void updateRowItemsEnabledStates();

}
