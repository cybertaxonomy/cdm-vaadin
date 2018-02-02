/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.hibernate.FlushMode;
import org.vaadin.spring.events.annotation.EventBusListenerMethod;

import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.vaadin.mvp.event.EditorDeleteEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorViewEvent;

/**
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractEditorPresenter<DTO extends Object, V extends ApplicationView<?>> extends AbstractPresenter<V> {


    private static final long serialVersionUID = -6677074110764145236L;

    FlushMode previousPreSaveEvenFlushMode = null;

    protected BeanInstantiator<DTO> beanInstantiator = null;

    /**
     * Load the bean to be edited in the editor freshly from the persistent storage.
     * Ore create an new empty instance in case the supplied <code>identifier</code> is <code>null</code>.
     *
     * @param identifier
     * @return
     */
    protected abstract DTO loadBeanById(Object identifier);

    /**
     * @param beanInstantiator the beanInstantiator to set
     */
    public void setBeanInstantiator(BeanInstantiator<DTO> beanInstantiator) {
        this.beanInstantiator = beanInstantiator;
    }

    @EventBusListenerMethod
    public void onEditorPreSaveEvent(EditorPreSaveEvent<DTO> preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }
    }

    @EventBusListenerMethod
    public void onEditorSaveEvent(EditorSaveEvent<DTO> saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        DTO bean = saveEvent.getBean();
        saveBean(bean);
    }

   /**
    * @param saveEvent
    */
   @EventBusListenerMethod
   public void onEditorDeleteEvent(EditorDeleteEvent<DTO> deleteEvent){
       if(!isFromOwnView(deleteEvent)){
           return;
       }
       deleteBean(deleteEvent.getBean());
   }

    /**
     * @param saveEvent
     * @return
     */
    protected boolean isFromOwnView(EditorViewEvent saveEvent) {
        return saveEvent.getView().equals(getView());
    }

    protected Class<V> getViewType() {
        return (Class<V>) super.getView().getClass();
    }

    protected boolean isFromOwnView(AbstractEditorAction action){
        return action.getSourceView() != null && getView().equals(action.getSourceView());
    }


    protected abstract void saveBean(DTO bean);

    /**
     * @param bean
     */
    protected abstract void deleteBean(DTO bean);

}
