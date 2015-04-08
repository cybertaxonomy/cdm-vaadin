// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.unitils;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.unitils.database.config.PropertiesDataSourceFactory;

/**
 * @author cmathew
 * @date 8 Apr 2015
 *
 */
public class CdmTestDataSourceFactory extends PropertiesDataSourceFactory {

    @Override
    public DataSource createDataSource() {
        BasicDataSource dataSource = (BasicDataSource)super.createDataSource();
        dataSource.setMaxActive(-1);
        return dataSource;
    }

}
