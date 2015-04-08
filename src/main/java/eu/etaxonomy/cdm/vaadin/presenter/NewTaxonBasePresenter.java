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

import eu.etaxonomy.cdm.api.service.INameService;
import eu.etaxonomy.cdm.api.service.IReferenceService;
import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.name.NonViralName;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.model.taxon.Synonym;
import eu.etaxonomy.cdm.model.taxon.SynonymRelationshipType;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.IdAndUuid;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */
public class NewTaxonBasePresenter implements INewTaxonBaseComponentListener {



    private final CdmSQLContainer secRefContainer;

    private final IReferenceService referenceService;
    private final ITaxonService taxonService;
    private final INameService nameService;



    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener#getSecRefContainer()
     */
    @Override
    public CdmSQLContainer getSecRefContainer() {
        return secRefContainer;
    }

    public NewTaxonBasePresenter() throws SQLException {

        secRefContainer = CdmSQLContainer.newInstance("Reference");
        referenceService = CdmSpringContextHelper.getReferenceService();
        taxonService = CdmSpringContextHelper.getTaxonService();
        nameService = CdmSpringContextHelper.getNameService();


    }


    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener#newTaxon(java.lang.String, java.lang.Object)
     */
    @Override
    public IdAndUuid newTaxon(String scientificName, Object secRefItemId) {
        UUID uuid = secRefContainer.getUuid(secRefItemId);
        Reference sec = CdmBase.deproxy(referenceService.load(uuid), Reference.class);
        NonViralName name = NonViralName.NewInstance(null);
        name.setTitleCache(scientificName, true);
        nameService.save(name);
        Taxon newTaxon = Taxon.NewInstance(name, sec);
        // TODO : add new TaxonNode since we want to have it show up
        // in the table
        UUID newUuid = taxonService.save(newTaxon);
        return new IdAndUuid(newTaxon.getId(), newUuid);
    }

    /* (non-Javadoc)
     * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener#newSynonym(java.lang.String, java.lang.Object, java.util.UUID)
     */
    @Override
    public IdAndUuid newSynonym(String scientificName, Object secRefItemId, UUID accTaxonUuid) {
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "synonymRelations"
        });

        UUID refUuid = secRefContainer.getUuid(secRefItemId);
        Reference sec = CdmBase.deproxy(referenceService.load(refUuid), Reference.class);
        NonViralName name = NonViralName.NewInstance(null);
        name.setTitleCache(scientificName, true);
        Taxon accTaxon = CdmBase.deproxy(taxonService.load(accTaxonUuid, ACC_TAXON_INIT_STRATEGY), Taxon.class);
        Synonym newSynonym = Synonym.NewInstance(name, sec);
        accTaxon.addSynonym(newSynonym, SynonymRelationshipType.SYNONYM_OF());
        UUID newUuid = taxonService.save(newSynonym);
        return new IdAndUuid(newSynonym.getId(), newUuid);
    }


}
