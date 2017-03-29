/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Component;

/**
 * @author a.kohlbecker
 * @since Mar 27, 2017
 *
 */
public class AccordionWorkflow extends Accordion {

    private static final long serialVersionUID = -3279853764419918649L;

    public AccordionWorkflow() {
        super();
        init();
    }

    public AccordionWorkflow(Component... components) {
        super(components);
        init();
    }

    /**
     *
     */
    private void init() {
        setTabCaptionsAsHtml(true);
    }
}
