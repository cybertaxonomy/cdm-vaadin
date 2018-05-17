/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.error;

import org.hibernate.HibernateException;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;

import eu.etaxonomy.cdm.database.PermissionDeniedException;
import eu.etaxonomy.cdm.i18n.Messages;

/**
 * @author freimeier
 *
 */
public class HibernateExceptionHandler extends ErrorTypeHandler<HibernateException>{

    private static final long serialVersionUID = -5703485298578474572L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<HibernateException> supports() {
        return HibernateException.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void error(ErrorEvent event) {
        // not needed in this class so far
    }

    @Override
    public void exception(HibernateException exception) {
        if(exception != null) {
            Notification notification = new Notification(exception.getMessage());
            if(exception.getCause() != null) {
                if(exception.getCause().getClass().equals(PermissionDeniedException.class)) {
                    notification = new Notification(Messages.getLocalizedString(Messages.HibernateExceptionHandler_PERMISSION_DENIED));
                }else {
                    notification = new Notification(exception.getCause().getMessage());
                }
            }
            notification.show(Page.getCurrent());
        }
    }



}
