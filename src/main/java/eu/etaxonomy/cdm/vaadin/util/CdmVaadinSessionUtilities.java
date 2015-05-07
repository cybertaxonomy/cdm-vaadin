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

import java.util.concurrent.locks.Lock;

import org.apache.log4j.Logger;

import com.vaadin.server.VaadinSession;

import eu.etaxonomy.cdm.vaadin.session.BasicEventService;
import eu.etaxonomy.cdm.vaadin.session.CdmDataChangeService;
import eu.etaxonomy.cdm.vaadin.session.SelectionService;

/**
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class CdmVaadinSessionUtilities {

    private static final Logger logger = Logger.getLogger(CdmVaadinSessionUtilities.class);

    public static void setCurrentAttribute(String name, Object value) {
        Lock sessionLock = VaadinSession.getCurrent().getLockInstance();
        try {
            if(sessionLock != null) {
                sessionLock.lock();
            }
            VaadinSession.getCurrent().setAttribute(name, value);
        } finally {
            if(sessionLock != null) {
                sessionLock.unlock();
            }
        }
    }

    public static void initCdmDataChangeService() {
//        if(getCurrentCdmDataChangeService() != null) {
//           logger.info("replacing data change service with new one");
//        }
        setCurrentAttribute(CdmDataChangeService.KEY, new CdmDataChangeService());
    }

    public static CdmDataChangeService getCurrentCdmDataChangeService() {
        return (CdmDataChangeService) VaadinSession.getCurrent().getAttribute(CdmDataChangeService.KEY);
    }

    public static void initSelectionService() {
//        if(getCurrentSelectionService() != null) {
//            logger.info("replacing selection service with new one");
//        }
        setCurrentAttribute(SelectionService.KEY, new SelectionService());
    }

    public static SelectionService getCurrentSelectionService() {
        return (SelectionService) VaadinSession.getCurrent().getAttribute(SelectionService.KEY);
    }

    public static void initBasicEventService() {
//        if(getCurrentBasicEventService() != null) {
//            logger.info("replacing basic event service with new one");
//        }
        setCurrentAttribute(BasicEventService.KEY, new BasicEventService());
    }

    public static BasicEventService getCurrentBasicEventService() {
        return (BasicEventService) VaadinSession.getCurrent().getAttribute(BasicEventService.KEY);
    }
}
