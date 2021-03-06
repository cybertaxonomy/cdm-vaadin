package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import eu.etaxonomy.cdm.test.unitils.CleanSweepInsertLoadStrategy;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;


@DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class, value="CdmSQLContainerTest.xml")
public class CdmSQLContainerTest extends CdmVaadinBaseTest {

	@SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(CdmSQLContainerTest.class);


	@Test
	@DataSet(loadStrategy=CleanSweepInsertLoadStrategy.class, value="CdmSQLContainerTest.xml")
	public void testTaxonContainer() throws SQLException {

		CdmSQLContainer csc = CdmSQLContainer.newInstance("TaxonBase");
		Collection<?> propIds = csc.getContainerPropertyIds();
		Collection<?> itemIds = csc.getItemIds();
		Assert.assertEquals(38, itemIds.size());
	}



}
