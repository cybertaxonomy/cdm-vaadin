/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.debug;

import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import eu.etaxonomy.cdm.cache.CdmEntityCache;
import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since 08.11.2017
 *
 */
public class PersistentContextAnalyzer extends CdmEntityCache {


    private static final char HASH_SEPARATOR = '.';

    private static final String IN_PERSITENT_CONTEXT = "*";

    private final static Logger logger = Logger.getLogger(PersistentContextAnalyzer.class);

    private Session session;

    private boolean showHashCodes = false;

    /**
     * @param entity
     * @param session
     */
    public PersistentContextAnalyzer(CdmBase entity, Session session){
        this.session = session;
        this.entities.add(entity);
        update();
    }

    public PersistentContextAnalyzer(CdmBase entity){
        this(entity, null);
    }

    public PersistentContextAnalyzer(CdmEntityCache entityCache, Session session){
        this.session = session;
        this.entities.addAll(entityCache.getEntities());

    }


    /**
     * @param printStream
     */
    @Override
    protected void printLegend(PrintStream printStream) {
        printStream.println("PersistentContextAnalyzer legend: ");
        printStream.println("    - '.{objectHash}': unique copy entity, followed by object hash (only shown when showHashCodes is enabled)");
        printStream.println("    - '!{objectHash}': detected copy entity, followed by object hash");
        printStream.println("    - '*': entity mapped in persistent context");
    }

    @Override
    protected String analyzeMore(CdmBase bean, EntityKey entityKey, String flags, CdmBase mappedEntity) {

        boolean hashAdded = false;

        if(mappedEntity != null && mappedEntity != bean) {
            flags += bean.hashCode();
            hashAdded = true;
        }
        if(session != null && session.contains(bean)){
            flags += IN_PERSITENT_CONTEXT;
        }
        if(showHashCodes && ! hashAdded){
            flags += HASH_SEPARATOR + bean.hashCode();
        }
        return flags;
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

}
