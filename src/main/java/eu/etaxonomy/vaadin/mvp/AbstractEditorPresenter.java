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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionDefinition;

import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.session.ViewScopeConversationHolder;
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

    @Autowired
    protected ApplicationEventPublisher eventBus;

    /**
     * Load the bean to be edited in the editor freshly from the persistent storage.
     * Ore create an new empty instance in case the supplied <code>identifier</code> is <code>null</code>.
     *
     * @param identifier
     * @return
     */
    protected abstract DTO loadBeanById(Object identifier);

    /**
     * This method is called directly before setting the bean as item data source to
     * the field group of the editor.
     * <p>
     * Override this method to pre-process the bean if needed. This can be the case if
     * you are using a persistence layer with short running session like Hibernate.
     *
     * @param bean
     * @return
     */
    protected DTO prepareAsFieldGroupDataSource(DTO bean){

        return bean;
    }

    @Override
    protected TransactionDefinition getTransactionDefinition(){
        super.getTransactionDefinition();
        if(definition.isReadOnly()){
            definition.setReadOnly(false);
        }
        return definition;
    }

    /**
     * Regarding changing the Flush mode see see also {@link ViewScopeConversationHolder}
     *
     * @param preSaveEvent
     */
    @EventListener
    public void onEditorPreSaveEvent(EditorPreSaveEvent<DTO> preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }
        ensureBoundConversation();
        previousPreSaveEvenFlushMode = getConversationHolder().getSession().getFlushMode();
        getConversationHolder().getSession().setFlushMode(FlushMode.AUTO);

    }

    /**
     * Regarding changing the Flush mode see see also {@link ViewScopeConversationHolder}
     *
     * @param saveEvent
     */
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent<DTO> saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        DTO bean = saveEvent.getBean();
        saveBean(bean);
        getConversationHolder().getSession().setFlushMode(previousPreSaveEvenFlushMode);
        previousPreSaveEvenFlushMode = null;
    }

    /**
    * Regarding changing the Flush mode see see also {@link ViewScopeConversationHolder}
    *
    * @param saveEvent
    */
   @EventListener
   public void onEditorDeleteEvent(EditorDeleteEvent<DTO> deleteEvent){
       if(!isFromOwnView(deleteEvent)){
           return;
       }
       if(!conversationBound){
           bindConversation();
       }
       FlushMode previousFlushMode = getSession().getFlushMode();
       getConversationHolder().getSession().setFlushMode(FlushMode.AUTO);
       deleteBean(deleteEvent.getBean());
       getConversationHolder().getSession().setFlushMode(previousFlushMode);
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
