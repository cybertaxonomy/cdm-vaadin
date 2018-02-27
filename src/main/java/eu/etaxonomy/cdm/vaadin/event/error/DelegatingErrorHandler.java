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

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.ui.Notification;

/**
 * @author freimeier
 * @date 26.02.2018
 *
 */
public class DelegatingErrorHandler implements ErrorHandler{
    Map<Class<? extends Exception>, ErrorTypeHandler<? extends Exception>> handlerMap = new HashMap<>();

    public <E extends Exception> void  registerHandler(Class<E> type, ErrorTypeHandler<E> handler) {
        handlerMap.put(type, handler);
    }

    /* (non-Javadoc)
     * @see com.vaadin.server.ErrorHandler#error(com.vaadin.server.ErrorEvent)
     */
    @Override
    public void error(ErrorEvent event) {
        System.out.println(event.getThrowable().getCause().getClass());
        Throwable cause = event.getThrowable().getCause();
        if(handlerMap.get(cause.getClass()) != null){
            handlerMap.get(cause.getClass()).error(event);
          } else {
            Notification.show(event.getThrowable().getMessage());
          }
    }
}
