/**
* Copyright (C) 2017 EDIT
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
import java.util.UUID;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.data.util.BeanItemContainer;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.IEnumTerm;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.common.TermVocabulary;
import eu.etaxonomy.cdm.persistence.query.OrderHint;

/**
 * <b>Read only</b> service which can provide {@link BeanItemContainer BeanItemContainers} as source for select form
 * element options.
 *
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
@Service
public class CdmBeanItemContainerFactory {

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private final static List<String> INIT_STRATEGY = Arrays.asList(new String[]{"$", "representations"});

    private static List<OrderHint> orderHints = new ArrayList<>();

    static {
        orderHints.add(OrderHint.BY_ORDER_INDEX);
        orderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
    }

    /**
     * @param termType
     */
    @Transactional(readOnly=true)
    public BeanItemContainer<DefinedTermBase> buildBeanItemContainer(TermType termType) {

        clearSession();
        // TODO use TermCacher?
        List<DefinedTermBase> terms = repo.getTermService().listByTermType(termType, null, null, orderHints, INIT_STRATEGY);
        BeanItemContainer<DefinedTermBase> termItemContainer = new BeanItemContainer<>(DefinedTermBase.class);
        termItemContainer.addAll(terms);
        return termItemContainer;
    }

    /**
     * @param termType
     */
    @Transactional(readOnly=true)
    public BeanItemContainer<DefinedTermBase> buildBeanItemContainer(UUID vocabularyUuid) {

        clearSession();
        TermVocabulary vocab = repo.getVocabularyService().find(vocabularyUuid);
        Pager<DefinedTermBase> terms = repo.getVocabularyService().getTerms(vocab, null, null, orderHints, INIT_STRATEGY);
        BeanItemContainer<DefinedTermBase> termItemContainer = new BeanItemContainer<>(DefinedTermBase.class);
        termItemContainer.addAll(terms.getRecords());
        return termItemContainer;
    }

    /**
     * @param termType
     */
    public BeanItemContainer<DefinedTermBase> buildTermItemContainer(UUID ... termUuid) {
        return buildTermItemContainer(Arrays.asList(termUuid));
    }

    /**
     * @param derivation_EVENT_TYPE_UUIDS
     * @return
     */
    @Transactional(readOnly=true)
    public BeanItemContainer<DefinedTermBase> buildTermItemContainer(List<UUID> termsUuids) {
        clearSession();
        List<DefinedTermBase> terms = repo.getTermService().load(termsUuids, INIT_STRATEGY);
        BeanItemContainer<DefinedTermBase> termItemContainer = new BeanItemContainer<>(DefinedTermBase.class);
        termItemContainer.addAll(terms);
        return termItemContainer;
    }

    /**
     * @param termType
     */
    @Transactional(readOnly=true)
    public <T extends CdmBase> BeanItemContainer<T> buildBeanItemContainer(Class<T> type, List<OrderHint> orderHints) {

        if(orderHints == null){
            orderHints = OrderHint.defaultOrderHintsFor(type);
        }
        clearSession();
        List<T> terms = repo.getCommonService().list(type, (Integer)null, (Integer)null,
                orderHints,
                Arrays.asList(new String[]{"$"}));
        BeanItemContainer<T> termItemContainer = new BeanItemContainer<>(type);
        termItemContainer.addAll(terms);
        return termItemContainer;
    }

    public <T extends CdmBase> BeanItemContainer<T> buildBeanItemContainer(Class<T> type) {
        return buildBeanItemContainer(type, null);
    }

    /**
     * @param values
     * @return
     */
    public <T extends IEnumTerm<T>> BeanItemContainer<T> buildBeanItemContainer(Class<T> termType, T ... enumTerms) {
        BeanItemContainer<T> termItemContainer = new BeanItemContainer<>(termType);
        List<T> termList = Arrays.asList(enumTerms);
        termItemContainer.addAll(termList);
        return termItemContainer;
    }

    public void clearSession() {
        Session session = repo.getSession();
        if(session.isOpen()){
            session.clear();
        }
    }

}
