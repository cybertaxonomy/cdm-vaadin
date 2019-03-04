/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.UUID;

import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;

import com.vaadin.ui.AbstractField;

import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;

/**
 * @author a.kohlbecker
 * @since Mar 4, 2019
 *
 */
public interface ITaxonNameStringFilterablePagingProvider extends FilterableStringRepresentationPagingProvider<UUID>, FilterableCountProvider  {

    /**
     * @param cdmEntity
     */
    public void excludeNames(TaxonName ... excludedTaxonNames);

    /**
     *
     */
    public void updateFromFields();

    /**
     *
     */
    public void unlistenAllFields();

    void listenToFields(AbstractField<String> genusOrUninomialField, AbstractField<String> infraGenericEpithetField, AbstractField<String> specificEpithetField, AbstractField<String> infraSpecificEpithetField);

    /**
     * @param rank
     */
    public void setRankFilter(Rank rank);

}
