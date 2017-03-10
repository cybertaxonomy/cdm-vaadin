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

import eu.etaxonomy.cdm.vaadin.design.phycobank.WorkflowStepsDesign;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class WorkflowSteps extends WorkflowStepsDesign {

    /**
     *
     */
    public WorkflowSteps() {
        // TODO Auto-generated constructor stub
    }

    public void appendWorkflowItem(WorkflowItem item){
        this.steps.addComponent(item);
    }

    public void appendNewWorkflowItem(int stepIndex, String caption, ClickListener listener){
        this.steps.addComponent(new WorkflowItem(stepIndex, caption, listener));
    }

}
