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

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
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

    Session session = null;

    CdmStore<DTO, IService<DTO>> store ;

    public AbstractCdmEditorPresenter() {
        super();
        logger.trace(this._toString() + " constructor");
    }

    protected CdmStore<DTO, IService<DTO>> getStore() {
        if(store == null){
            store = new CdmStore<>(getRepo(), getService());
        }
        return store;
    }


    /**
     * @return
     */
    protected abstract IService<DTO> getService();

    @SuppressWarnings("unchecked")
    @Override
    @EventListener // the generic type parameter <DTO> must not be used here otherwise events will not be received
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){
        if(!isFromOwnView(preSaveEvent)){
            return;
        }

        session = getSession();
        logger.trace(this._toString() + ".onEditorPreSaveEvent - session: " + session);
        logger.trace(this._toString() + ".onEditorPreSaveEvent - starting transaction");
        tx = getStore().startTransaction();
        // merge the bean and update the fieldGroup with the merged bean, so that updating
        // of field values in turn of the commit are can not cause LazyInitializationExeptions
        // the bean still has the original values at this point
        logger.trace(this._toString() + ".onEditorPreSaveEvent - merging bean into session");
        mergedBean((DTO) preSaveEvent.getBean());
    }

    @SuppressWarnings("unchecked")
    @Override
    @EventListener // the generic type parameter <DTO> must not be used here otherwise events will not be received
    public void onEditorSaveEvent(EditorSaveEvent saveEvent){
        if(!isFromOwnView(saveEvent)){
            return;
        }
        // the bean is now updated with the changes made by the user
        DTO bean = (DTO) saveEvent.getBean();
        bean = handleTransientProperties(bean);
        EntityChangeEvent changeEvent = getStore().saveBean(bean);
        if(changeEvent != null){
            eventBus.publishEvent(changeEvent);
        }
    }

    /**
     * EditorPresneters for beans with transient properties should overwrite this method to
     * update the beanItem with the changes made to the transient properties.
     * <p>
     * This is necessary because Vaadin MethodProperties are readonly when no setter is
     * available. This can be the case with transient properties. Automatic updating
     * of the property during the fieldGroup commit does not work in this case.
     *
     * @deprecated editors should operate on DTOs instead, remove this method if unused.
     */
    @Deprecated
    protected DTO handleTransientProperties(DTO bean) {
        // no need to handle transient properties in the generic case
        return bean;
    }

    /**
     * If the bean is contained in the session it is being updated by
     * doing an evict and merge. The fieldGroup is updated with the merged bean.
     *
     * @param bean
     *
     * @return The bean merged to the session or original bean in case a merge was not necessary.
     */
    private DTO mergedBean(DTO bean) {
        DTO mergedBean = getStore().mergedBean(bean);
        ((AbstractPopupEditor<DTO, AbstractCdmEditorPresenter<DTO, V>>)getView()).updateItemDataSource(mergedBean);
        return mergedBean;

    }

    @Override
    protected final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
        // see onEditorSaveEvent() instead
    }

    @Override
    protected final void deleteBean(DTO bean){
        EntityChangeEvent changeEvent = getStore().deleteBean(bean);
        if(changeEvent != null){
            eventBus.publishEvent(changeEvent);
        }

    }

}
