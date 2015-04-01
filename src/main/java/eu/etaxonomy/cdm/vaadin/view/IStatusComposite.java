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

import eu.etaxonomy.cdm.vaadin.container.CdmSQLContainer;
import eu.etaxonomy.cdm.vaadin.container.LeafNodeTaxonContainer;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public interface IStatusComposite {

    public interface StatusComponentListener {
        public LeafNodeTaxonContainer loadTaxa(int classificationId) throws SQLException;
        public CdmSQLContainer loadClassifications() throws SQLException;
        /**
         *
         */
        public void setUnplacedFilter();
        /**
         *
         */
        public void setUnpublishedFilter();
        /**
         *
         */
        public void removeUnplacedFilter();
        /**
         *
         */
        public void removeUnpublishedFilter();

        /**
         * @param filterString
         */
        public void setNameFilter(String filterString);
        /**
         *
         */
        public void removeNameFilter();

        /**
         * @return
         */
        public int getCurrentNoOfTaxa();

        /**
         * @return
         */
        public int getTotalNoOfTaxa();
        /**
         *
         */
        public void refresh();
        /**
         *
         */
        public void removeFilters();
        /**
         * @param parentItemId
         */
        public void addChildren(Object parentItemId);
    }

    public void setListener(StatusComponentListener listener);

}
