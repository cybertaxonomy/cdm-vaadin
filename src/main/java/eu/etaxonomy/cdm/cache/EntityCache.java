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
public interface EntityCache {

    /**
     * @param value
     * @return
     */
    public <CDM extends CdmBase> CDM find(CDM value);

    public boolean update();

    /**
     * @param type
     * @param id
     * @return
     */
    public <CDM extends CdmBase> CDM find(Class<CDM> type, int id);


}
