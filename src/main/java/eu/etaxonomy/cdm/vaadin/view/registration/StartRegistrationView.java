/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import org.vaadin.viritin.fields.LazyComboBox;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.ref.TypedEntityReference;
import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since Mar 2, 2017
 *
 */
public interface StartRegistrationView extends ApplicationView<StartRegistrationPresenter>  {

    Button getNewPublicationButton();

    LazyComboBox<TypedEntityReference<Reference>> getReferenceCombobox();

    /**
     * @return
     */
    Button getRemoveNewPublicationButton();

    /**
     * @return
     */
    Button getContinueButton();

    Label getNewPublicationLabel();



}
