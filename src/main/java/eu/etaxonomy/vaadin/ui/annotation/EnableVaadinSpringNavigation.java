/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.vaadin.spring.annotation.SpringViewDisplay;

import eu.etaxonomy.vaadin.ui.config.VaadinSpringNavigatorConfiguration;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManagerBean;

/**
 * Activates automatic navigation through the {@link NavigationManagerBean} based on
 * {@link SpringViewDisplay} annotations. This annotation should be added on a
 * {@link Configuration} class of the application to automatically import
 * {@link VaadinSpringNavigatorConfiguration}.
 *
 * @author a.kohlbecker
 * @since Feb 28, 2017
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(VaadinSpringNavigatorConfiguration.class)
public @interface EnableVaadinSpringNavigation {
}
