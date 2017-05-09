/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Collection;
import java.util.EnumSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.persistence.hibernate.permission.Role;
import eu.etaxonomy.cdm.service.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.security.RolesAndPermissions;
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
    @Qualifier(IRegistrationWorkingSetService.ACTIVE_IMPL)
    private IRegistrationWorkingSetService workingSetService;

    @Override
    public void handleViewEntered() {
        getView().populate(listRegistrations());
    }

    /**
     * FIXME write test !!!!!!!!!!!!!!!!!
     *
     * @return
     */
    private Collection<RegistrationDTO> listRegistrations() {

        // list all if the authenticated user is having the role CURATION of if it is an admin
        Authentication authentication = currentSecurityContext().getAuthentication();
        User submitter = null;
        if(!authentication.getAuthorities().stream().anyMatch(ga -> ga.equals(RolesAndPermissions.ROLE_CURATION) || ga.equals(Role.ROLE_ADMIN))){
            submitter = (User) authentication.getPrincipal();
        }

        // determine whether to show all or only registrations in progress
        EnumSet<RegistrationStatus> includeStatus = null;
        try {
            if(getNavigationManager().getCurrentViewParameters().get(0).equals(ListViewBean.OPTION_IN_PROGRESS)){
                includeStatus = inProgressStatus;
            }
        } catch (IndexOutOfBoundsException e){
            // no parameter provided:  IGNORE
        }

        Collection<RegistrationDTO> dtos = workingSetService.listDTOs(submitter, includeStatus);
        return dtos;
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.entityType == T(eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO)")
    public void onShowDetailsEvent(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        RegistrationDTO regDto = workingSetService.loadDtoById((Integer)event.getIdentifier());
        if(event.getProperty().equals("messages")){
            if(getView() != null){
                getView().openDetailsPopup("Messages", regDto.getMessages());
            }
        }
    }

}
