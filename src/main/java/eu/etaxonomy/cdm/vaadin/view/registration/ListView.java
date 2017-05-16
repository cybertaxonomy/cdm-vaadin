/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Collection;
import java.util.List;

import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 3, 2017
 *
 */
public interface ListView extends ApplicationView<ListPresenter>{

    /**
     * @param page
     */
    void populate(Collection<RegistrationDTO> registrations);

    /**
     * @param messages
     */
    void openDetailsPopup(String caption, List<String> messages);


}
