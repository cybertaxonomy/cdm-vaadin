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
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.reference.Reference;
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


    public CdmFilterablePagingProvider<Reference,Reference> referencePagingProvider(){
        List<OrderHint> referenceOrderHints = new ArrayList<OrderHint>();
        referenceOrderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
        referenceOrderHints.add(new OrderHint("issn", SortOrder.ASCENDING));
        referenceOrderHints.add(new OrderHint("isbn", SortOrder.ASCENDING));
        CdmFilterablePagingProvider<Reference,Reference> pagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(
                repo.getReferenceService(), MatchMode.ANYWHERE, referenceOrderHints);

        return pagingProvider;
    }

    public TypifiedEntityFilterablePagingProvider<Reference> referenceEntityReferencePagingProvider(ReferenceLabelProvider labelProvider, List<String> initStrategy){
        List<OrderHint> referenceOrderHints = new ArrayList<OrderHint>();
        referenceOrderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
        referenceOrderHints.add(new OrderHint("issn", SortOrder.ASCENDING));
        referenceOrderHints.add(new OrderHint("isbn", SortOrder.ASCENDING));
        TypifiedEntityFilterablePagingProvider<Reference> pagingProvider = new TypifiedEntityFilterablePagingProvider<Reference>(
                repo.getReferenceService(), MatchMode.ANYWHERE, referenceOrderHints, labelProvider);
        pagingProvider.setInitStrategy(initStrategy);

        return pagingProvider;
    }


}
