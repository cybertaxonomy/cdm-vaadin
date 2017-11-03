/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Arrays;
import java.util.List;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
public class RegistrationEditorPresenter extends AbstractCdmEditorPresenter<Registration, RegistrationPopEditorView> {

    private static final long serialVersionUID = 6930557602995331944L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRegistrationService getService() {
        return getRepo().getRegistrationService();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Registration loadCdmEntityById(Integer identifier) {

        Registration reg;
        if(identifier != null){
            List<String> initStrategy = Arrays.asList(new String[] {"$"});
            reg = getRepo().getRegistrationService().load(identifier, initStrategy );
        } else {
            reg = Registration.NewInstance();
        }
        return reg;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Integer identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(Registration.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Registration bean) {
        if(crud != null){
            newAuthorityCreated = UserHelper.fromSession().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());

        getView().getInstitutionField().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(Institution.class));
        getView().getInstitutionField().setItemCaptionPropertyId("titleCache");

        getView().getSubmitterField().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(User.class));
        getView().getSubmitterField().setItemCaptionPropertyId("username");
    }



}
