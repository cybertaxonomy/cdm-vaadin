/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin;

import com.github.springtestdbunit.annotation.DbUnitConfiguration;

/**
 * @author a.kohlbecker
 * @since Nov 21, 2017
 *
 */
@VaadinSpringIntegrationTest
@DbUnitConfiguration(dataSetLoaderBean="dataSetLoader")
public class SpringVaadinMvpTest {

}
