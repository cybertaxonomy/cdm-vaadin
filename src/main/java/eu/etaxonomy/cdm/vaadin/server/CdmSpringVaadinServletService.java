/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.server;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.spring.server.SpringVaadinServletService;

/**
 * @author a.kohlbecker
 * @since Jul 4, 2017
 *
 */
public class CdmSpringVaadinServletService extends SpringVaadinServletService {

    private static final long serialVersionUID = 5956798985059823698L;

    List<RequestEndListener> requestEndListeners = new ArrayList<>();

    List<RequestStartListener> requestStartListeners = new ArrayList<>();

    /**
     * @param servlet
     * @param deploymentConfiguration
     * @param serviceUrl
     * @throws ServiceException
     */
    public CdmSpringVaadinServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration,
            String serviceUrl) throws ServiceException {
        super(servlet, deploymentConfiguration, serviceUrl);
    }

    @Override
    public void requestStart(VaadinRequest request, VaadinResponse response) {
        super.requestStart(request, response);
        requestStartListeners.forEach(l -> l.onRequestStart(request));
    }

    @Override
    public void requestEnd(VaadinRequest request, VaadinResponse response,
            VaadinSession session) {
        super.requestEnd(request, response, session);
        requestEndListeners.forEach(l -> l.onRequestEnd(request, session));
    }

    public void addRequestStartListener(RequestStartListener requestStartListener){
        requestStartListeners.add(requestStartListener);
    }

    public void removeRequestStartListener(RequestStartListener requestStartListener){
        requestStartListeners.remove(requestStartListener);
    }

    public void addRequestEndListener(RequestEndListener requestEndListener){
        requestEndListeners.add(requestEndListener);
    }

    public void removeRequestEndListener(RequestEndListener requestEndListener){
        requestEndListeners.remove(requestEndListener);
    }






}
