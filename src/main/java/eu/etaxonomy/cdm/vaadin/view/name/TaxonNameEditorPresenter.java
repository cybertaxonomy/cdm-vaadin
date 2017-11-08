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

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.model.common.TermType;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.name.TaxonNameFactory;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
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

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getRankSelect().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(TermType.Rank));
        getView().getRankSelect().setItemCaptionPropertyId("label");

        getView().getNomReferenceCombobox().getSelect().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<Reference>());
        CdmFilterablePagingProvider<Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference>(getRepo().getReferenceService());
        getView().getNomReferenceCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
        getView().getNomReferenceCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getNomReferenceCombobox()));


        getView().getBasionymCombobox().setCaptionGenerator(new CdmTitleCacheCaptionGenerator<TaxonName>());
        CdmFilterablePagingProvider<TaxonName> namePagingProvider = new CdmFilterablePagingProvider<TaxonName>(getRepo().getNameService());
        getView().getBasionymCombobox().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TaxonName loadCdmEntityById(Integer identifier) {

        List<String> initStrategy = Arrays.asList(new String []{

                "$",
                "rank.vocabulary", // needed for comparing ranks

                "nomenclaturalReference.authorship",
                "nomenclaturalReference.inReference",

                "status.type",

                "combinationAuthorship",
                "exCombinationAuthorship",
                "basionymAuthorship",
                "exBasionymAuthorship",

                "basionyms.rank",
                "basionyms.nomenclaturalReference.authorship",
                "basionyms.nomenclaturalReference.inReference",

                }
        );

        TaxonName bean;
        if(identifier != null){
            bean = getRepo().getNameService().load(identifier, initStrategy);
        } else {
            bean = TaxonNameFactory.NewBotanicalInstance(Rank.SPECIES());
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(TaxonName.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(TaxonName bean) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(bean, crud, null);
        }
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
                if(basionymName != null){
                    if(basionymName .getUuid() != null){
                        // reload
                        basionymName = getRepo().getNameService().load(basionymName.getUuid());
                    }
                    bean.addBasionym(basionymName);
                }
            }
        }
        return bean;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected INameService getService() {
        return getRepo().getNameService();
    }



}
