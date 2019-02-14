/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.common;

import eu.etaxonomy.cdm.model.common.TimePeriod;

/**
 * @author a.kohlbecker
 * @since May 17, 2018
 *
 */
public class TimePeriodField extends AbstractTimePeriodField<TimePeriod> {

    private static final long serialVersionUID = -8399562684225562651L;

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<? extends TimePeriod> getType() {
        return TimePeriod.class;
    }

    /**
     *
     */
    public TimePeriodField() {
        super();
    }

    /**
     * @param string
     */
    public TimePeriodField(String string) {
        super(string);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected TimePeriod newModelInstance() {

        return TimePeriod.NewInstance();
    }

}
