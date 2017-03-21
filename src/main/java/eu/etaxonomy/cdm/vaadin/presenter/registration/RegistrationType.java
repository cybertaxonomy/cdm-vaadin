/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.registration;

import eu.etaxonomy.cdm.mock.Registration;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public enum RegistrationType {

    NAME, TYPIFICATION, INVALID;

    /**
     * @param reg
     * @return
     */
    public static RegistrationType from(Registration reg) {
        if(reg.getName() != null){
            return NAME;
        }
        if(reg.getTypeDesignations().size() > 0){
            return TYPIFICATION;
        }
        return INVALID;
    }

    /**
     * @return
     */
    public boolean isName() {
        return NAME.equals(this);

  }
    /**
     * @return
     */
    public boolean isTypification() {
        return TYPIFICATION.equals(this);
    }

}
