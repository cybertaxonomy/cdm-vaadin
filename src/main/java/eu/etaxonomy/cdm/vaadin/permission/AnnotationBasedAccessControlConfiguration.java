/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vaadin.spring.annotation.UIScope;

/**
 * @author a.kohlbecker
 * @since Apr 24, 2017
 */
@Configuration
public class AnnotationBasedAccessControlConfiguration {

    @Bean
    @UIScope
    public AnnotationBasedAccessControlBean annotationBasedAccessControlBean() {
        return new AnnotationBasedAccessControlBean();
    }

    @Bean
    @UIScope // TODO move into own @Configuration class?
    public AccessRestrictedViewControlBean accessRestrictedViewControlBean() {
        return new AccessRestrictedViewControlBean();
    }

}
