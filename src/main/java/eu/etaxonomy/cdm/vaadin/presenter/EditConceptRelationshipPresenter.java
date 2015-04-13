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

import com.vaadin.data.util.filter.Compare;

import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;

/**
 * @author cmathew
 * @date 13 Apr 2015
 *
 */
public class EditConceptRelationshipPresenter {

    private CdmSQLContainer taxonRTypeContainer;
    private CdmSQLContainer taxonRContainer;


    public CdmSQLContainer loadTaxonRelationshipTypeContainer() throws SQLException {
        taxonRTypeContainer = CdmSQLContainer.newInstance("DefinedTermBase");
        taxonRTypeContainer.addContainerFilter(new Compare.Equal("DTYPE","TaxonRelationshipType"));
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

}
