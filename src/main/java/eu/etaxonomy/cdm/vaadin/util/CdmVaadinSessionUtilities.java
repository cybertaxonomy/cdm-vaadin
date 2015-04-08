// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.vaadin.session.CdmDataChangeService;

/**
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class CdmVaadinSessionUtilities {

    public static void setCurrentAttribute(String name, Object value) {
        try {
            VaadinSession.getCurrent().getLockInstance().lock();
            VaadinSession.getCurrent().setAttribute(name, value);
        } finally {
            VaadinSession.getCurrent().getLockInstance().unlock();
        }
    }

    public static CdmDataChangeService getCurrentCdmDataChangeService() {
        return (CdmDataChangeService) VaadinSession.getCurrent().getAttribute(CdmDataChangeService.KEY);
    }

}
