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
 * Interface for select fields which support reloading the selected value from the persistent storage.
 *
 * @author a.kohlbecker
 * @since Feb 8, 2018
 *
 */
public interface ReloadableSelect {

    /**
     * reload the selected entity from the persistent storage
     */
    public void reload();

}