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
import org.unitils.dbunit.annotation.DataSets;

import com.vaadin.data.util.sqlcontainer.RowId;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Synonym;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.test.unitils.CleanSweepInsertLoadStrategy;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.component.taxon.INewTaxonBaseComponentListener;
import eu.etaxonomy.cdm.vaadin.component.taxon.INewTaxonBaseComposite;
import eu.etaxonomy.cdm.vaadin.component.taxon.NewTaxonBasePresenter;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;

/**
 * @author cmathew
 * @since 2 Apr 2015
 */
@DataSets({
    @DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class),
    @DataSet("/eu/etaxonomy/cdm/database/FirstData_UsersAndPermissions.xml")
})
public class NewTaxonBasePresenterTest extends CdmVaadinBaseTest {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(NewTaxonBasePresenterTest.class);

    private static NewTaxonBasePresenter ntbp;

    @BeforeClass
    public static void init() throws SQLException {
        ntbp = new NewTaxonBasePresenter();
    }

    @Test
    public void testNewTaxonBase(){
        RowId refId20 = new RowId(20);
        RowId refId21 = new RowId(21);
        UUID newTaxonUuid = ntbp.newTaxon("Taxon  h", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "sec",
                "synonyms"
        });
        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);

        UUID newSynonymUuid = ntbp.newSynonym("Synonym ofe", refId20, refId21, newTaxonUuid).getUuid();
        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);

        Set<Synonym> synonyms = taxon.getSynonyms();
        Assert.assertEquals(1,synonyms.size());
        Synonym synonymOfTaxon = synonyms.iterator().next();

        Synonym synonym = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newSynonymUuid),Synonym.class);
        Assert.assertEquals(synonym, synonymOfTaxon);

        Assert.assertEquals(synonym.getSec().getId(), 20);

        taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);

        Assert.assertEquals(taxon.getSec().getId(), 21);
    }

    @Test
    public void testNewTaxonBaseWhenNameAlreadyExists() {
        RowId refId20 = new RowId(20);
        // test taxa
        try {
            ntbp.newTaxon("Taxon e", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }

        try {
            ntbp.newTaxon("Taxon  e", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }
        try{
            ntbp.newTaxon("Taxon e Me.", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }

        // test synonym
        RowId refId21 = new RowId(21);
        UUID newTaxonUuid = ntbp.newTaxon("Taxon  h", refId20, UUID.fromString("6595638e-4993-421a-9fe5-76b09d94f36a")).getUuid();
        List<String> ACC_TAXON_INIT_STRATEGY = Arrays.asList(new String []{
                "sec",
                "synonymRelations"
        });
        Taxon taxon = CdmBase.deproxy(CdmSpringContextHelper.getTaxonService().load(newTaxonUuid,ACC_TAXON_INIT_STRATEGY),Taxon.class);
        try {
            ntbp.newSynonym("Htsynonym bofa", refId20, refId21, newTaxonUuid);
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }

        try {
            ntbp.newSynonym("Htsynonym  bofa", refId20, refId21, newTaxonUuid);
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }
        try{
            ntbp.newSynonym("Htsynonym bofa Me.", refId20, refId21, newTaxonUuid);
            Assert.fail("Exception should be thrown as name already exists");;
        } catch (IllegalArgumentException iae) {

        }
    }

    public static class MockNewTaxonBaseComposite implements INewTaxonBaseComposite {

        @Override
        public void setListener(INewTaxonBaseComponentListener listener) {
            // TODO Auto-generated method stub
        }
    }
}
