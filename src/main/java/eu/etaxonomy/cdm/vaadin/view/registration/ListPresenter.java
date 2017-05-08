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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.IRegistrationWorkingSetService;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class ListPresenter extends AbstractPresenter<ListView> {

    private static final long serialVersionUID = 5419947244621450665L;

    @Autowired
    @Qualifier(IRegistrationWorkingSetService.ACTIVE_IMPL)
    private IRegistrationWorkingSetService workingSetService;

    @Override
    public void handleViewEntered() {
        // TransactionStatus tx = getRepo().startTransaction(); // no longer needed since AbstractPresenter is caring for tranactions in this case
        getView().populate(listRegistrations());
        // getRepo().commitTransaction(tx);
    }

    /**
     * @return
     */
    private Collection<RegistrationDTO> listRegistrations() {
        Collection<RegistrationDTO> dtos = workingSetService.listDTOs();
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
