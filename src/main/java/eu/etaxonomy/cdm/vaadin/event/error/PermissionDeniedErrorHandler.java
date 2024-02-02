/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.error;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.persistence.permission.PermissionDeniedException;

/**
 * @author a.kohlbecker
 * @since Apr 2, 2019
 */
public class PermissionDeniedErrorHandler implements ErrorHandler {

    private static final long serialVersionUID = -2188760740450360478L;
    private final UI ui;

    public PermissionDeniedErrorHandler(UI ui){
        this.ui = ui;
    }

    @Override
    public void error(ErrorEvent event) {
        PermissionDeniedException permissionDeniedException = (PermissionDeniedException)event.getThrowable();
        Notification.show("Permission denied", permissionDeniedException.getMessage(), Type.ERROR_MESSAGE);
    }
}