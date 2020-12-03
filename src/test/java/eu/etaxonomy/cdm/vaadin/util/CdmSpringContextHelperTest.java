/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.unitils.dbunit.annotation.DataSet;

import eu.etaxonomy.cdm.api.service.ITaxonService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.taxon.Taxon;
import eu.etaxonomy.cdm.vaadin.CdmVaadinBaseTest;

@DataSet
public class CdmSpringContextHelperTest extends CdmVaadinBaseTest {

	@Test
	public void testTaxonService() {

		ITaxonService taxonService = CdmSpringContextHelper.getTaxonService();
		Taxon taxon1 = CdmBase.deproxy(taxonService.find(UUID.fromString("54e767ee-894e-4540-a758-f906ecb4e2d9")),Taxon.class);
		Assert.assertEquals(taxon1.getTitleCache(), "Sphingidae Linnaeus, 1758 sec. cate-sphingidae.org");

		Taxon taxon2 = CdmBase.deproxy(taxonService.find(UUID.fromString("b989a278-c414-49f7-9a10-7d784700e4c4")),Taxon.class);
		Assert.assertEquals(taxon2.getTitleCache(), "Manduca Hubner, 1807 sec. cate-sphingidae.org");
	}
}