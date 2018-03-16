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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;

/**
 * Vaadin allows setting an com.vaadin.server.ErrorHandler for UIs and components.
 * The caveat with this built in approach is that there is only one {@link com.vaadin.server.ErrorHandler}
 * for any type of errors. This <code>DelegatingErrorHandler</code> allows registering handlers for specific types of errors.
 *
 * see https://dev.e-taxonomy.eu/redmine/issues/7241
 *
 * @author freimeier
 * @date 26.02.2018
 *
 */
public class DelegatingErrorHandler implements ErrorHandler{

    private static final long serialVersionUID = 3378605204517477112L;

    Map<Class<? extends Exception>, ErrorTypeHandler<? extends Exception>> handlerMap = new HashMap<>();

    public <E extends Exception> void  registerHandler(Class<E> type, ErrorTypeHandler<E> handler) {
        handlerMap.put(type, handler);
    }

    /* (non-Javadoc)
     * @see com.vaadin.server.ErrorHandler#error(com.vaadin.server.ErrorEvent)
     */
    @Override
    public void error(ErrorEvent event) {

        Class<? extends Throwable> errorClass = event.getThrowable().getCause().getClass();
        Logger.getLogger(this.getClass()).debug(errorClass);
        if(handlerMap.get(errorClass) != null){
            handlerMap.get(errorClass).error(event);
          } else {
            Notification.show(event.getThrowable().getMessage());
          }
    }
}
