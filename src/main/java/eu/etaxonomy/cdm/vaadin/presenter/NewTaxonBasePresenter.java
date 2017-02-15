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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.util.sqlcontainer.RowId;
import com.vaadin.spring.annotation.SpringComponent;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.pager.Pager;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.INonViralName;
import eu.etaxonomy.cdm.model.name.TaxonNameBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.taxon.Classification;
import eu.etaxonomy.cdm.model.taxon.Synonym;
import eu.etaxonomy.cdm.model.taxon.SynonymType;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.model.taxon.TaxonNode;
import eu.etaxonomy.cdm.persistence.query.MatchMode;
import eu.etaxonomy.cdm.strategy.parser.NonViralNameParserImpl;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdUuidName;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
@SpringComponent
@Scope("prototype")
public class NewTaxonBasePresenter implements INewTaxonBaseComponentListener {

    @Autowired
    private CdmRepository cdmRepo = null;

    private final CdmSQLContainer accTaxonSecRefContainer;
    private final CdmSQLContainer synSecRefContainer;


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
    }

    private boolean checkIfNameExists(INonViralName nvn) {
        TaxonNameBase<?,?> name = TaxonNameBase.castAndDeproxy(nvn);
        Pager<TaxonNameBase> names = cdmRepo.getNameService().findByName(name.getClass(),
                name.getNameCache(),
                MatchMode.EXACT,
                null,
                null,
                -1,
                null,
                null);
        if(names.getCount() > 0) {
            return true;
        }
        return false;
    }

    @Override
    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public IdUuidName newTaxon(String scientificName, Object secRefItemId, UUID classificationUuid) {
        NonViralNameParserImpl parser = NonViralNameParserImpl.NewInstance();
        INonViralName name = parser.parseFullName(scientificName);

        if(checkIfNameExists(name)) {
            throw new IllegalArgumentException("Given name already exists");
        }
        TransactionStatus tx = cdmRepo.startTransaction();
        UUID uuid = accTaxonSecRefContainer.getUuid(secRefItemId);
        Reference sec = CdmBase.deproxy(cdmRepo.getReferenceService().load(uuid), Reference.class);

        //name.setTitleCache(scientificName, true);
        Taxon newTaxon = Taxon.NewInstance(name, sec);
        List<String> CLASSIFICATION_INIT_STRATEGY = Arrays.asList(new String []{
                "rootNode.childNodes"
        });
        Classification classification = CdmBase.deproxy(cdmRepo.getClassificationService().load(classificationUuid, CLASSIFICATION_INIT_STRATEGY), Classification.class);
        TaxonNode newTaxonNode = classification.addChildTaxon(newTaxon, null, null);
        cdmRepo.getTaxonNodeService().saveOrUpdate(newTaxonNode);
        newTaxonNode.setUnplaced(true);

        cdmRepo.commitTransaction(tx);
        return new IdUuidName(newTaxonNode.getTaxon().getId(), newTaxonNode.getTaxon().getUuid(), newTaxonNode.getTaxon().getTitleCache());
    }

    @Override
    //@Transactional // FIXME use this annotation instead of the explicit start commit below
    public IdUuidName newSynonym(String scientificName, Object synSecRefItemId, Object accTaxonSecRefItemId, UUID accTaxonUuid) {
        NonViralNameParserImpl parser = NonViralNameParserImpl.NewInstance();
        INonViralName name = parser.parseFullName(scientificName);

        if(checkIfNameExists(name)) {
            throw new IllegalArgumentException("Given name already exists");
        }
        TransactionStatus tx = cdmRepo.startTransaction();
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "synonymRelations"
        });

        UUID synRefUuid = synSecRefContainer.getUuid(synSecRefItemId);
        Reference synSec = CdmBase.deproxy(cdmRepo.getReferenceService().load(synRefUuid), Reference.class);

        //name.setTitleCache(scientificName, true);
        Synonym newSynonym = Synonym.NewInstance(name, synSec);


        UUID accTaxonRefUuid = accTaxonSecRefContainer.getUuid(accTaxonSecRefItemId);
        Reference accTaxonSec = CdmBase.deproxy(cdmRepo.getReferenceService().load(accTaxonRefUuid), Reference.class);
        Taxon accTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(accTaxonUuid, ACC_TAXON_INIT_STRATEGY), Taxon.class);
        accTaxon.setSec(accTaxonSec);

        accTaxon.addSynonym(newSynonym, SynonymType.SYNONYM_OF());

        UUID newUuid = cdmRepo.getTaxonService().save(newSynonym).getUuid();
        cdmRepo.commitTransaction(tx);
        return new IdUuidName(newSynonym.getId(), newUuid, newSynonym.getTitleCache());
    }


    @Override
    public Object getAcceptedTaxonRefId(UUID accTaxonUuid) {
        Taxon accTaxon = CdmBase.deproxy(cdmRepo.getTaxonService().load(accTaxonUuid), Taxon.class);
        if(accTaxon.getSec() != null) {
            int refId = accTaxon.getSec().getId();
            RowId itemId = new RowId(refId);
            return itemId;
        }
        return null;
    }

    @Override
    public Object getClassificationRefId(UUID classificationUuid) {
        Classification classification = CdmBase.deproxy(cdmRepo.getClassificationService().load(classificationUuid), Classification.class);
        if(classification.getReference() != null) {
            int refId = classification.getReference().getId();
            RowId itemId = new RowId(refId);
            return itemId;
        }
        return null;
    }



}
