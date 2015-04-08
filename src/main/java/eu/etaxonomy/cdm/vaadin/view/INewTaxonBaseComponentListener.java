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
import eu.etaxonomy.cdm.vaadin.container.IdAndUuid;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
public interface INewTaxonBaseComponentListener {


    public CdmSQLContainer getSecRefContainer();


    /**
     * @param scientificName
     * @param secRefItemId
     * @return
     */
    public IdAndUuid newTaxon(String scientificName, Object secRefItemId);


    /**
     * @param scientificName
     * @param secRefItemId
     * @param accTaxonUuid
     * @return
     */
    public IdAndUuid newSynonym(String scientificName, Object secRefItemId, UUID accTaxonUuid);



}
