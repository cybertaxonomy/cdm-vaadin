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
import eu.etaxonomy.cdm.model.common.CdmBase;
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

    /**
     *
     * @param caption
     * @param type
     * @return
     */
    public <T extends CdmBase> ListSelect createListSelect(String caption, Class<T> type){
        return createListSelect(caption, type, null);
    }

    public <T extends CdmBase> ListSelect createListSelect(String caption, Class<T> type, List<OrderHint> orderHints){

        if(orderHints == null){
            orderHints = OrderHint.defaultOrderHintsFor(type);
        }

        BeanItemContainer<T> termItemContainer = buildBeanItemContainer(type, orderHints);
        ListSelect select = new ListSelect(caption, termItemContainer);

        // guess property id to use for display
        String propertyId = null;
        if(orderHints != null && !orderHints.isEmpty()){
            propertyId = orderHints.get(0).getPropertyName();
            select.setItemCaptionPropertyId(propertyId);
        }
        return select;
    }

    /**
     * @param termType
     */
    private <T extends CdmBase> BeanItemContainer<T> buildBeanItemContainer(Class<T> type, List<OrderHint> orderHints) {

        List<T> terms = repo.getCommonService().list(type, (Integer)null, (Integer)null,
                orderHints,
                Arrays.asList(new String[]{"$"}));
        BeanItemContainer<T> termItemContainer = new BeanItemContainer<>(type);
        termItemContainer.addAll(terms);
        return termItemContainer;
    }

}
