// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import org.json.JSONException;
import org.springframework.transaction.TransactionStatus;

import eu.etaxonomy.cdm.api.application.ICdmApplicationConfiguration;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.IConceptRelationshipComponentListener;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
public class ConceptRelationshipPresenter implements IConceptRelationshipComponentListener {


    private final D3ConceptRelationshipTree crTree;
    private final ITaxonService taxonService;

    private final ICdmApplicationConfiguration app;


    public ConceptRelationshipPresenter(D3ConceptRelationshipTree crTree) {
        this.crTree = crTree;

        taxonService = CdmSpringContextHelper.getTaxonService();
        app = CdmSpringContextHelper.getApplicationConfiguration();
    }

    @Override
    public void refreshRelationshipView(IdUuidName taxonIun) throws JSONException {
        TransactionStatus tx = app.startTransaction();
        Taxon taxon = CdmBase.deproxy(taxonService.load(taxonIun.getUuid()), Taxon.class);
        crTree.update(taxon);
        app.commitTransaction(tx);
    }



}
