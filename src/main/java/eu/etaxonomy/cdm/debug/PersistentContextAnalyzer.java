/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.debug;

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

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.collection.internal.AbstractPersistentCollection;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.CollectionProxy;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.MapProxy;
import org.hibernate.envers.internal.entities.mapper.relation.lazy.proxy.SortedMapProxy;

import eu.etaxonomy.cdm.hibernate.HibernateProxyHelper;
import eu.etaxonomy.cdm.model.common.CdmBase;
import eu.etaxonomy.cdm.persistence.dao.initializer.AbstractBeanInitializer;

/**
 * @author a.kohlbecker
 * @since 08.11.2017
 *
 */
public class PersistentContextAnalyzer {

    /**
     *
     */
    private static final char HASH_SEPARATOR = '.';

    /**
     *
     */
    private static final String COPY_ENTITY = "!";

    /**
     *
     */
    private static final String IN_PERSITENT_CONTEXT = "*";

    private final static Logger logger = Logger.getLogger(PersistentContextAnalyzer.class);

    private Session session;

    private CdmBase entity;

    private Map<EntityKey, CdmBase> entityyMap = new HashMap<>();

    private List<String> entityPathList = new ArrayList<>();

    private Map<EntityKey, List<String>> entityPathsMap = new HashMap<>();

    private Set<EntityKey> copyEntitiyKeys = new HashSet<>();

    private Set<Object> objectsSeen = new HashSet<>();

    private boolean showHashCodes = false;

    /**
     * TODO the PersistentContextAnalyzer should be a subclass od the CdmEntityCache!!!
     *
     * @param entity
     * @param session
     */
    public PersistentContextAnalyzer(CdmBase entity, Session session){
        this.session = session;
        this.entity = entity;
        update();
    }

    public PersistentContextAnalyzer(CdmBase entity){
        this(entity, null);
    }

    /**
     * - find copied entities in the graph
     */
    private void update() {

        entityyMap.clear();
        entityPathList.clear();
        entityPathsMap.clear();
        copyEntitiyKeys.clear();
        objectsSeen.clear();

        String propertyPath = "";

        analyzeEntity(entity, propertyPath);
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
        printStream.println("PersistentContextAnalyzer legend: ");
        printStream.println("    - '.{objectHash}': unique copy entity, followed by object hash (only shown when showHashCodes is enabled)");
        printStream.println("    - '!{objectHash}': detected copy entity, followed by object hash");
        printStream.println("    - '*': entity mapped in persistent context");
    }

    /**
     *
     */
    protected void analyzeEntity(CdmBase bean, String propertyPath) {

        EntityKey entityKey = new EntityKey(bean);

        propertyPath += "[" + entityKey;
        String flags = "";
        CdmBase mappedEntity = entityyMap.put(entityKey, bean);

        boolean hashAdded = false;

        if(session != null && session.contains(bean)){
            flags += IN_PERSITENT_CONTEXT;
        }
        if(mappedEntity != null && mappedEntity != bean) {
            copyEntitiyKeys.add(entityKey);
            flags += COPY_ENTITY + bean.hashCode();
            hashAdded = true;
        }
        if(showHashCodes && ! hashAdded){
            flags += HASH_SEPARATOR + bean.hashCode();
        }
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

                if(Hibernate.isInitialized(propertyValue)) {

                    if(CdmBase.class.isAssignableFrom(prop.getPropertyType())){
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
     * @return the showHashCodes
     */
    public boolean isShowHashCodes() {
        return showHashCodes;
    }

    /**
     * @param showHashCodes the showHashCodes to set
     */
    public void setShowHashCodes(boolean showHashCodes) {
        boolean runUpdate = this.showHashCodes != showHashCodes;
        this.showHashCodes = showHashCodes;
        if(runUpdate){
            update();
        }
    }

    class EntityKey {

        Class type;
        int id;

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
            return new HashCodeBuilder(17, 31)
                    .append(type)
                    .append(id)
                    .toHashCode();
        }

        @Override
        public String toString() {
            return type.getSimpleName() + "#" + getId();
        }

    }


}
