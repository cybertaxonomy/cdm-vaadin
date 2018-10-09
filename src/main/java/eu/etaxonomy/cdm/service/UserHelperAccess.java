/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.utility.UserHelper;

/**
 * Provides access to the UserHelper bean.
 *
 * @author a.kohlbecker
 * @since May 23, 2017
 *
 */
@SpringComponent
public class UserHelperAccess {

    private static UserHelper userHelper;

    @Autowired
    public void setUserUelper(UserHelper userHelper){
        UserHelperAccess.userHelper = userHelper;
    }

    /**
     * Static accessor method to obtain the UserHelper bean
     *
     * @return
     */
    public static UserHelper userHelper() {
       return userHelper;
    }



}
