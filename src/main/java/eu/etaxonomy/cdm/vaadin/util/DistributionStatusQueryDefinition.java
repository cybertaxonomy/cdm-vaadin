// $Id$
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

import org.vaadin.addons.lazyquerycontainer.LazyQueryDefinition;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.location.NamedArea;

/**
 * @author freimeier
 * @date 22.11.2017
 *
 */
public class DistributionStatusQueryDefinition extends LazyQueryDefinition {

    private CdmRepository repo;
    private List<UUID> nodeUuids;
    private List<NamedArea> namedAreas;

    /**
     * @param repo
     * @param nodeUuids
     * @param namedAreas
     * @param compositeItems
     * @param batchSize
     * @param idPropertyId
     */
    public DistributionStatusQueryDefinition(List<NamedArea> namedAreas,
            boolean compositeItems, int batchSize) {
        super(compositeItems, batchSize, DistributionStatusQuery.UUID_COLUMN);
        this.addProperty(DistributionStatusQuery.UUID_COLUMN, UUID.class, null, true, false);
        this.addProperty(DistributionStatusQuery.TAXON_COLUMN, String.class, null, true, false);
        namedAreas.forEach(na -> this.addProperty(na.getUuid(), UUID.class, null, false, false));
    }
}
