/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.debug;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.vaadin.spring.events.Event;
import org.vaadin.spring.events.EventBus;
import org.vaadin.spring.events.EventBus.UIEventBus;
import org.vaadin.spring.events.EventBusListener;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractCdmPopupEditor;
import eu.etaxonomy.vaadin.mvp.AbstractPopupView;
import eu.etaxonomy.vaadin.mvp.AbstractPresenter;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.view.PopEditorOpenedEvent;
import eu.etaxonomy.vaadin.ui.view.PopupView;

/**
 * @author a.kohlbecker
 * @since Jan 22, 2018
 */
@Component
@UIScope
@Profile("debug")
public class EntityCacheDebugger implements ViewChangeListener, EventBusListener<PopEditorOpenedEvent> {

    private final static Logger logger = LogManager.getLogger();

    private UIEventBus uiEventBus;


    @Autowired
    protected final void setUIEventBus(EventBus.UIEventBus uiEventBus){
        this.uiEventBus = uiEventBus;
        uiEventBus.subscribe(this);
    }

    EntityCacheDebuggerShortcutListener shortcutListener;

    public EntityCacheDebugger(){
        shortcutListener = new EntityCacheDebuggerShortcutListener("Debug Entities",
                ShortcutAction.KeyCode.SPACEBAR,
                ShortcutAction.ModifierKey.CTRL);
    }

    public void openFor(AbstractView view){

        if(view != null){

                try {
                    AbstractPresenter<?> presenter;
                    Method getPresenterMethod = AbstractView.class.getDeclaredMethod("getPresenter");
                    getPresenterMethod.setAccessible(true);
                    presenter = (AbstractPresenter<?>) getPresenterMethod.invoke(view);
                    if(CachingPresenter.class.isAssignableFrom(presenter.getClass())){
                        open(view, (CachingPresenter)presenter);
                    } else {
                        logger.warn("can only operate on CachingPresenters");
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                        | InvocationTargetException e) {
                    logger.error(e);
                }

        } else {
            logger.warn("view is null");
        }

   }

    private void open(AbstractView view, CachingPresenter presenter) {

        EntityCacheDebuggerComponent content = new EntityCacheDebuggerComponent(presenter);

        if(view instanceof AbstractCdmPopupEditor){
            findWindow((AbstractCdmPopupEditor)view).setModal(false);
        }
        Window window = new Window();
        window.setCaption("Entity Cache Debugger");
        window.setResizable(true);
        window.setModal(false);
        content.setSizeFull();
        window.setContent(content);
        window.setWidth("800px");
        window.setHeight("600px");
        UI.getCurrent().addWindow(window);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void afterViewChange(ViewChangeEvent event) {
        View newView = event.getNewView();
        if(newView instanceof AbstractView){
            ((AbstractView)newView).addShortcutListener(shortcutListener);
        }
        if(event.getOldView() instanceof AbstractView){
            ((AbstractView)event.getOldView()).removeShortcutListener(shortcutListener);
        }
    }

    @Override
    public void onEvent(Event<PopEditorOpenedEvent> event){
        PopupView popupView = event.getPayload().getPopupView();
        if(popupView != null && popupView instanceof AbstractPopupView){
            findWindow((AbstractPopupView)popupView).addShortcutListener(shortcutListener);
        }

    }

    private Window findWindow(AbstractPopupView view){
        Optional<Window> popUpWindow = UI.getCurrent().getWindows().stream().filter(w -> w.getContent().equals(view)).findFirst();
        if(popUpWindow.isPresent()){
            return popUpWindow.get();
        } else {
            return null;
        }

    }

    /**
     * @return the shortcutListener
     */
    public EntityCacheDebuggerShortcutListener getShortcutListener() {
        return shortcutListener;
    }


    private class EntityCacheDebuggerShortcutListener extends ShortcutListener {

            private static final long serialVersionUID = -8727949764189908851L;

            /**
             * @param caption
             * @param keyCode
             * @param modifierKeys
             */
            public EntityCacheDebuggerShortcutListener(
                    String caption,
                    int keyCode,
                    int modifierKey) {
                super(caption, keyCode, new int[]{modifierKey});
            }


            public EntityCacheDebuggerShortcutListener(
                    String caption,
                    int keyCode) {
                super(caption, new int[]{keyCode});
            }

            @Override
            public void handleAction(Object sender, Object target) {
                if(sender instanceof AbstractView) {
                    EntityCacheDebugger.this.openFor((AbstractView)sender);
                }
                if(sender instanceof Window && ((Window)sender).getContent() instanceof AbstractPopupView) {
                    EntityCacheDebugger.this.openFor((AbstractPopupView)((Window)sender).getContent());
                }

            }
        };

}
