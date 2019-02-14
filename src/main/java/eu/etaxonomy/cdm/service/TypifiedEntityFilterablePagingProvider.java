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

import org.hibernate.criterion.Criterion;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.api.service.IIdentifiableEntityService;
import eu.etaxonomy.cdm.format.EllypsisFormatter;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.persistence.dao.common.Restriction;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.cdm.ref.TypedEntityReference;

/**
 * @author a.kohlbecker
 * @since Dec 11, 2018
 *
 */
public class TypifiedEntityFilterablePagingProvider<T extends IdentifiableEntity> implements FilterablePagingProvider<TypedEntityReference<T>>, FilterableCountProvider{

    private CdmFilterablePagingProvider<T, T> entityPagingProvider;

    private EllypsisFormatter<T> labelProvider;

    public TypifiedEntityFilterablePagingProvider(IIdentifiableEntityService<T> service, MatchMode matchMode, List<OrderHint> orderHints, EllypsisFormatter<T> labelProvider){
        this.labelProvider = labelProvider;
        entityPagingProvider = new CdmFilterablePagingProvider<T, T>(service, matchMode, orderHints);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(String filter) {
        return entityPagingProvider.size(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TypedEntityReference<T>> findEntities(int firstRow, String filter) {
        List<T> entities = entityPagingProvider.findEntities(firstRow, filter);
        List<TypedEntityReference<T>> ters = new ArrayList<>(entities.size());
        for(T e : entities){
            Class<T> type = (Class<T>)e.getClass();
            String label = labelProvider.ellypsis(e, filter);
            TypedEntityReference<T> ter = new TypedEntityReference<T>(type, e.getUuid(), label);
            ters.add(ter);
        }
        return ters;
    }

    /**
     * @param restriction
     */
    public void addRestriction(Restriction restriction) {
        entityPagingProvider.addRestriction(restriction);
    }

    /**
     * @return
     */
    public int getPageSize() {
        return entityPagingProvider.getPageSize();
    }

    /**
     * @param asList
     */
    public void setInitStrategy(List<String> initStrategy) {
        entityPagingProvider.setInitStrategy(initStrategy);
    }

    public void setMatchMode(MatchMode matchMode){
        entityPagingProvider.setMatchMode(matchMode);
    }

    /**
     * @deprecated criteria should not be used externally from cdmlib-persistence
     */
    @Deprecated
    public List<Criterion> getCriteria() {
        return entityPagingProvider.getCriteria();
    }

    /**
     * @deprecated criteria should not be used externally from cdmlib-persistence
     */
    @Deprecated
    public void addCriterion(Criterion criterion){
        entityPagingProvider.addCriterion(criterion);
    }

}
