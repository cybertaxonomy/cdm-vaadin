/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import com.vaadin.ui.Button.ClickListener;

import eu.etaxonomy.cdm.vaadin.design.registration.WorkflowStepsDesign;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
public class WorkflowSteps extends WorkflowStepsDesign {

    private static final long serialVersionUID = 7224620045791102584L;

    public WorkflowSteps() {
        // TODO Auto-generated constructor stub
    }

    public void appendWorkflowItem(WorkflowBulletItem item){
        this.steps.addComponent(item);
    }

    public void appendNewWorkflowItem(int stepIndex, String caption, ClickListener listener){
        this.steps.addComponent(new WorkflowBulletItem(stepIndex, caption, listener));
    }

}
