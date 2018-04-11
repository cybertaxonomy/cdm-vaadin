/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.TextField;

import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.component.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.vaadin.component.registration.RegistrationItem;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.event.UpdateResultsEvent;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 *
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class ListPresenter extends AbstractPresenter<ListView> {

    private static final EnumSet<RegistrationStatus> inProgressStatus = EnumSet.of(
            RegistrationStatus.PREPARATION,
            RegistrationStatus.CURATION,
            RegistrationStatus.READY
            );

    private static final long serialVersionUID = 5419947244621450665L;

    @Autowired
    private IRegistrationWorkingSetService workingSetService;

    /**
     * @return the workingSetService
     */
    public IRegistrationWorkingSetService getWorkingSetService() {
        return workingSetService;
    }


    @Override
    public void handleViewEntered() {

        if(getNavigationManager().getCurrentViewParameters().get(0).equals(ListView.Mode.inProgress.name())){
            getView().setViewMode(ListViewBean.Mode.inProgress);
            getView().getStatusFilter().setVisible(false);
        } else {
            getView().setViewMode(ListViewBean.Mode.all);
        }

        CdmBeanItemContainerFactory selectFieldFactory = new CdmBeanItemContainerFactory(getRepo());

        if(getView().getSubmitterFilter() != null){
            getView().getSubmitterFilter().setContainerDataSource(selectFieldFactory.buildBeanItemContainer(User.class));
            getView().getSubmitterFilter().setItemCaptionPropertyId("username");
        }

        getView().populate(pageRegistrations(null, null));
    }

    /**
     * FIXME write test !!!!!!!!!!!!!!!!!
     *
     * @return
     */
    private Pager<RegistrationDTO> pageRegistrations(TextField textFieldOverride, String alternativeText) {

        // list all if the authenticated user is having the role CURATION of if it is an admin
        Authentication authentication = currentSecurityContext().getAuthentication();

        // prepare the filters
        String identifierFilter;
        if(textFieldOverride != null && textFieldOverride == getView().getIdentifierFilter()){
            identifierFilter = alternativeText;
        } else {
            identifierFilter = getView().getIdentifierFilter().getValue();
        }
        String nameFilter;
        if(textFieldOverride != null && textFieldOverride == getView().getTaxonNameFilter()){
            nameFilter = alternativeText;
        } else {
            nameFilter = getView().getTaxonNameFilter().getValue();
        }
        User submitter = null;
        if(getView().getSubmitterFilter() != null){
            Object o = getView().getSubmitterFilter().getValue();
            if(o != null){
                submitter = (User)o;
            }
        } else {
            submitter = (User) authentication.getPrincipal();
        }
        EnumSet<RegistrationStatus> includeStatus = inProgressStatus;
        if(getView().getViewMode().equals(ListView.Mode.all)){
            includeStatus = null;
            Object o = getView().getStatusFilter().getValue();
            if(o != null){
                includeStatus = EnumSet.of((RegistrationStatus)o);
            }
        }

        Pager<RegistrationDTO> dtoPager = getWorkingSetService().pageDTOs(
                submitter,
                includeStatus,
                StringUtils.trimToNull(identifierFilter),
                StringUtils.trimToNull(nameFilter),
                null,
                null);
        return dtoPager;
    }

    @EventBusListenerMethod
    public void onShowDetailsEvent(ShowDetailsEvent<RegistrationDTO, UUID> event) {

        // FIXME check from own view!!!
        if(getView() == null){
            return;
        }

        UUID registrationUuid = event.getIdentifier();

        RegistrationDTO regDto = getWorkingSetService().loadDtoByUuid(registrationUuid);
        if(event.getProperty().equals("messages")){

            getView().openDetailsPopup("Messages", regDto.getValidationProblems());

        } else if(event.getProperty().equals("blockedBy")){

            Set<RegistrationDTO> blockingRegs = getWorkingSetService().loadBlockingRegistrations(registrationUuid);
            RegistrationItem regItem = getView().getRegistrationItem(registrationUuid);
            regItem.showBlockingRegistrations(blockingRegs);
        }

    }

    @EventBusListenerMethod
    public void onEntityChangeEvent(EntityChangeEvent event){
        if(event.getEntityType().isAssignableFrom(Reference.class)){
            // TODO update component showing the according reference, is there a Vaadin event supporting this?
        }
    }

    @EventBusListenerMethod
    public void onUpdateResultsEvent(UpdateResultsEvent event){
        getView().populate(pageRegistrations(event.getField(), event.getNewText()));
    }

}
