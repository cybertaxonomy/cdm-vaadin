// $Id$
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
 * @date 21 Apr 2015
 *
 */
public class BasicEventService {


    public final static String KEY = "key_simpleEventService";

    private final List<IBasicEventListener> listeners;

    private final List<BasicEvent> currentEvents;

    public BasicEventService() {
        listeners = new ArrayList<IBasicEventListener>();
        currentEvents = new ArrayList<BasicEvent>();
    }

    public void register(IBasicEventListener listener) {
        listeners.add(listener);
    }

    public void addEvent(BasicEvent event) {
        currentEvents.add(event);
    }

    public void fireCurrentSelectionEvents(boolean async) {
        try {
            for(BasicEvent event : currentEvents) {
                fireBasicEvent(event,async);
            }
        } finally {
            currentEvents.clear();
        }
    }

    public void fireBasicEvent(final BasicEvent event, boolean async) {
        for(final IBasicEventListener listener : listeners) {
            if(async) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        listener.onAction(event);
                    }
                });
            } else {
                listener.onAction(event);
            }
        }
    }

}
