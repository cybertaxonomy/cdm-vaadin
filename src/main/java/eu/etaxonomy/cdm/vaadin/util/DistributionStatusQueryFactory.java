/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.List;
import java.util.UUID;

import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;
import org.vaadin.addons.lazyquerycontainer.QueryFactory;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.location.NamedArea;

/**
 * @author freimeier
 * @date 16.11.2017
 *
 */
public class DistributionStatusQueryFactory implements QueryFactory{
    CdmRepository repo;
    List<UUID> nodeUuids;
    List<NamedArea> namedAreas;

    public DistributionStatusQueryFactory(CdmRepository repo, List<UUID> nodeUuids, List<NamedArea> namedAreas) {
        this.repo = repo;
        this.nodeUuids = nodeUuids;
        this.namedAreas = namedAreas;
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.QueryFactory#constructQuery(org.vaadin.addons.lazyquerycontainer.QueryDefinition)
     */
    @Override
    public Query constructQuery(QueryDefinition definition) {
        return new DistributionStatusQuery(this.repo, this.nodeUuids, this.namedAreas, definition);
    }
}
