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
import java.util.Set;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Synonym;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener;
import eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComposite;

/**
 * @author cmathew
 * @date 2 Apr 2015
 *
 */

@DataSet("StatusPresenterTest.xml")
public class NewTaxonBasePresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(NewTaxonBasePresenterTest.class);

    private static NewTaxonBasePresenter ntbp;

    @BeforeClass
    public static void init() throws SQLException {
        ntbp = new NewTaxonBasePresenter();
    }


    @Test
    public void testNewTaxonBase() throws SQLException {
        RowId refId20 = new RowId(20);
        UUID newTaxonUuid = ntbp.newTaxon("Taxon E", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "synonymRelations"
        });
        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);

        UUID newSynonymUuid = ntbp.newSynonym("Synonym OfE", refId20, newTaxonUuid).getUuid();
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);

        Set<Synonym> synonyms = taxon.getSynonyms();
        Assert.assertEquals(1,synonyms.size());
        Synonym synonymOfTaxon = synonyms.iterator().next();

        Synonym synonym = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newSynonymUuid),Synonym.class);
        Assert.assertEquals(synonym, synonymOfTaxon);

        CdmSQLContainer csc = CdmSQLContainer.newInstance("TaxonBase");
    }


    public static class MockNewTaxonBaseComposite implements INewTaxonBaseComposite {

        /* (non-Javadoc)
         * @see eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComposite#setListener(eu.etaxonomy.cdm.vaadin.view.INewTaxonBaseComponentListener)
         */
        @Override
        public void setListener(INewTaxonBaseComponentListener listener) {
            // TODO Auto-generated method stub

        }



    }
}
