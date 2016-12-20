/**
* Copyright (C) 2015 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.ProgressBar;

import eu.etaxonomy.cdm.vaadin.util.CdmVaadinUtilities;

/**
 * @author cmathew
 * @date 15 Apr 2015
 *
 */
public class CdmProgressComponent extends CustomComponent {

    /*- VaadinEditorProperties={"grid":"RegularGrid,20","showGrid":true,"snapToGrid":true,"snapToObject":true,"movingGuides":false,"snappingDistance":10} */

    @AutoGenerated
    private HorizontalLayout mainLayout;
    @AutoGenerated
    private ProgressBar progressIndicator;
    @AutoGenerated
    private Label progressLabel;
    /**
     * The constructor should first build the main layout, set the
     * composition root and then do any custom initialization.
     *
     * The constructor will not be automatically regenerated by the
     * visual editor.
     */
    public CdmProgressComponent() {
        buildMainLayout();
        setCompositionRoot(mainLayout);
        mainLayout.setVisible(false);
    }

    public void setProgress(String progressText) {
        mainLayout.setVisible(true);
        CdmVaadinUtilities.setEnabled(mainLayout, true, null);
        progressLabel.setValue(progressText);
        progressIndicator.setIndeterminate(true);
    }

    public void setProgress(String progressText, float progress) {
        mainLayout.setVisible(true);
        CdmVaadinUtilities.setEnabled(mainLayout, true, null);
        progressLabel.setValue(progressText);
        progressIndicator.setValue(progress);
        if(progress == 1.0) {
            mainLayout.setVisible(false);
        }
    }

    @AutoGenerated
    private HorizontalLayout buildMainLayout() {
        // common part: create layout
        mainLayout = new HorizontalLayout();
        mainLayout.setImmediate(false);
        mainLayout.setWidth("240px");
        mainLayout.setHeight("29px");
        mainLayout.setMargin(false);
        mainLayout.setSpacing(true);

        // top-level component properties
        setWidth("240px");
        setHeight("29px");

        // progressLabel
        progressLabel = new Label();
        progressLabel.setImmediate(false);
        progressLabel.setWidth("-1px");
        progressLabel.setHeight("-1px");
        progressLabel.setValue("Label");
        mainLayout.addComponent(progressLabel);
        mainLayout.setComponentAlignment(progressLabel, new Alignment(33));

        // progressIndicator
        progressIndicator = new ProgressBar();
        progressIndicator.setImmediate(false);
        progressIndicator.setWidth("-1px");
        progressIndicator.setHeight("-1px");
        mainLayout.addComponent(progressIndicator);
        mainLayout.setExpandRatio(progressIndicator, 1.0f);
        mainLayout.setComponentAlignment(progressIndicator, new Alignment(48));

        return mainLayout;
    }

}
