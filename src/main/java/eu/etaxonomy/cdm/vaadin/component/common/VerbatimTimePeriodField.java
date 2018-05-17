/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import eu.etaxonomy.cdm.model.common.VerbatimTimePeriod;

/**
 * @author a.kohlbecker
 * @since May 17, 2018
 *
 */
public class VerbatimTimePeriodField extends AbstractTimePeriodField<VerbatimTimePeriod> {

    private static final long serialVersionUID = -8399562684225562651L;



    /**
     *
     */
    public VerbatimTimePeriodField() {
        super();
    }

    /**
     * @param string
     */
    public VerbatimTimePeriodField(String string) {
        super(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends VerbatimTimePeriod> getType() {
        return VerbatimTimePeriod.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected VerbatimTimePeriod newModelInstance() {

        return VerbatimTimePeriod.NewVerbatimInstance();
    }

}
