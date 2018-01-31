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

import eu.etaxonomy.cdm.vaadin.design.registration.WorkflowItemDesign;

/**
 * @author a.kohlbecker
 * @since Mar 10, 2017
 *
 */
@Deprecated // FIXME delete also all css styles if any
public class WorkflowBulletItem extends WorkflowItemDesign {

    private static final long serialVersionUID = -6825656185698773467L;

    /**
     *
     */
    public WorkflowBulletItem(int stepIndex, String caption, ClickListener listener) {
        this.stepIndex.setCaption(Integer.toString(stepIndex));
        this.caption.setValue(caption);
        if(listener != null){
            this.stepIndex.addClickListener(listener);
        }
    }

    @Override
    public void setCaption(String text){
        this.caption.setValue(text);
    }

}
