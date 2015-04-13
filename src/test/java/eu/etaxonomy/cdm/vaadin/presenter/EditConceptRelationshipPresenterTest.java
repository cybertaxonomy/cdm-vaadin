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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;

/**
 * @author cmathew
 * @date 13 Apr 2015
 *
 */
@DataSet("ConceptRelationshipPresenterTest.xml")
public class EditConceptRelationshipPresenterTest extends CdmVaadinBaseTest {

    private static final Logger logger = Logger.getLogger(EditConceptRelationshipPresenterTest.class);
    public static EditConceptRelationshipPresenter ecrp;


    @BeforeClass
    public static void init() {
        ecrp = new EditConceptRelationshipPresenter();
    }

    @Test
    public void testLoadTaxonRelationshipTypeContainer() throws SQLException {
        CdmSQLContainer container = ecrp.loadTaxonRelationshipTypeContainer();
        Assert.assertEquals(27, container.size());
    }

    @Test
    public void testLoadTaxonRelationshipContainer() throws SQLException {
        CdmSQLContainer container = ecrp.loadTaxonRelationshipContainer(30);
        Assert.assertEquals(3, container.size());
    }
}
