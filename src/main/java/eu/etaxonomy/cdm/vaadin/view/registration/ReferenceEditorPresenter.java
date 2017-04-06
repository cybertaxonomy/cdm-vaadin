/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;

import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;

/**
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
@SpringComponent
@ViewScope
public class ReferenceEditorPresenter extends AbstractEditorPresenter<Reference> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveBean(Reference bean) {
        getRepo().getReferenceService().saveOrUpdate(bean);

    }

}
