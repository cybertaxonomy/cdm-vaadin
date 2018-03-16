// $Id$
/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.error;

import org.springframework.orm.hibernate5.HibernateSystemException;

import com.vaadin.ui.Notification;

import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.i18n.Messages;

/**
 * @author freimeier
 * @date 26.02.2018
 *
 */
public class HibernateSystemErrorHandler extends ErrorTypeHandler<HibernateSystemException>{

    private static final long serialVersionUID = -5703485298578474572L;

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.util.errorhandler.ErrorTypeHandler#handleError(com.vaadin.server.ErrorEvent)
     */
    @Override
    public void handleError(HibernateSystemException exception) {
        if(exception != null) {
            if(exception.getCause().getClass().equals(PermissionDeniedException.class)) {
                Notification.show(Messages.getLocalizedString(Messages.PermissionDeniedErrorHandler_ERROR_MSG));
            }else {
                Notification.show(exception.getCause().getMessage());
            }
        }
    }

}
