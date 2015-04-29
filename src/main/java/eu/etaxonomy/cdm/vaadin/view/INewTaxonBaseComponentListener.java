// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.UUID;

import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
public interface INewTaxonBaseComponentListener {




    /**
     * @param scientificName
     * @param secRefItemId
     * @param classificationUuid
     * @return
     */
    public IdUuidName newTaxon(String scientificName, Object secRefItemId, UUID classificationUuid);


    /**
     * @param scientificName
     * @param synSecRefItemId
     * @param accTaxonSecRefItemId
     * @param accTaxonUuid
     * @return
     */
    public IdUuidName newSynonym(String scientificName, Object synSecRefItemId, Object accTaxonSecRefItemId, UUID accTaxonUuid);


    /**
     * @param accTaxonUuid
     * @return
     */
    public Object getAcceptedTaxonRefId(UUID accTaxonUuid);


    /**
     * @param classificationUuid
     * @return
     */
    public Object getClassificationRefId(UUID classificationUuid);


    /**
     * @return
     */
    public CdmSQLContainer getAccTaxonSecRefContainer();


    /**
     * @return
     */
    public CdmSQLContainer getSynSecRefContainer();









}
