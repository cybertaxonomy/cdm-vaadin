/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter.phycobank;

import eu.etaxonomy.cdm.mock.Registration;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public enum RegistrationType {

    name, typification, invalid;

    /**
     * @param reg
     * @return
     */
    public static RegistrationType from(Registration reg) {
        if(reg.getName() != null){
            return name;
        }
        if(reg.getTypeDesignations().size() > 0){
            return typification;
        }
        return invalid;
    }

    /**
     * @return
     */
    public boolean isName() {
        return name.equals(this);

  }
    /**
     * @return
     */
    public boolean isTypification() {
        return typification.equals(this);
    }

}
