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

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.json.JSONException;

import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.jscomponent.D3ConceptRelationshipTree;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.IConceptRelationshipComponentListener;

/**
 * @author cmathew
 * @date 9 Apr 2015
 *
 */
public class ConceptRelationshipPresenter implements IConceptRelationshipComponentListener {

    private final ITaxonService taxonService;
    private final D3ConceptRelationshipTree crTree;

    public ConceptRelationshipPresenter(D3ConceptRelationshipTree crTree) {
        taxonService = CdmSpringContextHelper.getTaxonService();
        this.crTree = crTree;
    }

    @Override
    public void updateConceptRelationship(UUID taxonUuid) throws JSONException {
        List<String> FROM_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "relationsFromThisTaxon"
        });

        Taxon taxon = CdmBase.deproxy(taxonService.load(taxonUuid, FROM_TAXON_INIT_STRATEGY), Taxon.class);
        crTree.updateConceptRelationshipTree(taxon);
    }

}
