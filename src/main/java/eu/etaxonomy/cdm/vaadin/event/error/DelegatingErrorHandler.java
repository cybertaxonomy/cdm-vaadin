/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.event.error;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.event.ListenerMethod;
import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.ServerRpcManager.RpcInvocationException;
import com.vaadin.ui.Notification;

/**
 * Vaadin allows setting an com.vaadin.server.ErrorHandler for UIs and components.
 * The caveat with this built in approach is that there is only one {@link com.vaadin.server.ErrorHandler}
 * for any type of errors. This <code>DelegatingErrorHandler</code> allows registering handlers for specific types of errors.
 *
 * see https://dev.e-taxonomy.eu/redmine/issues/7241
 *
 * @author freimeier
 * @author a.kohlbecker
 *
 */
public class DelegatingErrorHandler implements ErrorHandler{

    private static final long serialVersionUID = 3378605204517477112L;

    List<ErrorTypeHandler<? extends Throwable>> handlers = new ArrayList<>();

    public <E extends Throwable> void  registerHandler(ErrorTypeHandler<E> handler) {
        assert findHandler(handler.supports()) == null;
        handlers.add(handler);
    }

    @SuppressWarnings("unchecked")
    public <E extends Throwable> ErrorTypeHandler<E> findHandler(Class<E> errorClass){
        for(ErrorTypeHandler<?> h : handlers){
            if(h.supports().isAssignableFrom(errorClass)){
                return (ErrorTypeHandler<E>) h;
            }
        }
        return null;

    }

    /* (non-Javadoc)
     * @see com.vaadin.server.ErrorHandler#error(com.vaadin.server.ErrorEvent)
     */
    @Override
    public void error(ErrorEvent event) {

        boolean handlerFound = true;
        Throwable throwable = event.getThrowable();
        while(throwable != null && (
                RpcInvocationException.class.isAssignableFrom(throwable.getClass()) ||
                InvocationTargetException.class.isAssignableFrom(throwable.getClass()) ||
                ListenerMethod.MethodException.class.isAssignableFrom(throwable.getClass())
                )
            ){
            // we are only interested into the cause in these cases
            throwable = throwable.getCause().getCause();
            event.setThrowable(throwable);
        }
        while(throwable != null){
            if(delegate(event, throwable)){
                break;
            }
            throwable = throwable.getCause();
        }
        if(!handlerFound){
            Notification.show(event.getThrowable().getMessage());
          }
    }

    private <E extends Throwable> boolean delegate(ErrorEvent event, E throwable){

        Class<E> errorClass = (Class<E>) throwable.getClass();
        Logger.getLogger(this.getClass()).debug(errorClass);
        ErrorTypeHandler<E> handler = findHandler(errorClass);
        if(handler != null){
            handler.handleError(event, throwable);
            return true;
        }
        return false;
    }
}
