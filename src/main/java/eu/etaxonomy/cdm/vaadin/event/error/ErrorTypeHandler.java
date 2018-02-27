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

import com.vaadin.server.ErrorEvent;
import com.vaadin.server.ErrorHandler;

/**
 * @author freimeier
 * @date 26.02.2018
 *
 */
public abstract class ErrorTypeHandler<E extends Exception> implements ErrorHandler {

    /* (non-Javadoc)
     * @see com.vaadin.server.ErrorHandler#error(com.vaadin.server.ErrorEvent)
     */
    @Override
    public final void error(ErrorEvent event) {
        handleError((E) event.getThrowable().getCause());

    }

    public abstract void handleError(E exception);
}



