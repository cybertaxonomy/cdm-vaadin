/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 * A {@link SpringViewProvider.setAccessDeniedViewClass(Class<? extends View> accessDeniedViewClass) accessDeniedViewClass}
 * can not be a view- or UI-scoped bean, since the view context is not set up at the time when the <code>accessDeniedViewClass</code>
 * is needed. Therefore the <code>RedirectToLoginView</code> is a scoped prototype bean which always available. Using the 'singleton'
 * scope should also work but is not a good idea for a login view.
 * The <code>RedirectToLoginView</code> redirects the request to the LoginView which then is in a correctly
 * set up view scope.

 *
 * @author a.kohlbecker
 * @since Jul 13, 2017
 *
 */
@SpringComponent
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RedirectToLoginView extends VerticalLayout implements View {


    private static final long serialVersionUID = -8763747518841365925L;

    @Autowired
    NavigationManager navigationManager;

    @Autowired
    protected ApplicationEventPublisher eventBus;

    public RedirectToLoginView() {

        this.setWidth("100%");
        Label header = new Label("Access denied");
        header.setStyleName(ValoTheme.BUTTON_LARGE);
        header.setWidthUndefined();

        addComponent(header);
        setComponentAlignment(header, Alignment.MIDDLE_CENTER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter(ViewChangeEvent event) {

        String currentState = ((Navigator)navigationManager).getState();
        // redirect to the login view and pass over the current state
        eventBus.publishEvent(new NavigationEvent(LoginViewBean.NAME, currentState));
    }

}
