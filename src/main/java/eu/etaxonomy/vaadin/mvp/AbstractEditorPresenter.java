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

import com.vaadin.server.ServletPortletHelper;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction;
import eu.etaxonomy.cdm.vaadin.server.CdmSpringVaadinServletService;
import eu.etaxonomy.cdm.vaadin.server.RequestEndListener;
import eu.etaxonomy.cdm.vaadin.server.RequestStartListener;
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
public abstract class AbstractEditorPresenter<DTO extends Object, V extends ApplicationView<?>> extends AbstractPresenter<V>
implements RequestEndListener, RequestStartListener {


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

//    @Override
//    protected TransactionDefinition getTransactionDefinition(){
//        super.getTransactionDefinition();
//        if(definition.isReadOnly()){
//            definition.setReadOnly(false);
//        }
//        return definition;
//    }

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
        getSession().setFlushMode(FlushMode.AUTO);

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
        getSession().setFlushMode(previousPreSaveEvenFlushMode);
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
       FlushMode previousFlushMode = getSession().getFlushMode();
       getSession().setFlushMode(FlushMode.AUTO);
       deleteBean(deleteEvent.getBean());
       getSession().setFlushMode(previousFlushMode);
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

    @Override
    protected void init(V view) {
        super.init(view);
        registerListeners();
    }

    @Override
    public void onViewExit() {
        super.onViewExit();
        unregisterListeners();
    }


    // -------------------------------------------------------------------------

    protected void registerListeners() {
     // register as request start and end listener
        VaadinService service = UI.getCurrent().getSession().getService();
        if(service instanceof CdmSpringVaadinServletService){
            logger.trace(String.format("~~~~~ %s register as request listener", _toString()));
            ((CdmSpringVaadinServletService)service).addRequestEndListener(this);
            if(logger.isTraceEnabled()){
                ((CdmSpringVaadinServletService)service).addRequestStartListener(this);
            }
        } else {
            throw new RuntimeException("Using the CdmSpringVaadinServletService is required for proper per view conversation handling");
        }
    }

    /**
    *
    */
   protected void unregisterListeners() {
       VaadinService service = UI.getCurrent().getSession().getService();
       if(service instanceof CdmSpringVaadinServletService){
           logger.trace(String.format("~~~~~ %s un-register as request listener", _toString()));
           ((CdmSpringVaadinServletService)service).removeRequestEndListener(this);
           if(logger.isTraceEnabled()){
               ((CdmSpringVaadinServletService)service).removeRequestStartListener(this);
           }
       } else {
           throw new RuntimeException("Using the CdmSpringVaadinServletService is required for proper per view conversation handling");
       }
   }

    /**
     * <b>ONLY USED FOR LOGGING</b> when Level==TRACE
     * {@inheritDoc}
     */
    @Override
    public void onRequestStart(VaadinRequest request){

        if(requestNeedsSession(request) ){

            if(getView() instanceof AbstractPopupEditor){
                Object bean = ((AbstractPopupEditor)getView()).getBean();
                getSession().merge(bean);
            }

        } else {
            // ignore hartbeat, fileupload, push etc
            logger.trace("ignoring request:" + request.getPathInfo());
        }
    }

    /**
     * Returns <code>true</code> for:
     * <ul>
     *   <li>..</li>
     * <ul>
     *
     * Return <code>false</code> for:
     *
     * <ul>
     *   <li>UILD request in a existing view, like clicking on a button</li>
     * <ul>
     *
     * @return
    protected boolean isActiveView(){
        return UI.getCurrent() != null && getView() != null && getView() == navigationManager.getCurrentView();
    }
     */

    @Override
    public void onRequestEnd(VaadinRequest request, VaadinSession session){

        if(requestNeedsSession(request) ){
            logger.trace("onRequestEnd() " + request.getPathInfo() + " " + _toString());

        } else {
            // ignore hartbeat, fileupload, push etc
            logger.trace("ignoring request:" + request.getPathInfo());
        }

    }

    /**
     * @param request
     * @return
     */
    protected boolean requestNeedsSession(VaadinRequest request) {
        return !(
                ServletPortletHelper.isAppRequest(request) // includes published file request
             || ServletPortletHelper.isFileUploadRequest(request)
             || ServletPortletHelper.isHeartbeatRequest(request)
             || ServletPortletHelper.isPushRequest(request)
             );
    }

    protected abstract void saveBean(DTO bean);

    /**
     * @param bean
     */
    protected abstract void deleteBean(DTO bean);

}
