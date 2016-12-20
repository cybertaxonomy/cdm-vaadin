/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.component.CdmProgressComponent;
import eu.etaxonomy.cdm.vaadin.session.CdmChangeEvent;

/**
 * @author cmathew
 * @date 14 Apr 2015
 *
 */
public abstract class CdmVaadinOperation implements Runnable {
    private static final Logger logger = Logger.getLogger(CdmVaadinOperation.class);

    private int pollInterval = -1;
    private CdmProgressComponent progressComponent;

    private boolean opDone = false;

    List<CdmChangeEvent> events = new ArrayList<CdmChangeEvent>();

    private Date now = new java.util.Date();

    private Exception exception;



    public CdmVaadinOperation(int pollInterval, CdmProgressComponent progressComponent) {
        this.pollInterval = pollInterval;
        this.progressComponent = progressComponent;

        UI.getCurrent().setPollInterval(pollInterval);

        // comment out below for debugging
//        logger.warn(new Timestamp(now.getTime()) + " : set polling interval to " + pollInterval);
//        UI.getCurrent().addPollListener(new UIEvents.PollListener() {
//            @Override
//            public void poll(UIEvents.PollEvent event) {
//                logger.warn( new Timestamp(now.getTime()) + " : polling");
//            }
//        });
    }

    public CdmVaadinOperation() {

    }



    public void setProgress(final String progressText) {
        if(progressComponent == null) {
            return;
        }
        if(isAsync()) {
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    progressComponent.setProgress(progressText);
                    //logger.warn("set progress spinner");
                }
            });
        } else {
            progressComponent.setProgress(progressText);
        }


    }

    public void setProgress(final String progressText, final float progress) {
        if(progressComponent == null) {
            return;
        }
        if(isAsync()) {
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    progressComponent.setProgress(progressText, progress);
                }
            });
        } else {
            progressComponent.setProgress(progressText, progress);
        }
    }

    public void endProgress() {
        if(progressComponent == null) {
            return;
        }
        if(isAsync()) {
            UI.getCurrent().access(new Runnable() {
                @Override
                public void run() {
                    progressComponent.setVisible(false);
                }
            });
        } else {
            progressComponent.setVisible(false);
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            final boolean success = execute();
            //logger.warn(new Timestamp(now.getTime()) + " : ran execute");

            if(isAsync()) {
                UI.getCurrent().access(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(success) {
                                fireDelayedEvents();
                            }
                            if(exception != null) {
                                Notification notification = new Notification(exception.getMessage(), "", Type.WARNING_MESSAGE);
                                notification.show(Page.getCurrent());
                                exception = null;
                            }
                            postOpUIUpdate(success);
                            //logger.warn(new Timestamp(now.getTime()) + " : ran postOpUIUpdate ");
                        } finally {
                            UI.getCurrent().setPollInterval(-1);
                            opDone = true;
                            //logger.warn(new Timestamp(now.getTime()) + " : switched off pollling");
                        }
                    }
                });
            } else {
                postOpUIUpdate(success);
            }
        } finally {
            endProgress();
        }
    }

    public abstract  boolean execute();

    public void postOpUIUpdate(boolean isOpSuccess) {}

    public void fireEvent(CdmChangeEvent event, boolean async) {
        CdmVaadinSessionUtilities.getCurrentCdmDataChangeService().fireChangeEvent(event, async);
    }

    public void registerDelayedEvent(CdmChangeEvent event) {
        events.add(event);
    }

    private void fireDelayedEvents() {
        for(CdmChangeEvent event : events) {
            fireEvent(event, false);
        }
        events.clear();
    }

    public boolean isAsync() {
        return pollInterval > 0;
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }


}
