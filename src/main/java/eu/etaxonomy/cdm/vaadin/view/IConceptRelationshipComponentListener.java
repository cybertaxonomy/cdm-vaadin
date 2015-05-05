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

import org.json.JSONException;

import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree.Direction;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
public interface IConceptRelationshipComponentListener {

    /**
     * @param taxonIun
     * @param direction
     * @throws JSONException
     */
    public void refreshRelationshipView(IdUuidName taxonIun, Direction direction) throws JSONException;

    /**
     *
     */
    public void clearRelationshipView();



}
