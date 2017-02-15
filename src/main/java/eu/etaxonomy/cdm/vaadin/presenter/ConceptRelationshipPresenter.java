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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree.Direction;
import eu.etaxonomy.cdm.vaadin.view.IConceptRelationshipComponentListener;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
@SpringComponent
@Scope("prototype")
public class ConceptRelationshipPresenter implements IConceptRelationshipComponentListener {

    @Autowired
    private CdmRepository cdmRepo = null;

    private D3ConceptRelationshipTree crTree = null;

    public ConceptRelationshipPresenter(){
    }

    public void setTree(D3ConceptRelationshipTree crTree) {
        this.crTree = crTree;
    }

    @Override
    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public void refreshRelationshipView(IdUuidName taxonIun, Direction direction) throws JSONException {
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon taxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(taxonIun.getUuid()), Taxon.class);
        crTree.update(taxon, direction);
        cdmRepo.commitTransaction(tx);
    }

    @Override
    public void clearRelationshipView() {
        crTree.clear();
    }



}
