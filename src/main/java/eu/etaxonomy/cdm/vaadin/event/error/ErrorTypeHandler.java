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
import com.vaadin.server.ErrorHandler;

/**
 * see {@link DelegatingErrorHandler}
 *
 * @author freimeier
 *
 */
public abstract class ErrorTypeHandler<E extends Throwable> implements ErrorHandler {

    private  static final long serialVersionUID = 1782060185842059311L;

    public abstract Class<E> supports();

    public final void handleError(ErrorEvent event, E throwable){
        error(event);
        exception(throwable);
    }

    /**
     * @param exception
     */
    public abstract void exception(E throwable);
}



