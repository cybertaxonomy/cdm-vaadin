/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.presenter;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationship;
import eu.etaxonomy.cdm.model.taxon.TaxonRelationshipType;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;

/**
 * @author cmathew
 * @date 13 Apr 2015
 *
 */
@SpringComponent
@Scope("prototype")
public class EditConceptRelationshipPresenter {

    @Autowired
    private CdmRepository cdmRepo = null;

    private CdmSQLContainer taxonRTypeContainer;
    private CdmSQLContainer taxonRContainer;

    public final static String REL_TYPE_KEY = "relTypeIun";
    public final static String TO_TAXON_KEY = "toTaxonIun";

    public EditConceptRelationshipPresenter() {
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

    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public IdUuidName createRelationship(UUID fromTaxonUuid, UUID relTypeUuid, UUID toTaxonUuid) {
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(fromTaxonUuid), Taxon.class);
        Taxon toTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(toTaxonUuid), Taxon.class);
        TaxonRelationshipType relType = CdmBase.deproxy(cdmRepo.getTermService().load(relTypeUuid), TaxonRelationshipType.class);
        TaxonRelationship tr = fromTaxon.addTaxonRelation(toTaxon, relType, null, null);
        cdmRepo.commitTransaction(tx);
        return new IdUuidName(tr.getId(), tr.getUuid(), tr.getType().getTitleCache());
    }

    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public void updateRelationship(UUID fromTaxonUuid, UUID taxonRelUuid, UUID newRelTypeUuid , UUID newToTaxonUuid) {
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(fromTaxonUuid), Taxon.class);
        for(TaxonRelationship tr : fromTaxon.getRelationsFromThisTaxon()) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                if(newRelTypeUuid != null) {
                    TaxonRelationshipType relType = CdmBase.deproxy(cdmRepo.getTermService().load(newRelTypeUuid), TaxonRelationshipType.class);
                    tr.setType(relType);
                }
                if(newToTaxonUuid != null) {
                    Taxon toTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(newToTaxonUuid), Taxon.class);
                    tr.setToTaxon(toTaxon);
                }
            }
        }
        cdmRepo.commitTransaction(tx);
    }

    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public void deleteRelationship(UUID fromTaxonUuid, UUID taxonRelUuid) {
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(fromTaxonUuid), Taxon.class);
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
        cdmRepo.commitTransaction(tx);
    }


    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public Map<String, IdUuidName> getRelTypeToTaxonIunMap(UUID fromTaxonUuid, UUID taxonRelUuid) {
        Map<String, IdUuidName> relTypeToTaxonIunMap = new HashMap<String, IdUuidName>();
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(fromTaxonUuid), Taxon.class);
        for(TaxonRelationship tr : fromTaxon.getRelationsFromThisTaxon()) {
            if(tr.getUuid().equals(taxonRelUuid)) {
                relTypeToTaxonIunMap.put(REL_TYPE_KEY,
                        new IdUuidName(tr.getType().getId(),tr.getType().getUuid(), tr.getType().getTitleCache()));
                relTypeToTaxonIunMap.put(TO_TAXON_KEY,
                        new IdUuidName(tr.getToTaxon().getId(), tr.getToTaxon().getUuid(), tr.getToTaxon().getName().getTitleCache()));
            }
        }
        cdmRepo.commitTransaction(tx);
        return relTypeToTaxonIunMap;
    }

    public boolean canCreateRelationship(UUID fromTaxonUuid) {
        TransactionStatus tx = cdmRepo.startTransaction();
        Taxon fromTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(fromTaxonUuid), Taxon.class);
        boolean canCreateRelationship = true;
        Set<TaxonRelationship> trList = fromTaxon.getRelationsFromThisTaxon();
        for(TaxonRelationship tr : trList) {
            if(tr.getType() != null && tr.getType().equals(TaxonRelationshipType.CONGRUENT_TO())) {
                canCreateRelationship = false;
                break;
            }
        }
        cdmRepo.commitTransaction(tx);
        return canCreateRelationship;
    }

}
