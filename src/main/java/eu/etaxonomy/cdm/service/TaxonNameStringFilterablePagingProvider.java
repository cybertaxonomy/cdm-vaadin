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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;

import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.api.utility.TaxonNamePartsFilter;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.persistence.dto.TaxonNameParts;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;

/**
 * IMPORTANT !!!
 *
 * The string representations returned as rankSpecificNamePart must be unique in the database since these are being used as weak references between e.g.
 * genus name and the TaxonName entity for this genus.
 *
 * @author a.kohlbecker
 * @since Jun 7, 2017
 *
 */
public class TaxonNameStringFilterablePagingProvider implements FilterableStringRepresentationPagingProvider<UUID>, FilterableCountProvider {

    private static final List<String> DEFAULT_INIT_STRATEGY = Arrays.asList("$");

    private static final Logger logger = Logger.getLogger(TaxonNameStringFilterablePagingProvider.class);

    private int pageSize = 20;

    private INameService service;

    private MatchMode matchMode = MatchMode.BEGINNING;

    private List<OrderHint> orderHints = OrderHint.ORDER_BY_TITLE_CACHE.asList();

    List<String> initStrategy = DEFAULT_INIT_STRATEGY;

    private TaxonNamePartsFilter namePartsFilter = new TaxonNamePartsFilter();

    private Map<AbstractField<String>, ValueChangeListener> registeredToFields = new HashMap<>();

    private Map<String, UUID> lastPagedEntityUUIDs;


    public TaxonNameStringFilterablePagingProvider(INameService service) {
        this(service, Rank.GENUS(), null);
    }

    public TaxonNameStringFilterablePagingProvider(INameService service, Rank rank) {
        this(service, rank, null);
    }

    public TaxonNameStringFilterablePagingProvider(INameService service, Rank rank, MatchMode matchMode) {
        super();
        this.service = service;
        if(matchMode != null){
            this.matchMode = matchMode;
        }
        namePartsFilter.setRank(rank);
    }

    public void listenToFields(AbstractField<String> genusOrUninomialField, AbstractField<String> infraGenericEpithetField,
            AbstractField<String> specificEpithetField, AbstractField<String> infraSpecificEpithetField){

        unlistenAllFields();

        registerNullSave(genusOrUninomialField, e -> namePartsFilter.setGenusOrUninomial(genusOrUninomialField.getValue()));
        registerNullSave(infraGenericEpithetField, e -> namePartsFilter.setGenusOrUninomial(infraGenericEpithetField.getValue()));
        registerNullSave(specificEpithetField, e -> namePartsFilter.setGenusOrUninomial(specificEpithetField.getValue()));
        registerNullSave(infraSpecificEpithetField, e -> namePartsFilter.setGenusOrUninomial(infraSpecificEpithetField.getValue()));
    }

    /**
     *
     */
    public void unlistenAllFields() {
        for(AbstractField<String> f : registeredToFields.keySet()){
            f.removeValueChangeListener(registeredToFields.get(f));
        }
        registeredToFields.clear();
    }

    public void replaceFields(AbstractField<String> unlistenField, AbstractField<String> listenToField) throws UnknownFieldException{
        if(registeredToFields.containsKey(unlistenField)){
            ValueChangeListener listener = registeredToFields.get(unlistenField);
            unlistenField.removeValueChangeListener(listener);
            registeredToFields.remove(unlistenField);
            registerNullSave(listenToField, listener);
        } else {
            throw new UnknownFieldException();
        }
    }

    public void updateFromFields(){
        for(AbstractField<String> f : registeredToFields.keySet()){
            ValueChangeListener listener = registeredToFields.get(f);
            listener.valueChange(new Field.ValueChangeEvent(f));
        }
    }

    /**
     * @param genusOrUninomialField
     */
    protected void registerNullSave(AbstractField<String> field, ValueChangeListener listener) {
        if(field != null){
            registeredToFields.put(field, listener);
            field.addValueChangeListener(listener);
        }
    }

    /**
     * @return the matchMode
     */
    protected MatchMode getMatchMode() {
        return matchMode;
    }

    /**
     * @param matchMode the matchMode to set
     */
    protected void setMatchMode(MatchMode matchMode) {
        this.matchMode = matchMode;
    }

    /**
     * @return the orderHints
     */
    protected List<OrderHint> getOrderHints() {
        return orderHints;
    }

    /**
     * @param orderHints the orderHints to set
     */
    protected void setOrderHints(List<OrderHint> orderHints) {
        this.orderHints = orderHints;
    }

    public TaxonNamePartsFilter getFilter(){
        return namePartsFilter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> findEntities(int firstRow, String filter) {

        Integer pageIndex = firstRow / pageSize;
        Pager<TaxonNameParts> taxonNamePager = service.findTaxonNameParts(namePartsFilter, filter, pageSize, pageIndex, orderHints);
        if(logger.isTraceEnabled()){
            logger.trace("findEntities() - page: " + taxonNamePager.getCurrentIndex() + "/" + taxonNamePager.getPagesAvailable() + " totalRecords: " + taxonNamePager.getCount() + "\n" + taxonNamePager.getRecords());
        }
        List<String> namePartStrings = new ArrayList<>(taxonNamePager.getRecords().size());
        lastPagedEntityUUIDs = new HashMap<>(taxonNamePager.getRecords().size());
        for(TaxonNameParts tnp : taxonNamePager.getRecords()){
               String rankSpecificNamePart = tnp.rankSpecificNamePart();
               String namePartKey = rankSpecificNamePart;
               if(lastPagedEntityUUIDs.containsKey(namePartKey)){
                   namePartKey = rankSpecificNamePart + " DUPLICATE[" + tnp.getTaxonNameUuid() + "]";
               }
               namePartStrings.add(namePartKey);
               lastPagedEntityUUIDs.put(namePartKey, tnp.getTaxonNameUuid());
        }
        return namePartStrings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size(String filter) {

        Pager<TaxonNameParts> taxonNamePager = service.findTaxonNameParts(namePartsFilter, filter,  1, 0, null);
        if(logger.isTraceEnabled()){
            logger.trace("size() -  count: " + taxonNamePager.getCount().intValue());
        }
        return taxonNamePager.getCount().intValue();
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return the lastPagedEntityUUIDs
     */
    public Map<String, UUID> getLastPagedEntityUUIDs() {
        return lastPagedEntityUUIDs;
    }


    public class UnknownFieldException extends Exception {

        private static final long serialVersionUID = 1L;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UUID idFor(String stringRepresentation) {
        if(lastPagedEntityUUIDs == null){
            findEntities(0, stringRepresentation);
        }
        return lastPagedEntityUUIDs.get(stringRepresentation);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearIdCache() {
        lastPagedEntityUUIDs = null;
    }

    /**
     * @param asList
     * @return
     */
    public void excludeNames(TaxonName ... excludedTaxonNames) {
        namePartsFilter.getExludedNamesUuids();
        for(TaxonName n : excludedTaxonNames){
            namePartsFilter.getExludedNamesUuids().add(n.getUuid());
        }
    }
}
