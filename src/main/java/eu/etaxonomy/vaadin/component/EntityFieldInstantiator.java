/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.component;

/**
 * @author a.kohlbecker
 * @since Dec 11, 2017
 */
public abstract class EntityFieldInstantiator<F> {

    public abstract F createNewInstance();

}
