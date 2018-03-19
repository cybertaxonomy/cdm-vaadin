/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.List;

import eu.etaxonomy.cdm.ext.registration.messages.Message;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Feb 27, 2018
 *
 */
public interface RegistrationMessagesView extends ApplicationView<RegistrationMessagesPresenter> {

    /**
     * @param messages
     */
    void showMessages(String registrationLabel, List<Message> messages);

    DelegatingErrorHandler getErrrorHandler();

}
