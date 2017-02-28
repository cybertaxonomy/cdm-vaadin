/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package com.vaadin.devday.ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.vaadin.devday.ui.navigation.NavigationManagerBean;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.spring.internal.SpringViewDisplayPostProcessor;

/**
 * @author a.kohlbecker
 * @since Feb 28, 2017
 *
 */
@Configuration
public class VaadinSpringNavigatorConfiguration {

    @Bean //TODO see VaadinNavigatorConfiguration
    @UIScope
    public NavigationManagerBean vaadinNavigator() {
        return new NavigationManagerBean();
    }

    @Bean //TODO see VaadinNavigatorConfiguration
    public static SpringViewDisplayPostProcessor springViewDisplayPostProcessor() {
        return new SpringViewDisplayPostProcessor();
    }


}
