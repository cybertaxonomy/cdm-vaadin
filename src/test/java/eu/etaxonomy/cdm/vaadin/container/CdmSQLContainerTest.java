package eu.etaxonomy.cdm.vaadin.container;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import com.vaadin.data.Item;

import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;
import eu.etaxonomy.cdm.vaadin.util.CdmSpringContextHelper;


@DataSet
public class CdmSQLContainerTest extends CdmVaadinBaseTest {	
	
	private static final Logger logger = Logger.getLogger(CdmSQLContainerTest.class);
	
	@Test	
	public void testTaxonContainer() {
		CdmSpringContextHelper helper = CdmSpringContextHelper.newInstance();
		// FIXME : Need to figure out how to get the db connection username / password
		//         to initialise the container. Hard coded for now
		CdmSQLContainer csc = CdmSQLContainer.newInstance("TaxonBase", "sa","");		
		Collection<?> propIds = csc.getContainerPropertyIds();
		Collection<?> itemIds = csc.getItemIds();
		for(Object itemId : itemIds) {
			Item item = csc.getItem(itemId);
			// column names need to be uppercase for h2 in the test environment
			String uuid = (String)item.getItemProperty("UUID").getValue();		
		}
		Assert.assertEquals(itemIds.size(),38);
	}


}
