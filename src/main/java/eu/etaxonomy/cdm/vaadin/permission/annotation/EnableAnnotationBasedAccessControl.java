/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.permission.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import eu.etaxonomy.cdm.vaadin.permission.AnnotationBasedAccessControlConfiguration;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(AnnotationBasedAccessControlConfiguration.class)
/**
 * @author a.kohlbecker
 * @since Apr 24, 2017
 *
 */
public @interface EnableAnnotationBasedAccessControl {

}
