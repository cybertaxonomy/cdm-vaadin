/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.format.reference.ReferenceEllypsisFormatter;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.agent.Person;
import eu.etaxonomy.cdm.model.agent.TeamOrPersonBase;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.reference.ReferenceType;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.cdm.persistence.query.OrderHint.SortOrder;

/**
 * @author a.kohlbecker
 * @since Jun 7, 2018
 *
 */
@Component
public class CdmFilterablePagingProviderFactory {

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    public CdmFilterablePagingProvider<Reference, Reference> referencePagingProvider() {
        return inReferencePagingProvider(null, false);
    }

    /**
     *
     * @param subReferenceType
     *            the type of the reference for which to the paging provider is
     *            to be created
     * @param includeUnspecificTypes
     *            Whether to include references with the types
     *            {@link ReferenceType.Generic Generic} and {@code null},
     * @return
     */
    public CdmFilterablePagingProvider<Reference, Reference> inReferencePagingProvider(ReferenceType subReferenceType,
            boolean includeUnspecificTypes) {
        List<OrderHint> referenceOrderHints = new ArrayList<OrderHint>();
        referenceOrderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
        referenceOrderHints.add(new OrderHint("issn", SortOrder.ASCENDING));
        referenceOrderHints.add(new OrderHint("isbn", SortOrder.ASCENDING));
        CdmFilterablePagingProvider<Reference, Reference> pagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(
                repo.getReferenceService(), MatchMode.BEGINNING, referenceOrderHints);

        Set<ReferenceType> inRefTypes = subReferenceType.inReferenceContraints(subReferenceType);

        if (!inRefTypes.isEmpty()) {
            if (includeUnspecificTypes) {
                inRefTypes.add(ReferenceType.Generic);
                inRefTypes.add(null);
            }
            pagingProvider.addRestriction(new Restriction<ReferenceType>("type", null,
                    inRefTypes.toArray(new ReferenceType[inRefTypes.size()])));
        }

        // using the ReferenceEllypsisFormatter initstrategy since using the ReferenceEllypsisCaptionGenerator should be default
        // for all views and UIs
        pagingProvider.setInitStrategy(ReferenceEllypsisFormatter.INIT_STRATEGY);

        return pagingProvider;
    }

    public TypifiedEntityFilterablePagingProvider<Reference> referenceEntityReferencePagingProvider(
            ReferenceEllypsisFormatter labelProvider, List<String> initStrategy) {
        List<OrderHint> referenceOrderHints = new ArrayList<OrderHint>();
        referenceOrderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
        referenceOrderHints.add(new OrderHint("issn", SortOrder.ASCENDING));
        referenceOrderHints.add(new OrderHint("isbn", SortOrder.ASCENDING));
        TypifiedEntityFilterablePagingProvider<Reference> pagingProvider = new TypifiedEntityFilterablePagingProvider<Reference>(
                repo.getReferenceService(), MatchMode.BEGINNING, referenceOrderHints, labelProvider);
        pagingProvider.setInitStrategy(initStrategy);

        return pagingProvider;
    }

    public CdmFilterablePagingProvider<TaxonName, TaxonName> taxonNamesWithoutOrthophicIncorrect() {

        CdmFilterablePagingProvider<TaxonName, TaxonName> pagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(
                repo.getNameService());
        pagingProvider.setInitStrategy(
                Arrays.asList("registrations", "nomenclaturalSource.citation", "nomenclaturalSource.citation.inReference"));
        // pagingProvider.addRestriction(new
        // Restriction<>("relationsFromThisName.type", Operator.AND_NOT, null,
        // NameRelationshipType.ORTHOGRAPHIC_VARIANT()));
        return pagingProvider;
    }

    public CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase> teamOrPersonPagingProvider() {
        return  new CdmFilterablePagingProvider<AgentBase, TeamOrPersonBase>(repo.getAgentService(), TeamOrPersonBase.class, MatchMode.BEGINNING, OrderHint.ORDER_BY_TITLE_CACHE.asList());
    }

    public CdmFilterablePagingProvider<AgentBase, Person> personPagingProvider() {
        return new CdmFilterablePagingProvider<AgentBase, Person>(repo.getAgentService(), Person.class, MatchMode.BEGINNING, OrderHint.ORDER_BY_TITLE_CACHE.asList());
    }

}
