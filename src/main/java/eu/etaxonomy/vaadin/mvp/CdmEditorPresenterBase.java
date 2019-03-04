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
import java.util.UUID;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.api.utility.UserHelper;
import eu.etaxonomy.cdm.cache.CdmTransientEntityAndUuidCacher;
import eu.etaxonomy.cdm.debug.PersistentContextAnalyzer;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.User;
import eu.etaxonomy.cdm.model.name.Rank;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CRUD;
import eu.etaxonomy.cdm.persistence.hibernate.permission.CdmAuthority;
import eu.etaxonomy.cdm.service.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.CdmStore;
import eu.etaxonomy.cdm.service.ITaxonNameStringFilterablePagingProvider;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.vaadin.event.EntityChangeEvent;
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
public abstract class CdmEditorPresenterBase<DTO, CDM extends CdmBase, V extends ApplicationView<?>> extends AbstractEditorPresenter<DTO, V>
    implements CachingPresenter {

    private static final long serialVersionUID = 2218185546277084261L;

    private static final Logger logger = Logger.getLogger(CdmEditorPresenterBase.class);

    protected BeanInstantiator<DTO> beanInstantiator = null;

    @Autowired
    protected CdmBeanItemContainerFactory cdmBeanItemContainerFactory;

    @Autowired
    protected CdmFilterablePagingProviderFactory pagingProviderFactory;

    /**
     * @param beanInstantiator the beanInstantiator to set
     */
    public void setBeanInstantiator(BeanInstantiator<DTO> beanInstantiator) {
        this.beanInstantiator = beanInstantiator;
    }


    protected DTO createNewBean() {
        if(this.beanInstantiator != null){
            return beanInstantiator.createNewBean();
        }
        return defaultBeanInstantiator().createNewBean();
    }

    /**
     * @return
     */
    protected abstract BeanInstantiator<DTO> defaultBeanInstantiator();

    /**
     * if not null, this CRUD set is to be used to create a CdmAuthoritiy for the base entity which will be
     * granted to the current use as long this grant is not assigned yet.
     */
    protected EnumSet<CRUD> crud = null;


    private ICdmEntityUuidCacher cache;

    private java.util.Collection<CdmBase> rootEntities = new HashSet<>();

    public CdmEditorPresenterBase() {
        super();
        logger.trace(this._toString() + " constructor");
    }

    CdmStore<CDM, IService<CDM>> store ;

    protected CdmAuthority newAuthorityCreated;


    protected CdmStore<CDM, IService<CDM>> getStore() {
        if(store == null){
            store = new CdmStore<>(getRepo(), getService());
        }
        return store;
    }

    @Override
    protected DTO loadBeanById(Object identifier) {

        CDM cdmEntitiy;
        if(identifier != null) {
            UUID uuidIdentifier = (UUID)identifier;
            // CdmAuthority is needed before the bean is loaded into the session.
            // otherwise adding the authority to the user would cause a flush
            cdmEntitiy = loadCdmEntity(uuidIdentifier);
            guaranteePerEntityCRUDPermissions(cdmEntitiy);
        } else {
            cdmEntitiy = loadCdmEntity(null);
            if(cdmEntitiy != null){
                guaranteePerEntityCRUDPermissions(cdmEntitiy);
            }
        }
        cache = new CdmTransientEntityAndUuidCacher(this);
        // need to use load but put see #7214
        cdmEntitiy = cache.load(cdmEntitiy);
        addRootEntity(cdmEntitiy);

        DTO dto = createDTODecorator(cdmEntitiy);

        return dto;
    }

    /**
     * @param cdmEntitiy
     * @return
     */
    protected abstract DTO createDTODecorator(CDM cdmEntitiy);

    /**
     * @param cdmEntitiy
     */
    @Override
    protected void adaptToUserPermission(DTO dto) {

        CDM cdmEntitiy = cdmEntity(dto);

        UserHelper userHelper = UserHelperAccess.userHelper();
        boolean canDelte = userHelper.userHasPermission(cdmEntitiy, CRUD.DELETE);
        boolean canEdit = userHelper.userHasPermission(cdmEntitiy, CRUD.UPDATE);

        User user = userHelper.user();

        if(AbstractCdmPopupEditor.class.isAssignableFrom(getView().getClass())){
            AbstractCdmPopupEditor popupView = ((AbstractCdmPopupEditor)getView());

            if(cdmEntitiy.isPersited() && !canEdit){
                popupView.setReadOnly(true); // never reset true to false here!
                logger.debug("setting editor to readonly");
            }
            if(!cdmEntitiy.isPersited() || !canDelte){
                popupView.withDeleteButton(false);
                logger.debug("removing delete button");
            }
        }

    }

    /**
     * @param dto
     * @return
     */
    protected abstract CDM cdmEntity(DTO dto);

    /**
     * @param identifier
     * @return
     */
    protected abstract CDM loadCdmEntity(UUID uuid);

    /**
     * Grant per entity CdmAuthority to the current user <b>for the bean which is not yet loaded</b>
     * into the editor. The <code>CRUD</code> to be granted are stored in the <code>crud</code> field.
     */
    protected abstract void guaranteePerEntityCRUDPermissions(UUID identifier);

    /**
     * Grant per entity CdmAuthority to the current user for the bean which is loaded
     * into the editor. The <code>CRUD</code> to be granted are stored in the <code>crud</code> field.
     */
     protected abstract void guaranteePerEntityCRUDPermissions(CDM bean);

    /**
     * @return
     */
    protected abstract IService<CDM> getService();

    @SuppressWarnings("unchecked")
    @Override
    // @EventBusListenerMethod // already annotated at super class
    public void onEditorPreSaveEvent(EditorPreSaveEvent preSaveEvent){

        if(!isFromOwnView(preSaveEvent)){
            return;
        }
        super.onEditorPreSaveEvent(preSaveEvent);
    }

    @Override
    // @EventBusListenerMethod // already annotated at super class
    public void onEditorSaveEvent(EditorSaveEvent<DTO> saveEvent){

        if(!isFromOwnView(saveEvent)){
            return;
        }

        // the bean is now updated with the changes made by the user
        DTO dto = saveEvent.getBean();
        CDM cdmEntity = cdmEntity(dto);

        if(logger.isTraceEnabled()){
            PersistentContextAnalyzer pca = new PersistentContextAnalyzer(cdmEntity);
            pca.printEntityGraph(System.err);
            pca.printCopyEntities(System.err);
        }

        if(logger.isTraceEnabled()){
            PersistentContextAnalyzer pca = new PersistentContextAnalyzer(cdmEntity);
            pca.printEntityGraph(System.err);
            pca.printCopyEntities(System.err);
        }
        EntityChangeEvent<?> changeEvent = null;
        try {
            dto = preSaveBean(dto);
            changeEvent = getStore().saveBean(cdmEntity, (AbstractView<?>) getView());

            if(changeEvent != null){
                viewEventBus.publish(this, changeEvent);
            }
        } catch (HibernateException e){
            if(newAuthorityCreated != null){
                UserHelperAccess.userHelper().removeAuthorityForCurrentUser(newAuthorityCreated);
            }
            throw e;
        } finally {
            postSaveBean(changeEvent);
        }
    }

    /**
     * This method is intended to be used for the following purposes:
     * <ol>
     *   <li>
     *   EditorPresenters for beans with transient properties can overwrite this method to
     *   update the beanItem with the changes made to the transient properties.
     *   This can be necessary because Vaadin MethodProperties are readonly when no setter is
     *   available. This can be the case with transient properties. Automatic updating
     *   of the property during the fieldGroup commit does not work in this case.
     *   Presenters, however, should <b>operate on DTOs instead, which can implement the missing setter</b>.</li>
     *
     *   <li>When modifying a bi-directional relation between two instances the user would
     *   need to have GrantedAuthorities for both sides of the relationship. This, however is not
     *   always possible. As a temporary solution the user can be granted the missing authority just
     *   for the time of saving the new relationship. You may also want to implement
     *   {@link #postSaveBean(EntityChangeEvent)} in this case.
     *   See {@link https://dev.e-taxonomy.eu/redmine/issues/7390 #7390}
     *   </li>
     * </ol>
     *
     */
    protected DTO preSaveBean(DTO bean) {
        // blank implementation, to be implemented by sub classes if needed
        return bean;
    }

    @Override
    protected
    final void saveBean(DTO bean){
        // blank implementation, since this is not needed in this or any sub class
        // see onEditorSaveEvent() instead
    }

    /**
     * Called after saving the DTO to the persistent storage.
     * This method is called in any case even if the save operation failed.
     * See {@link  #postSaveBean(EntityChangeEvent)}.
     *
     * @param changeEvent may be null in case of errors during the save operation
     */
    protected void postSaveBean(EntityChangeEvent changeEvent) {
        // blank implementation, to be implemented by sub classes if needed
    }

    @Override
    protected void deleteBean(DTO bean){
        CDM cdmEntity = cdmEntity(bean);
        EntityChangeEvent changeEvent = getStore().deleteBean(cdmEntity, (AbstractView) getView());
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
    public ICdmEntityUuidCacher getCache() {
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


    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() throws Exception {
        super.destroy();
        disposeCache();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void disposeCache() {
        cache.dispose();
    }


    protected ITaxonNameStringFilterablePagingProvider taxonNameStringFilterablePagingProvider(Rank rank) {
        ITaxonNameStringFilterablePagingProvider pagingProvider =  (ITaxonNameStringFilterablePagingProvider) getRepo().getBean("taxonNameStringFilterablePagingProvider");
        if(rank != null) {
            pagingProvider.setRankFilter(rank);
        }
        return pagingProvider;
    }
}
