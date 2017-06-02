/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.taxon;

import java.util.Arrays;
import java.util.List;

import org.vaadin.viritin.fields.CaptionGenerator;
import org.vaadin.viritin.fields.LazyComboBox.FilterableCountProvider;
import org.vaadin.viritin.fields.LazyComboBox.FilterablePagingProvider;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.config.NameDeletionConfigurator;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.persistence.query.OrderHint;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public class TaxonNameEditorPresenter extends AbstractCdmEditorPresenter<TaxonNameBase, TaxonNamePopupEditorView> {

    private static final long serialVersionUID = -3538980627079389221L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getNomReferenceCombobox().getSelect().setCaptionGenerator(new CaptionGenerator<Reference>(){

            @Override
            public String getCaption(Reference option) {
                return option.getTitleCache();
            }

        });
        getView().getNomReferenceCombobox().loadFrom(new FilterablePagingProvider<Reference>(){

            @Override
            public List<Reference> findEntities(int firstRow, String filter) {
                Pager<Reference> page = getRepo().getReferenceService().findByTitle(
                        null,
                        filter,
                        MatchMode.ANYWHERE,
                        null,
                        20,
                        firstRow,
                        OrderHint.ORDER_BY_TITLE_CACHE.asList(),
                        Arrays.asList("$")
                      );
                return page.getRecords();
            }},
            new FilterableCountProvider(){
                @Override
                public int size(String filter) {
                    Pager<Reference> page = getRepo().getReferenceService().findByTitle(
                            null,
                            filter,
                            MatchMode.ANYWHERE,
                            null,
                            1,
                            0,
                            null,
                            null
                          );
                    return page.getCount().intValue();
                }}
            , 20);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(TaxonNameBase bean) {
        NameDeletionConfigurator config = new NameDeletionConfigurator();
        return getRepo().getNameService().delete(bean.getUuid(), config);
    }


}
