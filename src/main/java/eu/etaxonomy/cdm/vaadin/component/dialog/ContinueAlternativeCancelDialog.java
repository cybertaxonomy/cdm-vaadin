/**
* Copyright (C) 2018 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.dialog;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/**
 * @author a.kohlbecker
 * @since Nov 14, 2018
 *
 */
public class ContinueAlternativeCancelDialog extends Window {

    private static final long serialVersionUID = -1948284511236663594L;

    private Button alternativeButton;
    private Button continueButton;
    private Button cancelButton;

    /**
     * @param caption
     */
    public ContinueAlternativeCancelDialog(String caption, String message, String continueText, String alternativeText) {
        super(caption);
        setModal(true);
        VerticalLayout subContent = new VerticalLayout();
        subContent.setMargin(true);
        setContent(subContent);
        alternativeButton = new Button(alternativeText, FontAwesome.SAVE);
        alternativeButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        continueButton = new Button(continueText, FontAwesome.REMOVE);
        continueButton.setStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton = new Button("Cancel", FontAwesome.ARROW_CIRCLE_LEFT);
        Label messageLabel = new Label(message, com.vaadin.shared.ui.label.ContentMode.HTML);
        subContent.addComponent(messageLabel);
        HorizontalLayout buttonBar = new HorizontalLayout(alternativeButton, continueButton, cancelButton);
        buttonBar.setStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        buttonBar.setWidth(100, Unit.PERCENTAGE);
        buttonBar.setSpacing(true);
        buttonBar.setExpandRatio(alternativeButton, 1);
        buttonBar.setComponentAlignment(continueButton, Alignment.TOP_RIGHT);
        buttonBar.setComponentAlignment(alternativeButton, Alignment.TOP_RIGHT);
        buttonBar.setComponentAlignment(cancelButton, Alignment.TOP_RIGHT);
        subContent.addComponent(buttonBar);
    }

    public void addContinueClickListener(ClickListener listener){
        continueButton.addClickListener(listener);
    }
    public void addAlternativeClickListener(ClickListener listener){
        alternativeButton.addClickListener(listener);
    }

    public void addCancelClickListener(ClickListener listener){
        cancelButton.addClickListener(listener);
    }




}
