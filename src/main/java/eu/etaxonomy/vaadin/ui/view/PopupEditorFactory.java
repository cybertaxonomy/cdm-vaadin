/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.ui.view;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.vaadin.component.SelectFieldFactory;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractEditorPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationManager;

/**
 * @author a.kohlbecker
 * @since May 30, 2017
 *
 */
@SpringComponent
@UIScope
public class PopupEditorFactory {


    @Autowired
    protected ApplicationEventPublisher eventBus;

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    @Autowired
    private SelectFieldFactory selectFieldFactory;

    @Autowired
    @Lazy
    private NavigationManager navigationManager;

    private Field presenterRepoField;
    private Field presenterNavigationManagerField;
    private Field presenterEventBusField;

    private Field viewEventBusField;
    private Method viewInjectPresenterMethod;

    private Method viewInitMethod;

    private Field selectFieldFactoryField;

    public PopupEditorFactory(){
        initFieldsAccess();
    }


    /**
     *
     */
    private void initFieldsAccess() {

        try {
            presenterRepoField = AbstractPresenter.class.getDeclaredField("repo");
            presenterRepoField.setAccessible(true);

            presenterNavigationManagerField = AbstractPresenter.class.getDeclaredField("navigationManager");
            presenterNavigationManagerField.setAccessible(true);

            presenterEventBusField = AbstractEditorPresenter.class.getDeclaredField("eventBus");
            presenterEventBusField.setAccessible(true);

            viewEventBusField = AbstractView.class.getDeclaredField("eventBus");
            viewEventBusField.setAccessible(true);

            viewInjectPresenterMethod = AbstractView.class.getDeclaredMethod("injectPresenter", AbstractPresenter.class);
            viewInjectPresenterMethod.setAccessible(true);

            viewInitMethod = AbstractView.class.getDeclaredMethod("init");
            viewInitMethod.setAccessible(true);

            selectFieldFactoryField = AbstractCdmPopupEditor.class.getDeclaredField("selectFieldFactory");
            selectFieldFactoryField.setAccessible(true);

        } catch (NoSuchFieldException | SecurityException | NoSuchMethodException  e) {
            throw new RuntimeException("Severe error during initialization. Please check the classes AbstractPresenter, AbstractEditorPresenter, AbstractView for modificactions.", e);
        }

    }


    /**
     * @param popupViewClass
     * @return
     */
    public <V extends PopupView, P extends AbstractPresenter> PopupView newPopupView(Class<V> popupViewClass) {

        Class<? extends AbstractPresenter<?>> prestenterClass = findGenericPresenterType(popupViewClass);
        try {

            P presenter = (P) prestenterClass.newInstance();

            presenterRepoField.set(presenter, repo);
            presenterNavigationManagerField.set(presenter, navigationManager);

            if(AbstractEditorPresenter.class.isAssignableFrom(prestenterClass)){
                presenterEventBusField.set(presenter, eventBus);
            }

            PopupView view = popupViewClass.newInstance();
            if(AbstractView.class.isAssignableFrom(popupViewClass)){
                AbstractView<P> abstractView = (AbstractView<P>)view;
                viewEventBusField.set(abstractView, eventBus);
                viewInjectPresenterMethod.invoke(abstractView, presenter);
                // invoke the @PostConstruct method

                if(AbstractCdmPopupEditor.class.isAssignableFrom(popupViewClass)){
                    selectFieldFactoryField.set(view, selectFieldFactory);
                }

                viewInitMethod.invoke(abstractView);
            }
            return view;
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(String.format("Error creating the view class '%s' with presenter class '%s'", popupViewClass, prestenterClass), e);
        }
    }

    /**
     * @param popupViewClass
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<? extends AbstractPresenter<?>> findGenericPresenterType(Class<?  extends PopupView> popupViewClass) {

        ParameterizedType genericSuperClass = (ParameterizedType)popupViewClass.getGenericSuperclass();
        Type[] typeArgs = genericSuperClass.getActualTypeArguments();
        if(AbstractPopupEditor.class.isAssignableFrom(popupViewClass)){
           return (Class<? extends AbstractPresenter<?>>) typeArgs[1];
        } else {
           return (Class<? extends AbstractPresenter<?>>) typeArgs[0];
        }
    }


}
