// $Id$
/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.distributionStatus;

import java.io.IOException;

import org.apache.commons.io.IOUtils;

import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.RichTextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eu.etaxonomy.cdm.i18n.Messages;
import eu.etaxonomy.cdm.vaadin.view.distributionStatus.IDistributionTableView;

/**
 * @author freimeier
 * @since 9 Mar 2018
 *
 */
public class HelpWindow extends CustomComponent{
    private Window window;
    private AbstractOrderedLayout mainLayout;
    private IDistributionTableView view;

    public HelpWindow(IDistributionTableView view) {
        this.view = view;
        buildMainLayout();
    }

    private AbstractLayout buildMainLayout() {
        mainLayout = new VerticalLayout();
        mainLayout.setSizeFull();
        String text = "";
        try {
            text = IOUtils.toString(this.getClass().getResourceAsStream(Messages.getLocalizedString(Messages.HelpWindow_RESOURCE)),
                    "UTF-8");
        } catch (IOException e) {
        }

        RichTextArea textArea = new RichTextArea();
        textArea.setValue(text);
        textArea.setReadOnly(true);
        textArea.setWidth("95%");
        mainLayout.addComponent(textArea);
        mainLayout.setComponentAlignment(textArea, Alignment.TOP_CENTER);

        return mainLayout;
    }

    public Window createWindow(String caption) {
        window = new Window();
        window.setModal(true);
        window.setWidth("40%"); //$NON-NLS-1$
        window.setHeight("70%"); //$NON-NLS-1$
        window.setCaption(caption);
        window.setContent(mainLayout);
        return window;
    }
}
