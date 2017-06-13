/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

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

    private static final Logger logger = Logger.getLogger(TaxonNameEditorPresenter.class);

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

    @Override
    protected TaxonName prepareAsFieldGroupDataSource(TaxonName bean){
        TaxonName initializedBean = getRepo().getNameService().load(bean.getUuid(), Arrays.asList(
                "$",
                "basionymAuthorship",
                "combinationAuthorship",
                "exCombinationAuthorship",
                "exBasionymAuthorship",
                "nomenclaturalReference.authorship.teamMembers"));
        return initializedBean;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(TaxonName bean) {
        NameDeletionConfigurator config = new NameDeletionConfigurator();
        return getRepo().getNameService().delete(bean.getUuid(), config);
    }

    @Override
    protected TaxonName handleTransientProperties(TaxonName bean) {
        logger.trace(this._toString() + ".onEditorSaveEvent - handling transient properties");
        List<TaxonName> newBasionymNames = getView().getBasionymCombobox().getValueFromNestedFields();
        Set<TaxonName> oldBasionyms = bean.getBasionyms();
        boolean updateBasionyms = false;
        for(TaxonName newB : newBasionymNames){
            updateBasionyms = updateBasionyms || !oldBasionyms.contains(newB);
        }
        for(TaxonName oldB : oldBasionyms){
            updateBasionyms = updateBasionyms || !newBasionymNames.contains(oldB);
        }
        if(updateBasionyms){
            bean.removeBasionyms();
            for(TaxonName basionymName :newBasionymNames){
                getSession().merge(basionymName);
                bean.addBasionym(basionymName);
            }
        }
        return bean;

    }


}
