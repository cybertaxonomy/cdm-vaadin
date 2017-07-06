/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

/**
 * @author a.kohlbecker
 * @since May 8, 2017
 *
 */
public class InactiveUIException extends Exception {


    private static final long serialVersionUID = 3225226104624596439L;

    String viewName;

    public InactiveUIException(String viewName){
        super("The requested view '" + viewName + "' is not active for this web application instance.");
        this.viewName = viewName;
    }

    public String getViewName() {
        return viewName;
    }



}
