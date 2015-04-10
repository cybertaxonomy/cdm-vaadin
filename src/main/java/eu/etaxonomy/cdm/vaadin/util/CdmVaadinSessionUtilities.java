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
import eu.etaxonomy.cdm.vaadin.session.SelectionService;

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

    public static void initCdmDataChangeService() {
        setCurrentAttribute(CdmDataChangeService.KEY, new CdmDataChangeService());
    }

    public static CdmDataChangeService getCurrentCdmDataChangeService() {
        return (CdmDataChangeService) VaadinSession.getCurrent().getAttribute(CdmDataChangeService.KEY);
    }

    public static void initSelectionService() {
        setCurrentAttribute(SelectionService.KEY, new SelectionService());
    }

    public static SelectionService getCurrentSelectionService() {
        return (SelectionService) VaadinSession.getCurrent().getAttribute(SelectionService.KEY);
    }
}
