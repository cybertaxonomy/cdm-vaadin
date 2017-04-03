/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.registration;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.RegistrationService;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.view.registration.ListView;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
@SpringComponent
@ViewScope
public class ListPresenter extends AbstractPresenter<ListView> {

    @Autowired
    private RegistrationService serviceMock;

    @Override
    public void onViewEnter() {
        super.onViewEnter();
        getView().populate(listRegistrations());
    }

    /**
     * @return
     */
    private Collection<RegistrationDTO> listRegistrations() {
        Collection<RegistrationDTO> dtos = serviceMock.listDTOs();
        return dtos;
    }

    @EventListener(classes=ShowDetailsEvent.class, condition = "#event.entityType == T(eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO)")
    public void onShowDetailsEvent(ShowDetailsEvent<?,?> event) { // WARNING don't use more specific generic type arguments
        RegistrationDTO regDto = serviceMock.loadDtoById((Integer)event.getIdentifier());
        if(event.getProperty().equals("messages")){
            getView().openDetailsPopup("Messages", regDto.getMessages());
        }
    }

}
