/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;

/**
 * @author a.kohlbecker
 * @since Jun 26, 2017
 *
 * TODO better naming of this class, ServiceWrapper, ServiceOperator, ...?
 *
 */
public class CdmStore<T extends CdmBase, S extends IService<T>> {

    private static final Logger logger = Logger.getLogger(CdmStore.class);

    private CdmRepository repo;

    private S service;

    TransactionStatus txStatus = null;

//    ConversationHolder conversationHolder = null;
//
//    /**
//     * @return the conversationHolder
//     */
//    public ConversationHolder getConversationHolder() {
//        return conversationHolder;
//    }

    protected DefaultTransactionDefinition txDefinition = null;

    /**
     *
     * @param repo
     * @param service
     *            may be <code>null</code>, but delete operations will fail with
     *            a NullPointerException in this case.
     */
    public CdmStore(CdmRepository repo, S service) {

        this.repo = repo;
        this.service = service;

    }

//    /**
//     * constructor which takes a ConversationHolder. The supplying class of the conversationHolder needs
//     * to care for <code>bind()</code>, <code>unbind()</code> and <code>close()</code> since the store is
//     * only responsible for starting and committing of transactions.
//     *
//     * @param repo
//     * @param service
//     * @param conversationHolder
//     */
//    public CdmStore(CdmRepository repo, S service, ConversationHolder conversationHolder) {
//
//        this.repo = repo;
//        this.service = service;
//        this.conversationHolder = conversationHolder;
//
//    }

    /**
     * @return
     *
     */
    public TransactionStatus startTransaction() {
//        if(conversationHolder != null && !conversationHolder.isTransactionActive()){
//            //conversationHolder.setDefinition(getTransactionDefinition());
//            return conversationHolder.startTransaction();
//        } else {
            checkExistingTransaction();
            txStatus = repo.startTransaction();
            return txStatus;
//        }
    }

    /**
     *
     */
    protected void checkExistingTransaction() {
        if (txStatus != null) {
            // @formatter:off
            // holding the TransactionStatus as state is not good design. we
            // should change the save operation
            // in the EditorView so that the presenter can process the save in
            // one method call.
            // Problems:
            // 1. the fieldGroup needs a open session and read transaction
            // during the validation, otherwise
            // LazyInitialisationExceptions occur.
            // 2. passing the TransactionState to the view also doesn't seem
            // like a good idea.
            // @formatter:on
            throw new RuntimeException("Opening a second transaction in the same" + this.getClass().getSimpleName() + " is not supported");
        }
    }

    /**
     * If the bean is contained in the session it is being updated by doing an
     * evict and merge. The fieldGroup is updated with the merged bean.
     *
     *
     * @param bean
     * @return The bean merged to the session or original bean in case a merge
     *         was not necessary.
     */
    public T mergedBean(T bean) {

        Session session = getSession();

        // session.clear();
        if (session.contains(bean)) {
            // evict bean before merge to avoid duplicate beans in same session
            logger.trace(this._toString() + ".mergedBean() - evict " + bean.toString());
            session.evict(bean);
        }

        logger.trace(this._toString() + ".mergedBean() - doing merge of" + bean.toString());
        // to avoid merge problems as described in
        // https://dev.e-taxonomy.eu/redmine/issues/6687
        // we are set the hibernate property
        // hibernate.event.merge.entity_copy_observer=allow
        @SuppressWarnings("unchecked")
        T mergedBean = (T) session.merge(bean);
        logger.trace(this._toString() + ".mergedBean() - bean after merge " + bean.toString());
        return mergedBean;

    }

    /**
     * @return
     */
    private Session getSession() {

        Session session;
//        if(conversationHolder != null){
//            session = conversationHolder.getSession();
//        } else {
            session = repo.getSession();
//        }
        logger.trace(this._toString() + ".getSession() - session:" + session.hashCode() + ", persistenceContext: "
                + ((SessionImplementor) session).getPersistenceContext() + " - " + session.toString());

        return session;
    }

    protected String _toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    /**
     *
     * @param bean

     * @return the merged bean, this bean is <b>not reloaded</b> from the
     *         persistent storage.
     */
    public EntityChangeEvent saveBean(T bean) {

        Type changeEventType;
        if(bean.getId() > 1){
            changeEventType = Type.MODIFIED;
        } else {
            changeEventType = Type.CREATED;
        }

        Session session = getSession();
        logger.trace(this._toString() + ".onEditorSaveEvent - session: " + session.hashCode());

        if(txStatus == null
//                || (conversationHolder != null && !conversationHolder.isTransactionActive())
                ){
            // no running transaction, start one ...
            startTransaction();
        }

        logger.trace(this._toString() + ".onEditorSaveEvent - merging bean into session");
        // merge the changes into the session, ...

        T mergedBean = mergedBean(bean);
        //T mergedBean = bean;
        // NOTE: saveOrUpdate is really needed here even if we to a merge before
        repo.getCommonService().saveOrUpdate(mergedBean);
        session.flush();
        commitTransction();

        return new EntityChangeEvent(mergedBean.getClass(), mergedBean.getId(), changeEventType);
    }

    /**
     *
     * @param bean
     * @return a EntityChangeEvent in case the deletion was successful otherwise <code>null</code>.
     */
    public final EntityChangeEvent deleteBean(T bean) {

        logger.trace(this._toString() + ".onEditorPreSaveEvent - starting transaction");

        startTransaction();
        logger.trace(this._toString() + ".deleteBean - deleting" + bean.toString());
        DeleteResult result = service.delete(bean);
        if (result.isOk()) {

            getSession().flush();
            commitTransction();
            logger.trace(this._toString() + ".deleteBean - transaction comitted");
            return new EntityChangeEvent(bean.getClass(), bean.getId(), Type.REMOVED);
        } else {
            String notificationTitle;
            StringBuffer messageBody = new StringBuffer();
            if (result.isAbort()) {
                notificationTitle = "The delete operation as abborded by the system.";
            } else {
                notificationTitle = "An error occured during the delete operation.";
            }
            if (!result.getExceptions().isEmpty()) {
                messageBody.append("<h3>").append("Exceptions:").append("</h3>").append("<ul>");
                result.getExceptions().forEach(e -> messageBody.append("<li>").append(e.getMessage()).append("</li>"));
                messageBody.append("</ul>");
            }
            if (!result.getRelatedObjects().isEmpty()) {
                messageBody.append("<h3>").append("Related objects exist:").append("</h3>").append("<ul>");
                result.getRelatedObjects().forEach(e -> {
                    messageBody.append("<li>");
                    if (IdentifiableEntity.class.isAssignableFrom(e.getClass())) {
                        messageBody.append(((IdentifiableEntity) e).getTitleCache());
                    } else {
                        messageBody.append(e.toString());
                    }
                    messageBody.append("</li>");
                });

                messageBody.append("</ul>");
            }
            Notification notification = new Notification(notificationTitle, messageBody.toString(),
                    com.vaadin.ui.Notification.Type.ERROR_MESSAGE, true);
            notification.show(UI.getCurrent().getPage());
            txStatus = null;
        }
        return null;
    }


    protected void commitTransction() {

//        if(conversationHolder != null){
//            conversationHolder.commit();
//        } else {
            repo.commitTransaction(txStatus);
            txStatus = null;
//        }
    }

    /**
     * @param entityId
     */
    public T loadBean(int entityId) {
//        conversationHolder.startTransaction();
        return service.find(entityId);
    }

    public S getService() {
        return service;
    }


}
