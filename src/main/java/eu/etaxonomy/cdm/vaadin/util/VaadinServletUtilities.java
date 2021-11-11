/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;

/**
 * @author a.kohlbecker
 * @since Nov 10, 2021
 */
public class VaadinServletUtilities {

    public static URL getServletBaseUrl() throws MalformedURLException {
        URI baseURI = UI.getCurrent().getPage().getLocation();

        // int port = baseURI.getPort(); // ((baseURI.getPort() != 80 && baseURI.getScheme().equals("http")) || (baseURI.getPort() != 334 && baseURI.getScheme().equals("https")) ? ":" + baseURI.getPort() : ""), null);

        URL baseURL  = new URL(
                baseURI.getScheme(),
                baseURI.getHost(),
                baseURI.getPort(),
                VaadinServlet.getCurrent().getServletContext().getContextPath()
                );
        return baseURL;
    }

}
