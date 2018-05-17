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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.transaction.TransactionStatus;
import org.vaadin.addons.lazyquerycontainer.Query;
import org.vaadin.addons.lazyquerycontainer.QueryDefinition;

import com.vaadin.data.Item;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.description.DescriptionBase;
import eu.etaxonomy.cdm.model.description.Distribution;
import eu.etaxonomy.cdm.model.description.Feature;
import eu.etaxonomy.cdm.model.description.PresenceAbsenceTerm;
import eu.etaxonomy.cdm.model.description.TaxonDescription;
import eu.etaxonomy.cdm.model.location.NamedArea;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;

/**
 * @author freimeier
 * @since 16.11.2017
 *
 */

public class DistributionStatusQuery implements Query{
    public static final String DTYPE_COLUMN = "DTYPE";
    public static final String ID_COLUMN = "id";
    public static final String UUID_COLUMN = "uuid";
    public static final String CLASSIFICATION_COLUMN = "classification";
    public static final String RANK_COLUMN = "Rang";
    public static final String TAXON_COLUMN = "Taxon";

    private CdmRepository repo;
    private QueryDefinition definition;
    private List<UUID> nodeUuids;
    private List<NamedArea> namedAreas;

    public DistributionStatusQuery(CdmRepository repo, List<UUID> nodeUuids, List<NamedArea> namedAreas, QueryDefinition definition) {
        this.repo = repo;
        this.nodeUuids = nodeUuids;
        this.namedAreas = namedAreas;
        this.definition = definition;
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.Query#constructItem()
     */
    @Override
    public Item constructItem() {
        return null;
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.Query#deleteAllItems()
     */
    @Override
    public boolean deleteAllItems() {
        return false;
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.Query#loadItems(int, int)
     */
    @Override
    public List<Item> loadItems(int startIndex, int count) {
        List<Item> items = new ArrayList<>();
        for(int i=startIndex; i<startIndex+count; i++) {
            if(i<this.size()) {
                UUID nodeUuid = this.nodeUuids.get(i);
                TaxonNode taxonNode = CdmSpringContextHelper.getTaxonNodeService().load(nodeUuid, Arrays.asList("taxon"));
                Taxon taxon = taxonNode.getTaxon();
                Item item = new PropertysetItem();
                item.addItemProperty(UUID_COLUMN, new ObjectProperty<>(taxon.getUuid(), UUID.class));
                item.addItemProperty(TAXON_COLUMN, new ObjectProperty<>(taxon.getTitleCache(), String.class));
                Map<NamedArea,PresenceAbsenceTerm> distributionStatusMap = this.getAreaToDistributionStatusMap(taxon);
                for(NamedArea namedArea : this.namedAreas) {
                    PresenceAbsenceTerm distributionStatus = distributionStatusMap.get(namedArea);
                    UUID distributionStatusUuid = (distributionStatus != null) ? distributionStatus.getUuid() : null;
                    item.addItemProperty(namedArea.getUuid(), new ObjectProperty<>(distributionStatusUuid, UUID.class));
                }
                if(definition.getFilters().parallelStream().allMatch(f -> f.passesFilter(taxon.getUuid(), item))) {
                    items.add(item);
                }
            }
        }
        return items;
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.Query#saveItems(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public void saveItems(List<Item> addedItems, List<Item> modifiedItems, List<Item> removedItems) {
        System.out.println("Save requested!");
        for(Item item : modifiedItems) {
            final UUID taxonUuid = (UUID) item.getItemProperty(UUID_COLUMN).getValue();
            final Taxon taxon = (Taxon) CdmSpringContextHelper.getTaxonService().find(taxonUuid);
            Map<NamedArea, UUID> areaToDistributionStatusUuidMap = new HashMap<>();
            for(NamedArea namedArea : this.namedAreas) {
                areaToDistributionStatusUuidMap.put(namedArea, (UUID) item.getItemProperty(namedArea.getUuid()).getValue());
            }
            this.updateDistributionStatus(taxon, areaToDistributionStatusUuidMap);
        }
    }

    /* (non-Javadoc)
     * @see org.vaadin.addons.lazyquerycontainer.Query#size()
     */
    @Override
    public int size() {
        return this.nodeUuids.size();
    }

    private Map<NamedArea, PresenceAbsenceTerm> getAreaToDistributionStatusMap(Taxon taxon) {
        Map<NamedArea, PresenceAbsenceTerm> distributionStatusMap = new HashMap<>();

        List<Distribution> distributions = this.getDistributions(taxon);
        for(Distribution dist : distributions){
            if(dist.getArea()!=null){
                distributionStatusMap.put(dist.getArea(), dist.getStatus());
            }
        }

        return distributionStatusMap;
    }

    private void updateDistributionStatus(Taxon taxon,  Map<NamedArea, UUID> areaToDistributionStatusUuidMap) {
        CdmRepository repo = this.repo;
        TransactionStatus tx = repo.startTransaction();
        taxon = (Taxon)repo.getTaxonService().find(taxon.getUuid());

        List<Distribution> distributions = this.getDistributions(taxon);

        for(NamedArea namedArea : areaToDistributionStatusUuidMap.keySet()) {
            Distribution distribution = null;
            for(Distribution dist : distributions){
                if(dist.getArea()!= null && dist.getArea().equals(namedArea)){
                    distribution = dist;
                    break;
                }
            }

            PresenceAbsenceTerm distributionStatus = null;
            try {
                distributionStatus = (PresenceAbsenceTerm) CdmSpringContextHelper.getTermService().load(areaToDistributionStatusUuidMap.get(namedArea));
            } catch (IllegalArgumentException iae) {
                distributionStatus = null;
            }

            if(distribution==null){
                //create new distribution
                distribution = Distribution.NewInstance(namedArea, distributionStatus);
                Set<TaxonDescription> descriptions = taxon.getDescriptions();
                if (descriptions != null && !descriptions.isEmpty()) {
                    for (TaxonDescription desc : descriptions) {
                        // add to first taxon description
                        desc.addElement(distribution);
                    }
                } else {// there are no TaxonDescription yet.
                    TaxonDescription taxonDescription = TaxonDescription.NewInstance(taxon);
                    taxonDescription.addElement(distribution);
                }
            }
            else if(distributionStatus == null){
                //delete descriptionElementBase
                DescriptionBase<?> desc = distribution.getInDescription();
                desc.removeElement(distribution);
            }
            else if(!distributionStatus.equals(distribution.getStatus())){
               //update distribution
               distribution.setStatus(distributionStatus);
               repo.getCommonService().saveOrUpdate(distribution);
            }
        }
        repo.commitTransaction(tx);
    }

    private List<Distribution> getDistributions(Taxon taxon) {
        Set<Feature> setFeature = new HashSet<>(Arrays.asList(Feature.DISTRIBUTION()));
        List<Distribution> listTaxonDescription = CdmSpringContextHelper.getDescriptionService()
                .listDescriptionElementsForTaxon(taxon, setFeature, null, null, null, DESCRIPTION_INIT_STRATEGY);
        return listTaxonDescription;
    }

    private static final List<String> DESCRIPTION_INIT_STRATEGY = Arrays.asList(new String []{
            "$",
            "elements.*",
            "elements.sources.citation.authorship.$",
            "elements.sources.nameUsedInSource.originalNameString",
            "elements.area.level",
            "elements.modifyingText",
            "elements.states.*",
            "elements.media",
            "elements.multilanguageText",
            "multilanguageText",
            "stateData.$",
            "annotations",
            "markers",
            "sources.citation.authorship",
            "sources.nameUsedInSource",
            "multilanguageText",
            "media",
            "name.$",
            "name.rank.representations",
            "name.status.type.representations",
            "taxon2.name",
    });
}
