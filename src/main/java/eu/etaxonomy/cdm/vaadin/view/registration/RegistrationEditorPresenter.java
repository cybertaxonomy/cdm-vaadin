/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since May 15, 2017
 *
 */
public class RegistrationEditorPresenter extends AbstractCdmEditorPresenter<Registration, RegistrationPopEditorView> {

    private static final long serialVersionUID = 6930557602995331944L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(Registration bean) {
        return getRepo().getRegistrationService().delete(bean);
    }


}