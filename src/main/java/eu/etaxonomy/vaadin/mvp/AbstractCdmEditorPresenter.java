/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.vaadin.mvp;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.cache.CdmTransientEntityCacher;
import eu.etaxonomy.cdm.debug.PersistentContextAnalyzer;
import eu.etaxonomy.cdm.model.ICdmCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
import eu.etaxonomy.cdm.vaadin.security.UserHelper;
import eu.etaxonomy.cdm.vaadin.view.name.CachingPresenter;
import eu.etaxonomy.vaadin.mvp.event.EditorPreSaveEvent;
import eu.etaxonomy.vaadin.mvp.event.EditorSaveEvent;

/**
 * Provides generic save operations of modified cdm entities.
 *
 * @author a.kohlbecker
 * @since Apr 5, 2017
 *
 */
public abstract class AbstractCdmEditorPresenter<DTO extends CdmBase, V extends ApplicationView<?>> extends AbstractEditorPresenter<DTO, V>
    implements CachingPresenter {

    private static final long serialVersionUID = 2218185546277084261L;

    private static final Logger logger = Logger.getLogger(AbstractCdmEditorPresenter.class);

    /**
     * if not null, this CRUD set is to be used to create a CdmAuthoritiy for the base entity which will be
     * granted to the current use as long this grant is not assigned yet.
     */
    protected EnumSet<CRUD> crud = null;


    private ICdmCacher cache;

    private java.util.Collection<CdmBase> rootEntities = new HashSet<>();

    public AbstractCdmEditorPresenter() {
        super();
        logger.trace(this._toString() + " constructor");
    }

    CdmStore<DTO, IService<DTO>> store ;

    protected CdmAuthority newAuthorityCreated;

    protected CdmStore<DTO, IService<DTO>> getStore() {
        if(store == null){
            store = new CdmStore<>(getRepo(), getService());
        }
        return store;
    }

    @Override
    protected DTO loadBeanById(Object identifier) {

        DTO cdmEntitiy;
        if(identifier != null) {
            Integer integerID = (Integer)identifier;
            // CdmAuthority is needed before the bean is loaded into the session.
            // otherwise adding the authority to the user would cause a flush
            guaranteePerEntityCRUDPermissions(integerID);
            cdmEntitiy = loadCdmEntityById(integerID);
        } else {
            cdmEntitiy = loadCdmEntityById(null);
            if(cdmEntitiy != null){
                guaranteePerEntityCRUDPermissions(cdmEntitiy);
            }
        }


        cache = new CdmTransientEntityCacher(this);
        // need to use load but put see #7214
        cdmEntitiy = cache.load(cdmEntitiy);
        addRootEntity(cdmEntitiy);

        return cdmEntitiy;
    }


    /**
     * @param identifier
     * @return
     */
    protected abstract DTO loadCdmEntityById(Integer identifier);

    /**
     * Grant per entity CdmAuthority to the current user <b>for the bean which is not yet loaded</b>
     * into the editor. The <code>CRUD</code> to be granted are stored in the <code>crud</code> field.
     */
    protected abstract void guaranteePerEntityCRUDPermissions(Integer identifier);

    /**
     * Grant per entity CdmAuthority to the current user for the bean which is loaded
     * into the editor. The <code>CRUD</code> to be granted are stored in the <code>crud</code> field.
     */
     protected abstract void guaranteePerEntityCRUDPermissions(DTO bean);

    /**
     * @return
     */
    protected abstract IService<DTO> getService();

    @SuppressWarnings("unchecked")
    @Override
    // @EventBusListenerMethod // already annotated at super class
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){

        if(!isFromOwnView(preSaveEvent)){
            return;
        }
        super.onEditorPreSaveEvent(preSaveEvent);
    }

    @SuppressWarnings("unchecked")
    @Override
    // @EventBusListenerMethod // already annotated at super class
    public void onEditorSaveEvent(EditorSaveEvent<DTO> saveEvent){

        if(!isFromOwnView(saveEvent)){
            return;
        }

        // the bean is now updated with the changes made by the user
        DTO bean = saveEvent.getBean();

        if(logger.isTraceEnabled()){
            PersistentContextAnalyzer pca = new PersistentContextAnalyzer(bean);
            pca.printEntityGraph(System.err);
            pca.printCopyEntities(System.err);
        }
        bean = handleTransientProperties(bean);

        if(logger.isTraceEnabled()){
            PersistentContextAnalyzer pca = new PersistentContextAnalyzer(bean);
            pca.printEntityGraph(System.err);
            pca.printCopyEntities(System.err);
        }
        try {
            EntityChangeEvent changeEvent = getStore().saveBean(bean, (AbstractView) getView());

            if(changeEvent != null){
                viewEventBus.publish(this, changeEvent);
            }
        } catch (HibernateException e){
            if(newAuthorityCreated != null){
                UserHelper.fromSession().removeAuthorityForCurrentUser(newAuthorityCreated);
            }
            throw e;
        }
    }

    /**
     * EditorPresenters for beans with transient properties should overwrite this method to
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

//    @Override
//    protected DTO prepareAsFieldGroupDataSource(DTO bean){
//        DTO mergedBean = getStore().mergedBean(bean);
//        // DTO mergedBean = bean;
//        return mergedBean;
//    }

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
    protected
    final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
        // see onEditorSaveEvent() instead
    }

    @Override
    protected void deleteBean(DTO bean){
        EntityChangeEvent changeEvent = getStore().deleteBean(bean, (AbstractView) getView());
        if(changeEvent != null){
            viewEventBus.publish(this, changeEvent);
        }

    }

    /**
     * @param crud
     */
    public void setGrantsForCurrentUser(EnumSet<CRUD> crud) {
        this.crud = crud;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICdmCacher getCache() {
        return cache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRootEntity(CdmBase entity) {
        rootEntities.add(entity);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<CdmBase> getRootEntities() {
        return rootEntities;
    }


}
