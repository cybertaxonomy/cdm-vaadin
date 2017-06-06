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
import org.springframework.context.event.EventListener;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;

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
    @EventListener // the generic type parameter <DTO> must not be used here otherwise events will not be received
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }

        logger.trace(this._toString() + ".onEditorPreSaveEvent - starting transaction");
        tx = startTransaction();
        // merge the bean and update the fieldGroup with the merged bean, so that updating
        // of field values in turn of the commit are can not cause LazyInitializationExeptions
        // the bean still has the original values at this point
        logger.trace(this._toString() + ".onEditorPreSaveEvent - merging bean into session");
        mergedBean((DTO) preSaveEvent.getBean());
    }

    /**
     * @return
     *
     */
    protected TransactionStatus startTransaction() {
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
        return getRepo().startTransaction(true);
    }

    @Override
    @EventListener // the generic type parameter <DTO> must not be used here otherwise events will not be received
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        // the bean is now updated with the changes made by the user
        Session session = getSession();
        logger.trace(this._toString() + ".onEditorSaveEvent - merging bean into session");
        // merge the changes into the session, ...
        DTO bean = mergedBean((DTO) saveEvent.getBean());

        Type changeEventType;
        if(bean.getId() > 1){
            changeEventType = Type.MODIFIED;
        } else {
            changeEventType = Type.CREATED;
        }
        getRepo().getCommonService().saveOrUpdate(bean);
        session.flush();
        logger.trace(this._toString() + ".onEditorSaveEvent - session flushed");
        getRepo().commitTransaction(tx);
        tx = null;
        if(session.isOpen()){
            session.close();
        }
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
    private DTO mergedBean(DTO bean) {
        Session session = getSession();

        // session.clear();
        if(session.contains(bean)){
            // evict bean before merge to avoid duplicate beans in same session
            logger.trace(this._toString() + ".mergedBean() - evict " + bean.toString());
            session.evict(bean);
        }

        logger.trace(this._toString() + ".mergedBean() - doing merge of" + bean.toString());
        // to avoid merge problems as described in https://dev.e-taxonomy.eu/redmine/issues/6687
        // we are set the hibernate property hibernate.event.merge.entity_copy_observer=allow
        @SuppressWarnings("unchecked")
        DTO mergedBean = (DTO) session.merge(bean);
        logger.trace(this._toString() + ".mergedBean() - bean after merge " + bean.toString());
        ((AbstractPopupEditor<DTO, AbstractCdmEditorPresenter<DTO, V>>)getView()).updateItemDataSource(mergedBean);
        return mergedBean;

    }


    @Override
    protected final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
    }

    @Override
    protected final void deleteBean(DTO bean){
        logger.trace(this._toString() + ".onEditorPreSaveEvent - starting transaction");
        tx = startTransaction();
        logger.trace(this._toString() + ".deleteBean - deleting" + bean.toString());
        DeleteResult result = executeServiceDeleteOperation(bean);
        if(result.isOk()){
            Session session = getSession();
            session.flush();
            logger.trace(this._toString() + ".deleteBean - session flushed");
            getRepo().commitTransaction(tx);
            tx = null;
            session.close();
            logger.trace(this._toString() + ".deleteBean - transaction comitted");
            eventBus.publishEvent(new EntityChangeEvent(bean.getClass(), bean.getId(), Type.REMOVED));
        } else {
            String notificationTitle;
            StringBuffer messageBody = new StringBuffer();
            if(result.isAbort()){
                notificationTitle = "The delete operation as abborded by the system.";
            } else {
                notificationTitle = "An error occured during the delete operation.";
            }
            if(!result.getExceptions().isEmpty()){
                messageBody.append("<h3>").append("Exceptions:").append("</h3>").append("<ul>");
                result.getExceptions().forEach(e -> messageBody.append("<li>").append(e.getMessage()).append("</li>"));
                messageBody.append("</ul>");
            }
            if(!result.getRelatedObjects().isEmpty()){
                messageBody.append("<h3>").append("Related objects exist:").append("</h3>").append("<ul>");
                result.getRelatedObjects().forEach(e -> {
                    messageBody.append("<li>");
                    if(IdentifiableEntity.class.isAssignableFrom(e.getClass())){
                        messageBody.append(((IdentifiableEntity)e).getTitleCache());
                    } else {
                        messageBody.append(e.toString());
                    }
                    messageBody.append("</li>");
                }
                );

                messageBody.append("</ul>");
            }
            Notification notification = new Notification(
                   notificationTitle,
                   messageBody.toString(),
                   com.vaadin.ui.Notification.Type.ERROR_MESSAGE,
                   true);
            notification.show(UI.getCurrent().getPage());
            tx = null;
        }
    }

    /**
     * Implementations will execute the {@link
     *
     * @return
     */
    protected abstract DeleteResult executeServiceDeleteOperation(DTO bean);

}
