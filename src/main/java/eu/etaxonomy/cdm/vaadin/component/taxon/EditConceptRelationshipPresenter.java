/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.taxon;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.util.filter.Compare;

import eu.etaxonomy.cdm.api.application.ICdmApplication;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.api.service.ITermService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationshipType;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @since 13 Apr 2015
 *
 */
public class EditConceptRelationshipPresenter {

    private CdmSQLContainer taxonRTypeContainer;
    private CdmSQLContainer taxonRContainer;

    private final ITaxonService taxonService;
    private final ITermService termService;
    private final ICdmApplication app;

    public final static String REL_TYPE_KEY = "relTypeIun";
    public final static String TO_TAXON_KEY = "toTaxonIun";

    public EditConceptRelationshipPresenter() {
        taxonService = CdmSpringContextHelper.getTaxonService();
        termService = CdmSpringContextHelper.getTermService();
        app = CdmSpringContextHelper.getApplicationConfiguration();
    }

    public CdmSQLContainer loadTaxonRelationshipTypeContainer() throws SQLException {
        taxonRTypeContainer = CdmSQLContainer.newInstance("DefinedTermBase");
        taxonRTypeContainer.addContainerFilter(new Compare.Equal("DTYPE","TaxonRelationshipType"));
        taxonRTypeContainer.setPageLength(100);
        return taxonRTypeContainer;
    }

    public CdmSQLContainer getTaxonRTypeContainer() {
        return taxonRTypeContainer;
    }

    public CdmSQLContainer loadTaxonRelationshipContainer(Object itemId) throws SQLException {
        taxonRContainer = CdmSQLContainer.newInstance("TaxonRelationship");
        taxonRContainer.addContainerFilter(new Compare.Equal("relatedfrom_id", itemId.toString()));
        return taxonRContainer;
    }

    public CdmSQLContainer getTaxonRContainer() {
        return taxonRContainer;
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



    public Map<String, IdUuidName> getRelTypeToTaxonIunMap(UUID fromTaxonUuid, UUID taxonRelUuid) {
        Map<String, IdUuidName> relTypeToTaxonIunMap = new HashMap<String, IdUuidName>();
        TransactionStatus tx = app.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(taxonService.load(fromTaxonUuid), Taxon.class);
        for(TaxonRelationship tr : fromTaxon.getRelationsFromThisTaxon()) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                relTypeToTaxonIunMap.put(REL_TYPE_KEY,
                        new IdUuidName(tr.getType().getId(),tr.getType().getUuid(), tr.getType().getTitleCache()));
                relTypeToTaxonIunMap.put(TO_TAXON_KEY,
                        new IdUuidName(tr.getToTaxon().getId(), tr.getToTaxon().getUuid(), tr.getToTaxon().getName().getTitleCache()));
            }
        }
        app.commitTransaction(tx);
        return relTypeToTaxonIunMap;
    }

    public boolean canCreateRelationship(UUID fromTaxonUuid) {
        TransactionStatus tx = app.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(taxonService.load(fromTaxonUuid), Taxon.class);
        boolean canCreateRelationship = true;
        Set<TaxonRelationship> trList = fromTaxon.getRelationsFromThisTaxon();
        for(TaxonRelationship tr : trList) {
            if(tr.getType() != null && tr.getType().equals(TaxonRelationshipType.CONGRUENT_TO())) {
                canCreateRelationship = false;
                break;
            }
        }
        app.commitTransaction(tx);
        return canCreateRelationship;
    }

}
