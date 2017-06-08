/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.config.NameDeletionConfigurator;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.util.CdmTitleCacheCaptionGenerator;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public class TaxonNameEditorPresenter extends AbstractCdmEditorPresenter<TaxonName, TaxonNamePopupEditorView> {

    private static final long serialVersionUID = -3538980627079389221L;

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getNomReferenceCombobox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        CdmFilterablePagingProvider<Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference>(getRepo().getReferenceService());
        getView().getNomReferenceCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());


        getView().getBasionymCombobox().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());
        CdmFilterablePagingProvider<TaxonName> namePagingProvider = new CdmFilterablePagingProvider<TaxonName>(getRepo().getNameService());
        getView().getBasionymCombobox().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(TaxonName bean) {
        NameDeletionConfigurator config = new NameDeletionConfigurator();
        return getRepo().getNameService().delete(bean.getUuid(), config);
    }


}
