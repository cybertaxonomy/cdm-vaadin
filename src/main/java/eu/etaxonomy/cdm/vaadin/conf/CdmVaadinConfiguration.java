/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.conf;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;

import com.vaadin.spring.annotation.EnableVaadin;

/**
 *
 * @author a.kohlbecker
 * @since Feb 8, 2017
 *
 */
@Configuration
@EnableVaadin   // this imports VaadinConfiguration
public class CdmVaadinConfiguration {


    public static final Logger logger = Logger.getLogger(CdmVaadinConfiguration.class);

    public CdmVaadinConfiguration() {
        logger.debug("CdmVaadinConfiguration enabled");
    }

}
