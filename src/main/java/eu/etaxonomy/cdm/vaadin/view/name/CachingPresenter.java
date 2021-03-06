/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.name;

import java.util.Collection;

import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.CdmBase;

/**
 * @author a.kohlbecker
 * @since 09.11.2017
 *
 */
public interface CachingPresenter {

    public ICdmEntityUuidCacher getCache();

    public Collection<CdmBase> getRootEntities();

    public void addRootEntity(CdmBase entity);

    public void disposeCache();
}
