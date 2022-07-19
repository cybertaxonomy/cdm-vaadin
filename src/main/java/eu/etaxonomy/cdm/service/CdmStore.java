/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.TransactionStatus;

import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.ViewScope;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.api.application.CdmRepository;
import eu.etaxonomy.cdm.api.service.DeleteResult;
import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.agent.AgentBase;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.IdentifiableEntity;
import eu.etaxonomy.cdm.model.name.NameTypeDesignation;
import eu.etaxonomy.cdm.model.name.Registration;
import eu.etaxonomy.cdm.model.name.TaxonName;
import eu.etaxonomy.cdm.model.occurrence.Collection;
import eu.etaxonomy.cdm.model.occurrence.SpecimenOrObservationBase;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent.Type;
import eu.etaxonomy.vaadin.mvp.AbstractView;

/**
 * @author a.kohlbecker
 * @since Jun 26, 2017
 *
 * TODO better naming of this class, ServiceWrapper, ServiceOperator, ...?
 *
 */
@SpringComponent
@ViewScope
public class CdmStore {

    private final static Logger logger = LogManager.getLogger();

    @Autowired
    @Qualifier("cdmRepository")
    private CdmRepository repo;

    protected String _toString() {
        return this.getClass().getSimpleName() + "@" + this.hashCode();
    }

    /**
     *
     * @param bean
     *
     * @return the merged bean, this bean is <b>not reloaded</b> from the
     *         persistent storage.
     */
    public <T extends CdmBase> EntityChangeEvent saveBean(T bean, AbstractView view) {

        Type changeEventType;
        if(bean.isPersited()){
            changeEventType = Type.MODIFIED;
        } else {
            changeEventType = Type.CREATED;
        }

        try{
            TransactionStatus txStatus = repo.startTransaction();
            Session session = repo.getSession();
            try {
                logger.trace(this._toString() + ".onEditorSaveEvent - merging bean into session");
                // merge the changes into the session, ...
                if (session.contains(bean)) {
                    // evict bean before merge to avoid duplicate beans in same session
                    logger.trace(this._toString() + ".mergedBean() - evict " + bean.toString());
                    session.evict(bean);
                }
                logger.trace(this._toString() + ".mergedBean() - doing merge of" + bean.toString());
                @SuppressWarnings("unchecked")
                T mergedBean = (T) session.merge(bean);
                repo.commitTransaction(txStatus);
                return new EntityChangeEvent(mergedBean, changeEventType, view);
            } catch(Exception e){
                transactionRollbackIfNotCompleted(txStatus);
                throw e;
            }
        } finally {
            repo.clearSession(); // #7559
        }

    }


    /**
     * @param txStatus
     */
    public void transactionRollbackIfNotCompleted(TransactionStatus txStatus) {
        if(!txStatus.isCompleted()){
            repo.getTransactionManager().rollback(txStatus);
        }
    }

    /**
     *
     * @param bean
     * @return a EntityChangeEvent in case the deletion was successful otherwise <code>null</code>.
     */
    public final <T extends CdmBase> EntityChangeEvent deleteBean(T bean, AbstractView view) {

        IService<T> typeSpecificService = serviceFor(bean);

        try{
            logger.trace(this._toString() + ".deleteBean - deleting" + bean.toString());
            DeleteResult result = typeSpecificService.delete(bean);
            if (result.isOk()) {
                return new EntityChangeEvent(bean, Type.REMOVED, view);
            } else {
                handleDeleteresultInError(result);
            }
        } finally {
            repo.clearSession(); // #7559
        }

        return null;
    }

    /**
     * @param result
     */
    public static void handleDeleteresultInError(DeleteResult result) {
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
    }

    @SuppressWarnings("unchecked")
    protected <T extends CdmBase> IService<T> serviceFor(T bean){
         Class<? extends CdmBase> cdmType = bean.getClass();

         if(Registration.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getRegistrationService();
         } else if(TaxonName.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getNameService();
         } else if(Reference.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getReferenceService();
         } else if (NameTypeDesignation.class.isAssignableFrom(cdmType)){
             throw new RuntimeException("no generic sercvice for NameTypeDesignation, use dedicated methods of NameService");
         } else if (SpecimenOrObservationBase.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getOccurrenceService();
         } else if (AgentBase.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getAgentService();
         } else if (Collection.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getCollectionService();
         } else if (Collection.class.isAssignableFrom(cdmType)){
             return (IService<T>) repo.getCollectionService();
         } else {
             throw new RuntimeException("Implementation to find service for " + cdmType + " still missing.");
         }
    }

}
