/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

/**
 * @author a.kohlbecker
 * @since Sep 24, 2018
 */
public interface IFilterableStringRepresentationPagingProvider<IDTYPE> extends FilterablePagingProvider<String> {

    public IDTYPE idFor(String stringRepresentation);

    public void clearIdCache();
}