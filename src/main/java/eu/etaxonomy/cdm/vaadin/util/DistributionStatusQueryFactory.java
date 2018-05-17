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
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

/**
 * The {@link QueryFactory} to generate {@link DistributionStatusQuery}.
 * @author freimeier
 * @since 16.11.2017
 */
public class DistributionStatusQueryFactory implements QueryFactory{
    CdmRepository repo;
    List<UUID> nodeUuids;
    List<NamedArea> namedAreas;

    /**
     * Creates a new DistributionStatusQueryFactory which generates {@link DistributionStatusQuery}
     * which return distribution status information found in the specified {@link CdmRepository}
     * for the {@link TaxonNode}s given by {@code nodeUuids} and the specified {@link NamedArea}s.
     * @param repo The repo to search for information.
     * @param nodeUuids The {@link UUID}s of {@link TaxonNode}s to search information for.
     * @param namedAreas The list of {@link NamedArea}s the distribution information should be related to.
     */
    public DistributionStatusQueryFactory(CdmRepository repo, List<UUID> nodeUuids, List<NamedArea> namedAreas) {
        this.repo = repo;
        this.nodeUuids = nodeUuids;
        this.namedAreas = namedAreas;
    }

    /**
     * 
     * {@inheritDoc}
     */
    @Override
    public Query constructQuery(QueryDefinition definition) {
        return new DistributionStatusQuery(this.repo, this.nodeUuids, this.namedAreas, definition);
    }
}
