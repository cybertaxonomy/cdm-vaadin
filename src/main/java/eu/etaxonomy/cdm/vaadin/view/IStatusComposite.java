// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view;

import java.sql.SQLException;

import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;
import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public interface IStatusComposite {

    public interface StatusComponentListener {
        public LeafNodeTaxonContainer loadTaxa(int classificationId) throws SQLException;
        public CdmSQLContainer loadClassifications() throws SQLException;
    }

    public void setListener(StatusComponentListener listener);

}
