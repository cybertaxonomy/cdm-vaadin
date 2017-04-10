/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.ListSelect;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.DefinedTermBase;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.persistence.query.OrderHint;

/**
 * @author a.kohlbecker
 * @since Apr 6, 2017
 *
 */
@SpringComponent
public class SelectFieldFactory {

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    private final static List<String> INIT_STRATEGY = Arrays.asList(new String[]{"$", "representations"});

    private static List<OrderHint> orderHints = new ArrayList<>();

    static {
        orderHints.add(OrderHint.BY_ORDER_INDEX);
        orderHints.add(OrderHint.ORDER_BY_TITLE_CACHE);
    }

    public ListSelect createListSelect(String caption, TermType termType){
        BeanItemContainer<DefinedTermBase> termItemContainer = buildBeanItemContainer(termType);
        ListSelect select = new ListSelect(caption, termItemContainer);
        return select;
    }

    /**
     * @param termType
     */
    private BeanItemContainer<DefinedTermBase> buildBeanItemContainer(TermType termType) {
        // TODO use TermCacher?
        List<DefinedTermBase> terms = repo.getTermService().listByTermType(termType, null, null, orderHints, INIT_STRATEGY);
        BeanItemContainer<DefinedTermBase> termItemContainer = new BeanItemContainer<>(DefinedTermBase.class);
        termItemContainer.addAll(terms);
        return termItemContainer;
    }

}
