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

import com.vaadin.data.util.sqlcontainer.query.generator.filter.StringDecorator;

/**
 * @author cmathew
 * @date 16 Mar 2015
 *
 */
public class CdmSQLStringDecorator extends StringDecorator {

    /**
     * @param quoteStart
     * @param quoteEnd
     */
    public CdmSQLStringDecorator() {
        super("", "");        
    }

}
