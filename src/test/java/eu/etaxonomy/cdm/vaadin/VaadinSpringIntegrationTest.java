/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.vaadin.spring.test.annotation.VaadinAppConfiguration;

import eu.etaxonomy.cdm.addon.config.CdmVaadinConfiguration;

/**
 * @author a.kohlbecker
 * @since Nov 20, 2017
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration("file:./src/test/resources/webapp/WEB-INF/applicationContext.xml")
@TestPropertySource(properties = {
        CdmVaadinConfiguration.CDM_VAADIN_UI_ACTIVATED + "=concept,distribution,editstatus,registration" // enable all UIs
        })
@VaadinAppConfiguration
public @interface VaadinSpringIntegrationTest {

}
