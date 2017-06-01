/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.taxon;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.config.NameDeletionConfigurator;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.vaadin.mvp.AbstractCdmEditorPresenter;

/**
 * @author a.kohlbecker
 * @since May 22, 2017
 *
 */
public class TaxonNameEditorPresenter extends AbstractCdmEditorPresenter<TaxonNameBase, TaxonNamePopupEditorView> {

    private static final long serialVersionUID = -3538980627079389221L;

    /**
     * {@inheritDoc}
     */
    @Override
    protected DeleteResult executeServiceDeleteOperation(TaxonNameBase bean) {
        NameDeletionConfigurator config = new NameDeletionConfigurator();
        return getRepo().getNameService().delete(bean.getUuid(), config);
    }


}
