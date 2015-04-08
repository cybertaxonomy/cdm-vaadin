// $Id$
/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.util;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.Logger;

/**
 *
 * The methods correctCase and isEscaped have been inspired from the
 * org.dbunit.util.SQLHelper class
 *
 * @author cmathew
 * @date 7 Apr 2015
 *
 */
public class SQLUtils {

    private static final Logger logger = Logger.getLogger(SQLUtils.class);

    public static String correctCase(final String databaseIdentifier, DatabaseMetaData databaseMetaData) throws SQLException
    {
        String resultIdentifier = databaseIdentifier;
        String dbIdentifierQuoteString = databaseMetaData.getIdentifierQuoteString();
        if(!isEscaped(databaseIdentifier, dbIdentifierQuoteString)){
            if(databaseMetaData.storesLowerCaseIdentifiers()) {
                resultIdentifier = databaseIdentifier.toLowerCase(Locale.ENGLISH);
            }
            else if(databaseMetaData.storesUpperCaseIdentifiers()) {
                resultIdentifier = databaseIdentifier.toUpperCase(Locale.ENGLISH);
            }
        }
        return resultIdentifier;

    }

    private static final boolean isEscaped(String tableName, String dbIdentifierQuoteString) {
        return tableName!=null && dbIdentifierQuoteString!= null && (tableName.startsWith(dbIdentifierQuoteString));
    }

}
