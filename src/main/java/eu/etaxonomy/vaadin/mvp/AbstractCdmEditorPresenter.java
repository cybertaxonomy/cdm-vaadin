/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.util.BeanItem;

import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorViewEvent;

/**
 * Provides generic save operations of modified cdm entities.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractCdmEditorPresenter<DTO extends CdmBase, V extends ApplicationView<?>> extends AbstractEditorPresenter<DTO, V> {

    private static final long serialVersionUID = 2218185546277084261L;

    private static final Logger logger = Logger.getLogger(AbstractCdmEditorPresenter.class);

    TransactionStatus tx = null;

    public AbstractCdmEditorPresenter() {
        super();
        logger.trace(this._toString() + " constructor");
    }

    @Override
    @EventListener
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        if(!isResponsible(preSaveEvent)){
            return;
        }
        if(tx != null){
            // @formatter:off
            // holding the TransactionStatus as state is not good design. we should change the save operation
            // in the EditorView so that the presenter can process the save in one method call.
            // Problems:
            // 1. the fieldGroup needs a open session and read transaction during the validation, otherwise
            //    LazyInitialisationExceptions occur.
            // 2. passing the TransactionState to the view also doesn't seem like a good idea.
            // @formatter:on
            throw new RuntimeException("Can't process a second save operation while another one is in progress.");
        }
        super.onEditorPreSaveEvent(preSaveEvent);

        logger.trace(this._toString() + ".onEditorPreSaveEvent - starting transaction");
        tx = getRepo().startTransaction(true);
        // merge the bean and update the fieldGroup with the merged bean, so that updating
        // of field values in turn of the commit are can not cause LazyInitializationExeptions
        // the bean still has the original values at this point
        logger.trace(this._toString() + ".onEditorPreSaveEvent - merging bean into session");
        mergedBean(preSaveEvent.getCommitEvent());

    }

    @Override
    @EventListener
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        if(!isResponsible(saveEvent)){
            return;
        }
        // the bean is now updated with the changes made by the user
        // merge the bean into the session, ...
        logger.trace(this._toString() + ".onEditorSaveEvent - merging bean into session");
        DTO bean = mergedBean(saveEvent.getCommitEvent());

        Type changeEventType;
        if(bean.getId() > 1){
            changeEventType = Type.MODIFIED;
        } else {
            changeEventType = Type.CREATED;
        }
        getRepo().getCommonService().saveOrUpdate(bean);
        getSession().flush();
        logger.trace(this._toString() + ".onEditorSaveEvent - session flushed");
        getRepo().commitTransaction(tx);
        tx = null;
        logger.trace(this._toString() + ".onEditorSaveEvent - transaction comitted");
        eventBus.publishEvent(new EntityChangeEvent(bean.getClass(), bean.getId(), changeEventType));
    }

    /**
     * Obtains the bean from the fieldGroup. If the bean is contained in the session is being updated by
     * doing an evict and merge. The fieldGroup is updated with the merged bean.
     *
     *
     * @param CommitEvent
     * @return The bean merged to the session or original bean in case a merge was not necessary.
     */
    private DTO mergedBean(CommitEvent commitEvent) {
        // using just some service to get hold of the session
        Session session = getSession();
        @SuppressWarnings("unchecked")
        BeanItem<DTO> itemDataSource = ((BeanFieldGroup<DTO>)commitEvent.getFieldBinder()).getItemDataSource();
        DTO bean = itemDataSource.getBean();
        if(session.contains(bean)){

            if(session.isOpen()){
                // evict bean before merge to avoid duplicate beans in same session
                logger.trace(this._toString() + ".mergedBean() - evict " + bean.toString());
                session.evict(bean);
            }
            logger.trace(this._toString() + ".mergedBean() - doing merge of" + bean.toString());
            @SuppressWarnings("unchecked")
            DTO mergedBean = (DTO) session.merge(bean);
            logger.trace(this._toString() + ".mergedBean() - bean after merge " + bean.toString());
            itemDataSource.setBean(mergedBean);
            return mergedBean;
        }
        return bean;
    }


    /**
     * @return
     */
    private Session getSession() {
        Session session = getRepo().getUserService().getSession();
        logger.trace(this._toString() + ".getSession() - session:" + session.hashCode() +", persistenceContext: " + ((SessionImplementor)session).getPersistenceContext() + " - " + session.toString());
        return session;
    }

    /**
     * see  #6673 (https://dev.e-taxonomy.eu/redmine/issues/6673)
     * @param event
     * @return
     */
    private boolean isResponsible(EditorViewEvent event){

        return !isViewLess() && event.getView().getClass().equals(getViewType());
    }

    @Override
    protected final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
    }

    private String _toString(){
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

}
