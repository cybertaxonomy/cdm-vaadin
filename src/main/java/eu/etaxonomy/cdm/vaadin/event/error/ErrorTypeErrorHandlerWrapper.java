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
 * @author a.kohlbecker
 * @since Nov 8, 2018
 *
 */
public class ErrorTypeErrorHandlerWrapper<E extends Throwable> extends ErrorTypeHandler<E> {

    private static final long serialVersionUID = 856246216519945768L;

    private Class<E> errorType;

    private ErrorHandler errorHandler;

    public ErrorTypeErrorHandlerWrapper(Class<E> errorType, ErrorHandler errorHandler){
        this.errorType = errorType;
        this.errorHandler = errorHandler;
    }

    @Override
    public void error(ErrorEvent event) {
        errorHandler.error(event);
    }

    @Override
    public Class<E> supports() {
        return errorType;
    }

    @Override
    public void exception(Throwable throwable) {
        // nothing to do, since the errorHandler is expected to provide user feedback
    }

}
