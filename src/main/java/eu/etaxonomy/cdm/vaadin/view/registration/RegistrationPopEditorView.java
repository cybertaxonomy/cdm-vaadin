/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import com.vaadin.ui.DateField;
import com.vaadin.ui.NativeSelect;

import eu.etaxonomy.vaadin.mvp.ApplicationView;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
public interface RegistrationPopEditorView
        extends ApplicationView<RegistrationPopEditorView,RegistrationEditorPresenter> {

    public NativeSelect getSubmitterField();

    public NativeSelect getInstitutionField();

    public NativeSelect getStatusSelect();

    public DateField getRegistrationDateField();
}
