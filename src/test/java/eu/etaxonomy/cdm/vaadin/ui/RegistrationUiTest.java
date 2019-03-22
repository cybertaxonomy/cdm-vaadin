/**
* Copyright (C) 2019 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.ui;

import static com.codeborne.selenide.Selenide.open;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import eu.etaxonomy.cdm.SelenideTestBase;

/**
 * @author a.kohlbecker
 * @since Mar 21, 2019
 *
 */
@Ignore
public class RegistrationUiTest extends SelenideTestBase {

    @Test
    public void test()  {
        open("http://localhost:"+ localServerPort() + "/app/registration");
        Assert.assertEquals("Registration", driver.getTitle());
    }


}
