/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import com.vaadin.server.VaadinSession;

/**
 * Abstract VaadinUserHelper which auto registers in the VaadinSession.
 *
 * @author a.kohlbecker
 * @since May 23, 2017
 *
 */
public abstract class AbstractVaadinUserHelper implements VaadinUserHelper {

    public AbstractVaadinUserHelper() {
        VaadinSession.getCurrent().setAttribute(VADDIN_SESSION_KEY, this);
    }


}
