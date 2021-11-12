/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.vaadin.spring.events.EventBus;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.application.ICdmRepository;
import eu.etaxonomy.cdm.api.security.IPasswordResetTokenStore;
import eu.etaxonomy.cdm.api.security.PasswordResetRequest;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Nov 11, 2021
 */
@SpringComponent
@ViewScope
public class PasswordResetPresenter extends AbstractPresenter<PasswordResetView> {

    private static final long serialVersionUID = 2656148780493202130L;

    @Autowired
    @Qualifier("cdmRepository")
    private ICdmRepository repo;

    @Autowired
    private IPasswordResetTokenStore tokenStore;

    protected EventBus.UIEventBus uiEventBus;

    @Autowired
    protected void setUIEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

    @Override
    public void handleViewEntered() {

        boolean debug = true;
        if(debug) {
            getView().setUserName("debug-user");
        } else {
            List<String> viewParameters = getNavigationManager().getCurrentViewParameters();
            if(viewParameters.size() != 1  || !tokenStore.isEligibleToken(viewParameters.get(0))) {
                // invalid token show error
                getView().showErrorMessage("Invalid token");
            }
            Optional<PasswordResetRequest> resetRequest = tokenStore.findResetRequest(viewParameters.get(0));
            if(resetRequest.isPresent()) {
                getView().setUserName(resetRequest.get().getUserName());
            }
        }
    }

}
