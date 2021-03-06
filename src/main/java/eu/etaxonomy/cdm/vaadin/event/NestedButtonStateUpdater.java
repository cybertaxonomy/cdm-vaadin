/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event;

import com.vaadin.data.Property.ValueChangeListener;

/**
 * @author a.kohlbecker
 * @since Jun 6, 2018
 *
 */
public interface NestedButtonStateUpdater<T> extends ValueChangeListener{

    public void updateButtons(T value);

}
