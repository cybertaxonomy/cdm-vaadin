/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm;

import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.unitils.AlternativeUnitilsJUnit4TestClassRunner;
import org.unitils.database.annotations.Transactional;
import org.unitils.database.util.TransactionMode;
import org.unitils.spring.annotation.SpringApplicationContext;
import org.unitils.spring.annotation.SpringBeanByType;

import eu.etaxonomy.cdm.test.integration.CdmIntegrationTest;

/**
 * Base test class for pure service layer tests.
 *
 * The application context will not contain any Webservice application context!
 *
 * @author a.kohlbecker
 * @since Nov 24, 2017
 *
 */
@RunWith(AlternativeUnitilsJUnit4TestClassRunner.class)
@SpringApplicationContext("file:./src/test/resources/eu/etaxonomy/cdm/applicationContext-service-integration.xml")
@Transactional(TransactionMode.DISABLED)
public abstract class CdmVaadinIntegrationTest extends CdmIntegrationTest {

    @SpringBeanByType
    protected SessionFactory sessionFactory;


}
