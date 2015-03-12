// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.container;

import java.sql.SQLException;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.query.QueryDelegate;

/**
 * @author cmathew
 * @date 10 Mar 2015
 *
 */
public class LeafNodeTaxonContainer extends SQLContainer {

    /**
     * @param delegate
     * @throws SQLException
     */
    public LeafNodeTaxonContainer(QueryDelegate delegate) throws SQLException {
        super(delegate);
        // TODO Auto-generated constructor stub
    }

}
