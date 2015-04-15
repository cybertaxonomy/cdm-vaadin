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
 * @date 9 Apr 2015
 *
 */
public class SelectionService {

    public final static String KEY = "key_selectionService";

    private final List<ISelectionListener> listeners;

    private final List<SelectionEvent> currentEvents;

    public SelectionService() {
        listeners = new ArrayList<ISelectionListener>();
        currentEvents = new ArrayList<SelectionEvent>();
    }

    public void register(ISelectionListener listener) {
        listeners.add(listener);
    }

    public void addEvent(SelectionEvent event) {
        currentEvents.add(event);
    }

    public void fireCurrentSelectionEvents(boolean async) {
        for(SelectionEvent event : currentEvents) {
            fireSelectionEvent(event,async);
        }
        currentEvents.clear();
    }

    public void fireSelectionEvent(final SelectionEvent event, boolean async) {
        for(final ISelectionListener listener : listeners) {
            if(async) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        listener.onSelect(event);
                    }
                });
            } else {
                listener.onSelect(event);
            }
        }
    }
}
