/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.cache;

import java.beans.PropertyDescriptor;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.CollectionProxy;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.MapProxy;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.SortedMapProxy;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.model.common.VersionableEntity;
import eu.etaxonomy.cdm.model.reference.INomenclaturalReference;
import eu.etaxonomy.cdm.persistence.dao.initializer.AbstractBeanInitializer;

/**
 * @author a.kohlbecker
 * @since 08.11.2017
 *
 */
public class CdmEntityCache implements EntityCache {


    private final static Logger logger = Logger.getLogger(CdmEntityCache.class);

    private static final String COPY_ENTITY = "!";

    private Set<CdmBase> entities = new HashSet<>();

    private Map<EntityKey, CdmBase> entityyMap = new HashMap<>();

    private List<String> entityPathList = new ArrayList<>();

    private Map<EntityKey, List<String>> entityPathsMap = new HashMap<>();

    private Set<EntityKey> copyEntitiyKeys = new HashSet<>();

    private Set<Object> objectsSeen = new HashSet<>();

    /**
     * @param entity the first entity to be cached. can be <code>null</code>.
     *      Further entities can be added to the cache with {@link CdmEntityCache#add(CdmBase)}
     */
    public CdmEntityCache(CdmBase entity){
        if(entity != null){
            this.entities.add(entity);
            update();
        }
    }

    @Override
    public boolean update() {

        entityPathList.clear();
        entityPathsMap.clear();
        objectsSeen.clear();
        copyEntitiyKeys.clear();

        for(CdmBase entity : entities){
        analyzeEntity(entity, "");
        }

        return copyEntitiyKeys.isEmpty();
    }

    /**
     *
     */
    protected void analyzeEntity(CdmBase bean, String propertyPath) {

        if(bean == null){
            return;
        }

        CdmBase proxyBean = bean;

        bean = HibernateProxyHelper.deproxy(proxyBean, CdmBase.class);

        EntityKey entityKey = new EntityKey(bean);

        propertyPath += "[" + entityKey;
        String flags = "";
        CdmBase mappedEntity = entityyMap.put(entityKey, proxyBean);

        if(mappedEntity != null && mappedEntity != bean) {
            copyEntitiyKeys.add(entityKey);
            flags += COPY_ENTITY + bean.hashCode();
        }

        flags = analyzeMore(bean, entityKey, flags, mappedEntity);

        if(!flags.isEmpty()){
            propertyPath += "(" + flags + ")";
        }
        propertyPath += "]";

        logger.debug(propertyPath);

        entityPathList.add(propertyPath);
        if(!entityPathsMap.containsKey(entityKey)){
            entityPathsMap.put(entityKey, new ArrayList<>());
        }
        entityPathsMap.get(entityKey).add(propertyPath);

        if(!objectsSeen.add(bean)){
            // avoid cycles, do not recurse into properties of objects that have been analyzed already
            return;
        }

        Set<PropertyDescriptor> properties = AbstractBeanInitializer.getProperties(bean, null);
        for(PropertyDescriptor prop : properties){

            try {
                Object propertyValue = PropertyUtils.getProperty(bean, prop.getName());

                if(propertyValue == null){
                    continue;
                }

                String propertyPathSuffix = "." + prop.getName();
                logger.debug("\t\tproperty:" + propertyPathSuffix);

                if(Hibernate.isInitialized(propertyValue)) {

                    if(CdmBase.class.isAssignableFrom(prop.getPropertyType())
                            || INomenclaturalReference.class.isAssignableFrom(prop.getPropertyType())
                            ){
                        analyzeEntity(HibernateProxyHelper.deproxy(propertyValue, CdmBase.class), propertyPath + propertyPathSuffix);
                        continue;
                    }

                    Collection<CdmBase> collection = null;
                    if(propertyValue instanceof AbstractPersistentCollection){
                        if (propertyValue  instanceof Collection) {
                            collection = (Collection<CdmBase>) propertyValue;
                        } else if (propertyValue instanceof Map) {
                            collection = ((Map<?,CdmBase>)propertyValue).values();
                        } else {
                            logger.error("unhandled subtype of AbstractPersistentCollection");
                        }
                    } else if (propertyValue instanceof CollectionProxy
                                || propertyValue instanceof MapProxy<?, ?>
                                || propertyValue instanceof SortedMapProxy<?, ?>){
                            //hibernate envers collections
                            // FIXME this won't work!!!!
                            collection = (Collection<CdmBase>)propertyValue;
                    }

                    if(collection != null){
                        for(CdmBase collectionItem : collection){
                            analyzeEntity(HibernateProxyHelper.deproxy(collectionItem, CdmBase.class), propertyPath + propertyPathSuffix);
                        }
                    } else {
                        // logger.error("Unhandled property type " + propertyValue.getClass().getName());
                    }
                }

            } catch (IllegalAccessException e) {
                String message = "Illegal access on property " + prop;
                logger.error(message);
                throw new RuntimeException(message, e);
            } catch (InvocationTargetException e) {
                String message = "Cannot invoke property " + prop + " not found";
                logger.error(message);
                throw new RuntimeException(message, e);
            } catch (NoSuchMethodException e) {
                String message = "Property " + prop.getName() + " not found for class " + bean.getClass();
                logger.error(message);
            }

        }
    }

    /**
     * Empty method which can be implemented by subclasses which do further analysis.
     *
     * @param bean
     * @param entityKey
     * @param flags
     * @param mappedEntity
     * @return
     */
    protected String analyzeMore(CdmBase bean, EntityKey entityKey, String flags, CdmBase mappedEntity) {
        return flags;
    }


    public void printEntityGraph(PrintStream printStream){
        printLegend(printStream);
        for(String path : entityPathList) {
            printStream.println(path);
        }
    }

    public void printCopyEntities(PrintStream printStream){
        printLegend(printStream);
        for(EntityKey key : copyEntitiyKeys){
            for(String path : entityPathsMap.get(key)) {
                printStream.println(path);
            }
        }
    }

    /**
     * @param printStream
     */
    protected void printLegend(PrintStream printStream) {
        printStream.println(this.getClass().getSimpleName() + " legend: ");
        printStream.println("    - '!{objectHash}': detected copy entity, followed by object hash");
    }

    public class EntityKey {

        Class type;
        int id;

        public EntityKey(Class type, int id){
            this.type = type;
            this.id = id;
        }

        public EntityKey(CdmBase entity){
            type = entity.getClass();
            id = entity.getId();
        }

        /**
         * @return the type
         */
        public Class getType() {
            return type;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return new HashCodeBuilder(15, 33)
                    .append(type)
                    .append(id)
                    .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            EntityKey other = (EntityKey)obj;
            return this.id == other.id && this.type == other.type;

        }

        @Override
        public String toString() {
            return type.getSimpleName() + "#" + getId();
        }

    }

    /**
     * {@inheritDoc}
     * <p>
     * In case the cached bean is a HibernateProxy it will be unproxied
     * before returning it.
     */
    @Override
    public <CDM extends CdmBase> CDM find(CDM value) {
        if(value != null){
            EntityKey entityKey = new EntityKey(HibernateProxyHelper.deproxy(value));
            CDM cachedBean = (CDM) entityyMap.get(entityKey);
            if(cachedBean != null){
                return (CDM) HibernateProxyHelper.deproxy(cachedBean, CdmBase.class);
            }
        }
        return null;
    }

    private <CDM extends CdmBase> CDM findProxy(CDM value) {
        if(value != null){
            EntityKey entityKey = new EntityKey(HibernateProxyHelper.deproxy(value));
            return (CDM) entityyMap.get(entityKey);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * In case the cached bean is a HibernateProxy it will be unproxied
     * before returning it.
     */
    @Override
    public <CDM extends CdmBase> CDM find(Class<CDM> type, int id) {
        EntityKey entityKey = new EntityKey(type, id);
        CDM cachedBean = (CDM) entityyMap.get(entityKey);
        if(cachedBean != null){
            return (CDM) HibernateProxyHelper.deproxy(cachedBean, CdmBase.class);
        }
        return null;
    }

    @Override
    public <CDM extends CdmBase> CDM findAndUpdate(CDM value){
        CDM cachedBean = findProxy(value);
        if(cachedBean != null && VersionableEntity.class.isAssignableFrom(cachedBean.getClass())){
            updatedCachedIfEarlier((VersionableEntity)cachedBean, (VersionableEntity)value);
        }
        if(cachedBean != null){
            return (CDM) HibernateProxyHelper.deproxy(cachedBean, CdmBase.class);
        }
        return null;

    }

    /**
     * @param cachedValue
     * @param value
     */
    private <CDM extends VersionableEntity> void updatedCachedIfEarlier(CDM cachedValue, CDM value) {
       if(cachedValue != null && value != null && value.getUpdated() != null){
           if(cachedValue.getUpdated() == null || value.getUpdated().isAfter(cachedValue.getUpdated())){
               try {
                copyProperties(cachedValue, value);
            } catch (IllegalAccessException e) {
                /* should never happen */
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                /* critical! re-throw as runtime exception, which is ok in the context of a vaadin app */
                throw new RuntimeException(e);
            }
           }
       }

    }

    /**
     * partially copy of {@link BeanUtilsBean#copyProperties(Object, Object)}
     */
    private <CDM extends VersionableEntity> void copyProperties(CDM dest, CDM orig) throws IllegalAccessException, InvocationTargetException {

        PropertyUtilsBean propertyUtils = BeanUtilsBean.getInstance().getPropertyUtils();
        PropertyDescriptor[] origDescriptors =
                propertyUtils.getPropertyDescriptors(orig);
            for (int i = 0; i < origDescriptors.length; i++) {
                String name = origDescriptors[i].getName();
                if ("class".equals(name)) {
                    continue; // No point in trying to set an object's class
                }
                if (propertyUtils.isReadable(orig, name) &&
                        propertyUtils.isWriteable(dest, name)) {
                    try {
                        if(CdmBase.class.isAssignableFrom(propertyUtils.getPropertyType(dest, name))){
                            CdmBase origValue = (CdmBase)propertyUtils.getSimpleProperty(orig, name);
                            if(!Hibernate.isInitialized(origValue)){
                                // ignore uninitialized entities
                                continue;
                            }
                            // only copy entities if origValue either is a completely different entity (A)
                            // or if origValue is updated (B).
                            CdmBase destValue = (CdmBase)propertyUtils.getSimpleProperty(dest, name);
                            if(destValue == null && origValue == null){
                                continue;
                            }
                            if(
                                    origValue != null && destValue == null ||
                                    origValue == null && destValue != null ||

                                    origValue.getId() != destValue.getId() ||
                                    origValue.getClass() != destValue.getClass() ||
                                    origValue.getUuid() != destValue.getUuid()){
                                // (A)
                                BeanUtilsBean.getInstance().copyProperty(dest, name, origValue);

                            } else {
                                // (B) recurse into findAndUpdate
                                findAndUpdate(origValue);
                            }
                        } else {
                                Object value = propertyUtils.getSimpleProperty(orig, name);
                                BeanUtilsBean.getInstance().copyProperty(dest, name, value);
                        }

                    } catch (NoSuchMethodException e) {
                        // Should not happen
                    }
                }
            }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <CDM extends CdmBase> void add(CDM value) {
        entities.add(value);
        analyzeEntity(value, "");
    }


}
