/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.util.concurrent.ListenableFuture;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.api.application.ICdmRepository;
import eu.etaxonomy.cdm.api.security.IAbstractRequestTokenStore;
import eu.etaxonomy.cdm.api.security.PasswordResetRequest;
import eu.etaxonomy.cdm.api.service.security.AccountSelfManagementException;
import eu.etaxonomy.cdm.vaadin.event.UserAccountEvent;
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
    private IAbstractRequestTokenStore tokenStore;

    protected EventBus.UIEventBus uiEventBus;

    PasswordResetRequest resetRequest = null;

    @Autowired
    protected void setUIEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

    @Override
    public void handleViewEntered() {

        boolean debug = false;
        if(debug) {
            getView().setUserName("debug-user");
        } else {
            List<String> viewParameters = getNavigationManager().getCurrentViewParameters();
            if(viewParameters.size() != 1  || !tokenStore.isEligibleToken(viewParameters.get(0))) {
                // invalid token show error
                getView().showErrorMessage("Invalid token");
            }
            Optional<PasswordResetRequest> resetRequestOpt = tokenStore.findResetRequest(viewParameters.get(0));
            if(resetRequestOpt.isPresent()) {
                resetRequest = resetRequestOpt.get();
                getView().setUserName(resetRequest.getUserName());
            }
        }
    }

    @EventBusListenerMethod
    public void onPasswordRevoveryEvent(UserAccountEvent event) throws AccountSelfManagementException, ExecutionException {

        if(event.getAction().equals(UserAccountEvent.UserAccountAction.RESET_PASSWORD)) {
            String newPassword = getView().getPassword1Field().getValue();

            CountDownLatch passwordChangedSignal = new CountDownLatch(1);
            List<Throwable> asyncException = new ArrayList<>(1);
            ListenableFuture<Boolean> resetPasswordFuture = repo.getPasswordResetService().resetPassword(resetRequest.getToken(), newPassword);
            resetPasswordFuture.addCallback(requestSuccessVal -> {
                passwordChangedSignal.countDown();
            }, futureException -> {
                asyncException.add(futureException);
                passwordChangedSignal.countDown();
            });
            // -- wait for passwordResetService.resetPassword to complete
            boolean asyncTimeout = false;
            Boolean result = false;
            try {
                passwordChangedSignal.await(2, TimeUnit.SECONDS);
                result = resetPasswordFuture.get();
            } catch (InterruptedException e) {
                asyncTimeout = true;
            }
            if(!asyncException.isEmpty()) {
                if(asyncException.get(0) instanceof MailException) {
                    getView().showSuccessMessage("Your password has been changed but sending the confirmation email has failed.");
                } else if(asyncException.get(0) instanceof AccountSelfManagementException) {
                    getView().showErrorMessage("The password reset token has beceome invalid. Please request gain for a password reset.");
                }
           } else {
                if(!asyncTimeout && result) {
                    getView().showSuccessMessage("Your password has been changed and a confirmation email has been sent to you.");
                } else {
                    getView().showErrorMessage("A timeout has occured, please try again.");
                }
            }

        }
    }
}
