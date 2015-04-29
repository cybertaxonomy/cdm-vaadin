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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.api.application.ICdmApplicationConfiguration;
import eu.etaxonomy.cdm.api.service.IClassificationService;
import eu.etaxonomy.cdm.api.service.IReferenceService;
import eu.etaxonomy.cdm.api.service.ITaxonNodeService;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.NonViralName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.Synonym;
import eu.etaxonomy.cdm.model.taxon.SynonymRelationshipType;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.strategy.parser.NonViralNameParserImpl;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
public class NewTaxonBasePresenter implements INewTaxonBaseComponentListener {



    private final CdmSQLContainer accTaxonSecRefContainer;
    private final CdmSQLContainer synSecRefContainer;

    private final IReferenceService referenceService;
    private final ITaxonNodeService taxonNodeService;
    private final ITaxonService taxonService;
    private final IClassificationService classificationService;
    private final ICdmApplicationConfiguration app;



    @Override
    public CdmSQLContainer getAccTaxonSecRefContainer() {
        return accTaxonSecRefContainer;
    }

    @Override
    public CdmSQLContainer getSynSecRefContainer() {
        return synSecRefContainer;
    }

    public NewTaxonBasePresenter() throws SQLException {
        accTaxonSecRefContainer = CdmSQLContainer.newInstance("Reference");
        synSecRefContainer = CdmSQLContainer.newInstance("Reference");
        referenceService = CdmSpringContextHelper.getReferenceService();
        taxonNodeService = CdmSpringContextHelper.getTaxonNodeService();
        taxonService = CdmSpringContextHelper.getTaxonService();
        classificationService = CdmSpringContextHelper.getClassificationService();
        app = CdmSpringContextHelper.getApplicationConfiguration();
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener#newTaxon(java.lang.String, java.lang.Object, java.util.UUID)
     */
    @Override
    public IdUuidName newTaxon(String scientificName, Object secRefItemId, UUID classificationUuid) {
        TransactionStatus tx = app.startTransaction();
        UUID uuid = accTaxonSecRefContainer.getUuid(secRefItemId);

        Reference sec = CdmBase.deproxy(referenceService.load(uuid), Reference.class);

        NonViralNameParserImpl parser = NonViralNameParserImpl.NewInstance();
        NonViralName name = parser.parseFullName(scientificName);
        name.setTitleCache(scientificName, true);
        Taxon newTaxon = Taxon.NewInstance(name, sec);
        newTaxon.setUnplaced(true);
        List<String> CLASSIFICATION_INIT_STRATEGY = Arrays.asList(new String []{
                "rootNode.childNodes"
        });
        Classification classification = CdmBase.deproxy(classificationService.load(classificationUuid, CLASSIFICATION_INIT_STRATEGY), Classification.class);
        TaxonNode newTaxonNode = classification.addChildTaxon(newTaxon, null, null);
        UUID newUuid = taxonNodeService.saveOrUpdate(newTaxonNode);

        app.commitTransaction(tx);
        return new IdUuidName(newTaxonNode.getTaxon().getId(), newTaxonNode.getTaxon().getUuid(), newTaxonNode.getTaxon().getTitleCache());
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener#newSynonym(java.lang.String, java.lang.Object, java.util.UUID)
     */
    @Override
    public IdUuidName newSynonym(String scientificName, Object synSecRefItemId, Object accTaxonSecRefItemId, UUID accTaxonUuid) {
        TransactionStatus tx = app.startTransaction();
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "synonymRelations"
        });

        UUID synRefUuid = synSecRefContainer.getUuid(synSecRefItemId);
        Reference synSec = CdmBase.deproxy(referenceService.load(synRefUuid), Reference.class);
        NonViralNameParserImpl parser = NonViralNameParserImpl.NewInstance();
        NonViralName name = parser.parseFullName(scientificName);
        name.setTitleCache(scientificName, true);
        Synonym newSynonym = Synonym.NewInstance(name, synSec);


        UUID accTaxonRefUuid = accTaxonSecRefContainer.getUuid(accTaxonSecRefItemId);
        Reference accTaxonSec = CdmBase.deproxy(referenceService.load(accTaxonRefUuid), Reference.class);
        Taxon accTaxon = CdmBase.deproxy(taxonService.load(accTaxonUuid, ACC_TAXON_INIT_STRATEGY), Taxon.class);
        accTaxon.setSec(accTaxonSec);

        accTaxon.addSynonym(newSynonym, SynonymRelationshipType.SYNONYM_OF());

        UUID newUuid = taxonService.save(newSynonym);
        app.commitTransaction(tx);
        return new IdUuidName(newSynonym.getId(), newUuid, newSynonym.getTitleCache());
    }


    @Override
    public Object getAcceptedTaxonRefId(UUID accTaxonUuid) {
        Taxon accTaxon = CdmBase.deproxy(taxonService.load(accTaxonUuid), Taxon.class);
        if(accTaxon.getSec() != null) {
            int refId = accTaxon.getSec().getId();
            RowId itemId = new RowId(refId);
            return itemId;
        }
        return null;
    }

    @Override
    public Object getClassificationRefId(UUID classificationUuid) {
        Classification classification = CdmBase.deproxy(classificationService.load(classificationUuid), Classification.class);
        if(classification.getReference() != null) {
            int refId = classification.getReference().getId();
            RowId itemId = new RowId(refId);
            return itemId;
        }
        return null;
    }



}
