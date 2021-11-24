/**
* Copyright (C) 2021 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.addon.config;

/**
 * @author a.kohlbecker
 * @since Nov 22, 2021
 */
public class UIDisabledException extends RuntimeException {

    private static final long serialVersionUID = -4946537312656974252L;

    private String uiName = null;

    public UIDisabledException(String uiName) {
        this.uiName = uiName;
    }

    @Override
    public String getMessage() {
        return String.format("The ui '%s' has been requested but is disabled per configuration in ~/.cdmLibrary/remote-webapp/{bean-id}/%s.properties",
                uiName,
                CdmVaadinConfiguration.PROPERTIES_FILE_NAME
                );
    }

    @Override
    public String getLocalizedMessage() {
        return super.getMessage();
    }

}
