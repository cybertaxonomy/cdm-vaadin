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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;

import eu.etaxonomy.cdm.api.service.IService;
import eu.etaxonomy.cdm.api.util.UserHelper;
import eu.etaxonomy.cdm.cache.CdmTransientEntityWithUuidCacher;
import eu.etaxonomy.cdm.debug.PersistentContextAnalyzer;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.persistence.permission.CdmAuthority;
import eu.etaxonomy.cdm.service.CdmBeanItemContainerFactory;
import eu.etaxonomy.cdm.service.CdmFilterablePagingProviderFactory;
import eu.etaxonomy.cdm.service.CdmStore;
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
 */
public abstract class CdmEditorPresenterBase<DTO, CDM extends CdmBase, V extends ApplicationView<?>>
        extends AbstractEditorPresenter<DTO, V>
        implements CachingPresenter {

    private static final long serialVersionUID = 2218185546277084261L;
    private static final Logger logger = LogManager.getLogger();

    protected BeanInstantiator<DTO> beanInstantiator = null;

    @Autowired
    protected CdmBeanItemContainerFactory cdmBeanItemContainerFactory;

    @Autowired
    protected CdmFilterablePagingProviderFactory pagingProviderFactory;

    @Autowired
    protected CdmStore cdmStore;

    protected CdmAuthority newAuthorityCreated;

    /**
     * if not null, this CRUD set is to be used to create a CdmAuthoritiy for the base entity which will be
     * granted to the current use as long this grant is not assigned yet.
     */
    protected EnumSet<CRUD> crud = null;

    private ICdmEntityUuidCacher cache;

    private java.util.Collection<CdmBase> rootEntities = new HashSet<>();

    public void setBeanInstantiator(BeanInstantiator<DTO> beanInstantiator) {
        this.beanInstantiator = beanInstantiator;
    }

    protected DTO createNewBean() {
        if(this.beanInstantiator != null){
            return beanInstantiator.createNewBean();
        }
        return defaultBeanInstantiator().createNewBean();
    }

    protected abstract BeanInstantiator<DTO> defaultBeanInstantiator();

    public CdmEditorPresenterBase() {
        super();
        logger.trace(this._toString() + " constructor");
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
        DTO dto = initializeCache(cdmEntitiy);

        return dto;
    }

    /**
     * @param cdmEntitiy the CDM entity to initialize the cache with.
     */
    protected final DTO initializeCache(CDM cdmEntitiy) {
        cache = new CdmTransientEntityWithUuidCacher(this);
        // need to use load but put see #7214
        cdmEntitiy = cache.load(cdmEntitiy);
        addRootEntity(cdmEntitiy);

        DTO dto = createDTODecorator(cdmEntitiy);
        return dto;
    }

    protected abstract DTO createDTODecorator(CDM cdmEntitiy);

    @Override
    protected void adaptToUserPermission(DTO dto) {

        CDM cdmEntitiy = cdmEntity(dto);

        UserHelper userHelper = UserHelperAccess.userHelper();
        boolean canDelete = userHelper.userHasPermission(cdmEntitiy, CRUD.DELETE);
        boolean canEdit = userHelper.userHasPermission(cdmEntitiy, CRUD.UPDATE);

        if(AbstractPopupEditor.class.isAssignableFrom(getView().getClass())){
            AbstractPopupEditor<?,?> popupView = ((AbstractPopupEditor<?,?>)getView());

            if(cdmEntitiy.isPersited() && !canEdit){
                popupView.setReadOnly(true); // never reset true to false here!
                logger.debug("setting editor to readonly");
            }
            if(!cdmEntitiy.isPersited() || !canDelete){
                popupView.withDeleteButton(false);
                logger.debug("removing delete button");
            }
        }
    }

    protected abstract CDM cdmEntity(DTO dto);

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

    protected abstract IService<CDM> getService();

    @Override
    // @EventBusListenerMethod // already annotated at super class
    public void onEditorPreSaveEvent(EditorPreSaveEvent<DTO> preSaveEvent){
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
            changeEvent = cdmStore.saveBean(cdmEntity, (AbstractView<?>) getView());

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
    protected void postSaveBean(EntityChangeEvent<?> changeEvent) {
        // blank implementation, to be implemented by sub classes if needed
    }

    @Override
    protected void deleteBean(DTO bean){
        CDM cdmEntity = cdmEntity(bean);
        EntityChangeEvent<?> changeEvent = cdmStore.deleteBean(cdmEntity, (AbstractView<?>) getView());
        if(changeEvent != null){
            viewEventBus.publish(this, changeEvent);
        }
    }

    public void setGrantsForCurrentUser(EnumSet<CRUD> crud) {
        this.crud = crud;
    }

    @Override
    public ICdmEntityUuidCacher getCache() {
        return cache;
    }

    @Override
    public void addRootEntity(CdmBase entity) {
        rootEntities.add(entity);
    }

    @Override
    public Collection<CdmBase> getRootEntities() {
        return rootEntities;
    }

    @Override
    public void destroy() throws Exception {
        super.destroy();
        disposeCache();
    }

    @Override
    public void disposeCache() {
        cache.dispose();
    }
}