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

import java.util.Iterator;
import java.util.List;

import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.component.CdmProgressComponent;

/**
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class CdmVaadinUtilities {

    /**
     * Thread safe method to asynchronously perform an update.
     *
     * @param update
     */
    public static void asyncExec(Runnable update) {
        UI currentUI = UI.getCurrent();
        Thread t = new Thread(update);
        if(currentUI != null) {
            synchronized(currentUI) {
                t.start();
            }
        } else {
            throw new UnsupportedOperationException("Cannot execute update on a UI which is null");
        }
    }

    public static void exec(CdmVaadinOperation op) {
        if(op.isAsync()) {
            asyncExec(op);
        } else {
            op.run();
        }
    }


    public static void setEnabled(Component root, boolean isEnabled, List<Component> exceptions) {
        if(exceptions != null && exceptions.contains(root)) {
            return;
        }
        if(root instanceof HasComponents) {
            HasComponents hc = (HasComponents) root;
            Iterator<Component> iterator = hc.iterator();
            while(iterator.hasNext()) {
                setEnabled(iterator.next(), isEnabled, exceptions);
            }
        } else {
            root.setEnabled(isEnabled);
        }
    }

    public static void enableOnlyProgressBar(Component component) {
        if(component instanceof CdmProgressComponent) {
            component.setEnabled(true);
            return;
        }
        if(component instanceof HasComponents) {
            HasComponents hc = (HasComponents) component;
            Iterator<Component> iterator = hc.iterator();
            while(iterator.hasNext()) {
                enableOnlyProgressBar(iterator.next());
            }
        } else {
            component.setEnabled(false);
        }
    }


}
