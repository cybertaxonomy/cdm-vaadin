/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.session;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.ui.UI;

/**
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class CdmDataChangeService {

    public final static String KEY = "key_cdmDataChangeService";

    private final List<ICdmChangeListener> listeners;

    private final List<CdmChangeEvent> currentEvents;

    public CdmDataChangeService() {
        listeners = new ArrayList<ICdmChangeListener>();
        currentEvents = new ArrayList<CdmChangeEvent>();
    }

    public void register(ICdmChangeListener listener) {
        listeners.add(listener);
    }

    public void addEvent(CdmChangeEvent event) {
        currentEvents.add(event);
    }

    public void fireCurrentChangeEvents(boolean async) {
        try {
            for(CdmChangeEvent event : currentEvents) {
                fireChangeEvent(event,async);
            }
        } finally {
            currentEvents.clear();
        }
    }

    public void fireChangeEvent(CdmChangeEvent event, boolean async) {
        switch(event.getAction()) {
        case Create:
            fireCreateChangeEvent(event, async);
            break;
        case Update:
            fireUpdateChangeEvent(event, async);
            break;
        case Delete:
            fireDeleteChangeEvent(event, async);
            break;
        default:
            break;
        }
    }


    public void fireCreateChangeEvent(final CdmChangeEvent event, boolean async) {
        for(final ICdmChangeListener listener : listeners) {
            if(async) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        listener.onCreate(event);
                    }
                });
            } else {
                listener.onCreate(event);
            }
        }
    }

    public void fireUpdateChangeEvent(final CdmChangeEvent event, boolean async) {
        for(final ICdmChangeListener listener : listeners) {
            if(async) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        listener.onUpdate(event);
                    }
                });
            } else {
                listener.onUpdate(event);
            }
        }
    }

    public void fireDeleteChangeEvent(final CdmChangeEvent event, boolean async) {
        for(final ICdmChangeListener listener : listeners) {
            if(async) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        listener.onDelete(event);
                    }
                });
            } else {
                listener.onDelete(event);
            }
        }
    }

    public void dispose() {
        listeners.clear();
        currentEvents.clear();
    }
}
