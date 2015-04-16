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

import java.util.Set;
import java.util.UUID;

import org.json.JSONException;
import org.springframework.transaction.TransactionStatus;

import eu.etaxonomy.cdm.api.application.ICdmApplicationConfiguration;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationshipType;
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
    private final ITermService termService;
    private final ICdmApplicationConfiguration app;


    public ConceptRelationshipPresenter(D3ConceptRelationshipTree crTree) {
        this.crTree = crTree;

        taxonService = CdmSpringContextHelper.getTaxonService();
        termService = CdmSpringContextHelper.getTermService();
        app = CdmSpringContextHelper.getApplicationConfiguration();
    }

    @Override
    public void refreshRelationshipView(UUID taxonUuid) throws JSONException {
        TransactionStatus tx = app.startTransaction();
        Taxon taxon = CdmBase.deproxy(taxonService.load(taxonUuid), Taxon.class);
        crTree.updateConceptRelationshipTree(taxon);
        app.commitTransaction(tx);
    }

    public IdUuidName createRelationship(UUID fromTaxonUuid, UUID relTypeUuid, UUID toTaxonUuid) {
        TransactionStatus tx = app.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(taxonService.load(fromTaxonUuid), Taxon.class);
        Taxon toTaxon = CdmBase.deproxy(taxonService.load(toTaxonUuid), Taxon.class);
        TaxonRelationshipType relType = CdmBase.deproxy(termService.load(relTypeUuid), TaxonRelationshipType.class);
        TaxonRelationship tr = fromTaxon.addTaxonRelation(toTaxon, relType, null, null);
        app.commitTransaction(tx);
        return new IdUuidName(tr.getId(), tr.getUuid(), tr.getType().getTitleCache());
    }

    public void updateRelationship(UUID fromTaxonUuid, UUID taxonRelUuid, UUID newRelTypeUuid , UUID newToTaxonUuid) {
        TransactionStatus tx = app.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(taxonService.load(fromTaxonUuid), Taxon.class);
        for(TaxonRelationship tr : fromTaxon.getRelationsFromThisTaxon()) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                if(newRelTypeUuid != null) {
                    TaxonRelationshipType relType = CdmBase.deproxy(termService.load(newRelTypeUuid), TaxonRelationshipType.class);
                    tr.setType(relType);
                }
                if(newToTaxonUuid != null) {
                    Taxon toTaxon = CdmBase.deproxy(taxonService.load(newToTaxonUuid), Taxon.class);
                    tr.setToTaxon(toTaxon);
                }
            }
        }
        app.commitTransaction(tx);
    }

    public void deleteRelationship(UUID fromTaxonUuid, UUID taxonRelUuid) {
        TransactionStatus tx = app.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(taxonService.load(fromTaxonUuid), Taxon.class);
        TaxonRelationship trToDelete = null;
        Set<TaxonRelationship> trList = fromTaxon.getRelationsFromThisTaxon();
        for(TaxonRelationship tr : trList) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                trToDelete = tr;
            }
        }
        if(trToDelete != null) {
            trList.remove(trToDelete);
        }
        app.commitTransaction(tx);
    }

}
