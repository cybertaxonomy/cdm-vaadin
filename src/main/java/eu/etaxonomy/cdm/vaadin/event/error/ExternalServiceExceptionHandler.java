/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.error;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

import eu.etaxonomy.cdm.ext.common.ExternalServiceException;
import eu.etaxonomy.cdm.vaadin.ui.RegistrationUIDefaults;


public class ExternalServiceExceptionHandler extends ErrorTypeHandler<ExternalServiceException>{

    private static final long serialVersionUID = -5703485298578474572L;
    private String mainMessage;

    public ExternalServiceExceptionHandler(String mainMessage){
        this.mainMessage = mainMessage;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Class<ExternalServiceException> supports() {
        return ExternalServiceException.class;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void error(ErrorEvent event) {
        // only exception() needed in this class

    }

    @Override
    public void exception(ExternalServiceException exception) {
        Notification notification = new Notification(mainMessage,
                "<div><strong>Service:</strong> "+ exception.getExternalService() + "</div>" +
                "<div><strong>Problem:</strong> "+ exception.getProblem() + "</div>" +
                "<p>" + RegistrationUIDefaults.ERROR_CONTACT_MESSAGE_LINE + "</p>"
                , Type.ERROR_MESSAGE);
        notification.setHtmlContentAllowed(true);
        notification.show(Page.getCurrent());
    }



}
