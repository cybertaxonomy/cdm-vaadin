/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.cache;

import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since 09.11.2017
 *
 */
/**
 * @author a.kohlbecker
 * @since Jan 4, 2018
 *
 */
public interface EntityCache {

    /**
     * @param value
     *
     * @return the cached entity if it is found in the cache otherwise null
     */
    public <CDM extends CdmBase> CDM find(CDM value);

    public boolean update();

    public <CDM extends CdmBase> void add(CDM value);

    /**
     * @param type
     * @param id
     * @return
     */
    public <CDM extends CdmBase> CDM find(Class<CDM> type, int id);


    /**
     * Find the <code>value</code> in the cache and update it in case it
     * has an later <code>updatedWhen</code> value.
     * The properties of later updated entity will be copied over to the cached entity before it is returned.
     *
     * @param value the value to be searched in the cache
     *
     * @return the cached entity if it is found in the cache otherwise null
     */
    public <CDM extends CdmBase> CDM findAndUpdate(CDM value);


}
