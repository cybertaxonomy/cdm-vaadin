/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.server;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;

/**
 * @author a.kohlbecker
 * @since Jul 4, 2017
 *
 */
public interface RequestEndListener {

    public void onRequestEnd(VaadinRequest request, VaadinSession session);

}
