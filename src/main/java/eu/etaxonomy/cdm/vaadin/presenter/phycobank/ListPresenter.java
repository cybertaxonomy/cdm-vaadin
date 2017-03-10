/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.mock.Registration;
import eu.etaxonomy.cdm.mock.RegistrationService;
import eu.etaxonomy.cdm.vaadin.view.phycobank.ListView;
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
        getView().populateTable(listRegistrations());
    }

    /**
     * @return
     */
    private Collection<RegistrationDTO> listRegistrations() {
        Collection<Registration> registrations = serviceMock.list();
        Collection<RegistrationDTO> dtos = new ArrayList<>(registrations.size());
        registrations.forEach(reg -> { dtos.add(new RegistrationDTO(reg)); });
        return dtos;
    }

}
