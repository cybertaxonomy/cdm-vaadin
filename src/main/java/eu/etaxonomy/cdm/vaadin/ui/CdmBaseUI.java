/**
 * Copyright (C) 2015 EDIT
 * European Distributed Institute of Taxonomy
 * http://www.e-taxonomy.eu
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * See LICENSE.TXT at the top of this package for the full license terms.
 */
package eu.etaxonomy.cdm.vaadin.ui;

import com.vaadin.data.util.sqlcontainer.query.generator.filter.QueryBuilder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;

import eu.etaxonomy.cdm.addon.config.UIDisabledException;
import eu.etaxonomy.cdm.vaadin.util.CdmSQLStringDecorator;
import eu.etaxonomy.cdm.vaadin.util.CdmVaadinSessionUtilities;

/**
 * @author cmathew
 * @since 7 Apr 2015
 *
 */
public class CdmBaseUI extends UI {

    /* (non-Javadoc)
     * @see com.vaadin.ui.UI#init(com.vaadin.server.VaadinRequest)
     */
    @Override
    protected void init(VaadinRequest request) {
        // TODO: Need to evaluate the various sql dialects and make sure that these
        // queries are compatible with all

        if(!isEnabled()) {
            throw new UIDisabledException(getClass().getSimpleName());
        }
        QueryBuilder.setStringDecorator(new CdmSQLStringDecorator());

        CdmVaadinSessionUtilities.initCdmDataChangeService();

        CdmVaadinSessionUtilities.initSelectionService();

        CdmVaadinSessionUtilities.initBasicEventService();
    }


}
