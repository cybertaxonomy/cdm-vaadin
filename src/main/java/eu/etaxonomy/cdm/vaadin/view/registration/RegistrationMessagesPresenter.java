/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.service.IRegistrationService;
import eu.etaxonomy.cdm.ext.common.ExternalServiceException;
import eu.etaxonomy.cdm.ext.registration.messages.IRegistrationMessageService;
import eu.etaxonomy.cdm.ext.registration.messages.Message;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.vaadin.event.error.ExternalServiceExceptionHandler;
import eu.etaxonomy.cdm.vaadin.permission.VaadinUserHelper;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;

/**
 * @author a.kohlbecker
 * @since Feb 27, 2018
 *
 */
@SpringComponent
@Scope("prototype")
public class RegistrationMessagesPresenter extends AbstractPresenter<RegistrationMessagesView> {

    private static final long serialVersionUID = -1069755744585623770L;

    @Autowired
    IRegistrationMessageService messageService;

    @Autowired
    IRegistrationService registrationService;

    Registration registration;

    @Override
    public void onPresenterReady() {
        getView().getErrrorHandler().registerHandler(
                new ExternalServiceExceptionHandler("The external messages service reported an error")
                );
    }

    /**
     * @param identifier
     */
    public void loadMessagesFor(UUID uuid) {
        if(registration == null){
            registration = registrationService.load(uuid, Arrays.asList("submitter"));
        }
        try {
            List<Message> messages = messageService.listMessages(registration);
            getView().showMessages("On Registration " + registration.getIdentifier(), messages);
        } catch (ExternalServiceException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * @param value
     */
    public void postMessage(String message) {

        User user = VaadinUserHelper.fromSession().user();
        List<Message> activeMessages;
        try {
            activeMessages = messageService.listActiveMessagesFor(registration, user);
        } catch (ExternalServiceException e) {
            throw new RuntimeException(e);
        }
        User toUser = null;
        if(VaadinUserHelper.fromSession().userIsRegistrationCurator()){
            toUser = registration.getSubmitter();
        } else {
            Stack<Message> stack = new Stack<>();
            stack.addAll(activeMessages);
            while(!stack.empty()){
                toUser = stack.pop().getFrom();
                if(!toUser.equals(user)){
                    break;
                }
            }
            if(toUser == null){
                throw new RuntimeException("Only a curator can initiate a communication");
            }
        }
        try {
            messageService.postMessage(registration, message, user, toUser);
            loadMessagesFor(registration.getUuid());
        } catch (ExternalServiceException e) {
            logger.error(e);
            throw new RuntimeException(e);
        }


    }

}
