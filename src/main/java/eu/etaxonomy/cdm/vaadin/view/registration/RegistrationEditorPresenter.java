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
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.model.agent.Institution;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.permission.User;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.util.converter.JodaDateTimeConverter;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.BeanInstantiator;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
@SpringComponent
@Scope("prototype")
public class RegistrationEditorPresenter extends AbstractCdmEditorPresenter<Registration, RegistrationPopEditorView> {

    private static final long serialVersionUID = 6930557602995331944L;
    private RegistrationStatus lastStatus;

    /**
     * {@inheritDoc}
     */
    @Override
    protected IRegistrationService getService() {
        return getRepo().getRegistrationService();
    }


    protected static BeanInstantiator<Registration> defaultBeanInstantiator = new BeanInstantiator<Registration>() {

        @Override
        public Registration createNewBean() {
            return Registration.NewInstance();
        }
    };


    @Override
    protected BeanInstantiator<Registration> defaultBeanInstantiator(){
       return defaultBeanInstantiator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Registration loadCdmEntity(UUID identifier) {

        Registration reg;
        if(identifier != null){
            List<String> initStrategy = Arrays.asList(new String[] {"$", "typeDesignations"});
            reg = getRepo().getRegistrationService().load(identifier, initStrategy );
        } else {
            reg = createNewBean();
        }
        return reg;
    }



    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(UUID identifier) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(Registration.class, identifier, crud, null);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void guaranteePerEntityCRUDPermissions(Registration bean) {
        if(crud != null){
            newAuthorityCreated = UserHelperAccess.userHelper().createAuthorityForCurrentUser(bean, crud, null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleViewEntered() {
        super.handleViewEntered();

        getView().getInstitutionField().setContainerDataSource(cdmBeanItemContainerFactory.buildBeanItemContainer(Institution.class));
        getView().getInstitutionField().setItemCaptionPropertyId("titleCache");

        getView().getSubmitterField().setContainerDataSource(cdmBeanItemContainerFactory.buildBeanItemContainer(User.class));
        getView().getSubmitterField().setItemCaptionPropertyId("username");


    }

    @Override
    protected void adaptDataProviders() {
        getView().getStatusSelect().addValueChangeListener( e -> updateRegStatus((RegistrationStatus) e.getProperty().getValue()) );
    }

    private void updateRegStatus(RegistrationStatus status){
        Registration reg = ((AbstractPopupEditor<Registration, RegistrationEditorPresenter>)getView()).getBean();
        if(lastStatus != null){
            // set last status again to allow updateStatusAndDate() to the job
            reg.setStatus(lastStatus);
        }
        reg.updateStatusAndDate(status);
        lastStatus = status;
        JodaDateTimeConverter converter = new JodaDateTimeConverter();
        getView().getRegistrationDateField().setValue(converter.convertToPresentation(reg.getRegistrationDate(), Date.class, null));
    }



}
