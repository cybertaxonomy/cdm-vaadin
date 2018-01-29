/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Arrays;
import java.util.List;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.NameTypeDesignationStatus;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProvider;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityButtonUpdater;
import eu.etaxonomy.cdm.vaadin.event.ToOneRelatedEntityReloader;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since Jan 26, 2018
 *
 */
public class NameTypeDesignationPresenter
        extends AbstractCdmEditorPresenter<NameTypeDesignation, NameTypeDesignationEditorView> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected NameTypeDesignation loadCdmEntityById(Integer identifier) {
        List<String> initStrategy = Arrays.asList(new String []{
                "$",
                "typifiedNames.typeDesignations", // important !!
                "typeName.$",
                "citation.authorship.$",
                }
        );

        NameTypeDesignation typeDesignation;
        if(identifier != null){
            typeDesignation = (NameTypeDesignation) getRepo().getNameService().loadTypeDesignation(identifier, initStrategy);
        } else {
            if(beanInstantiator != null){
                typeDesignation = beanInstantiator.createNewBean();
            } else {
                typeDesignation = NameTypeDesignation.NewInstance();
            }
        }
        return typeDesignation;
    }




    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {

        CdmBeanItemContainerFactory selectFactory = new CdmBeanItemContainerFactory(getRepo());
        getView().getTypeStatusSelect().setContainerDataSource(selectFactory.buildBeanItemContainer(NameTypeDesignationStatus.class));

        CdmFilterablePagingProvider<Reference,Reference> referencePagingProvider = new CdmFilterablePagingProvider<Reference, Reference>(getRepo().getReferenceService());
        getView().getCitationCombobox().loadFrom(referencePagingProvider, referencePagingProvider, referencePagingProvider.getPageSize());
        getView().getCitationCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<Reference>(getView().getCitationCombobox()));
        getView().getCitationCombobox().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getCitationCombobox(), this));

        CdmFilterablePagingProvider<TaxonName,TaxonName> namePagingProvider = new CdmFilterablePagingProvider<TaxonName, TaxonName>(getRepo().getNameService());
        getView().getTypeNameField().loadFrom(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize());
        getView().getTypeNameField().getSelect().addValueChangeListener(new ToOneRelatedEntityButtonUpdater<TaxonName>(getView().getTypeNameField()));
        getView().getTypeNameField().getSelect().addValueChangeListener(new ToOneRelatedEntityReloader<>(getView().getTypeNameField(), this));

        getView().getTypifiedNamesComboboxSelect().setPagingProviders(namePagingProvider, namePagingProvider, namePagingProvider.getPageSize(), this);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(NameTypeDesignation.class, identifier, crud, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(NameTypeDesignation bean) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IService<NameTypeDesignation> getService() {
        // TODO Auto-generated method stub
        return null;
    }

}
