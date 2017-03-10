/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.phycobank;

import com.vaadin.ui.Button.ClickListener;

import eu.etaxonomy.cdm.vaadin.design.phycobank.WorkflowItemDesign;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class WorkflowItem extends WorkflowItemDesign {

    private static final long serialVersionUID = -6825656185698773467L;

    /**
     *
     */
    public WorkflowItem(int stepIndex, String caption, ClickListener listener) {
        this.stepIndex.setCaption(Integer.toString(stepIndex));
        this.caption.setValue(caption);
        if(listener != null){
            this.stepIndex.addClickListener(listener);
        }
    }

}
