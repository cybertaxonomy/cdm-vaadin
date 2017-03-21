/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.ISODateTimeFormat;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.presenter.registration.RegistrationType;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationTypeConverter;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationWorkflowViewBean;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 17, 2017
 *
 */
public class RegistrationItem extends GridLayout {


    private static final String LABEL_CAPTION_CREATED = "Created";

    private static final String LABEL_CAPTION_PUBLISHED = "Published";

    private static final int GRID_ROWS = 3;

    private static final int GRID_COLS = 3;

    private static final String STYLE_LABEL_NOWRAP = "label-nowrap";

    private static final long serialVersionUID = -211003770452173644L;

    private RegistrationDTO regDto;

    private RegistrationTypeConverter regTypeConverter = new RegistrationTypeConverter();

    private AbstractView<?> parentView;

    // --------------------------------------------------
    private Label typeStateLabel = new Label();
    private Link identifierLink = new Link();
    private Label citationSummaryLabel = new Label();
    private Button blockedByButton = new Button(FontAwesome.WARNING);
    private Button messageButton = new Button(FontAwesome.COMMENT);
    private Button openButton = new Button(FontAwesome.COGS);
    private Label createdLabel = new Label();
    private Label publishedLabel = new Label();
    // --------------------------------------------------

    /**
     *
     */
    public RegistrationItem(RegistrationDTO item, AbstractView<?> parentView) {
        super(GRID_COLS, GRID_ROWS);
        init();
        setItem(item, parentView);
    }

    public void init() {

        setWidth(100, Unit.PERCENTAGE);
        addStyleName("registration-item");

        typeStateLabel.setStyleName(STYLE_LABEL_NOWRAP);
        addComponent(typeStateLabel, 0, 0);
        setComponentAlignment(typeStateLabel, Alignment.TOP_LEFT);

        addComponent(identifierLink, 1, 0);
        setComponentAlignment(identifierLink, Alignment.TOP_CENTER);
        setColumnExpandRatio(1, 1.0f);

        CssLayout buttonGroup = new CssLayout(blockedByButton, messageButton, openButton);
        blockedByButton.setStyleName(ValoTheme.BUTTON_TINY);
        blockedByButton.setEnabled(false);
        messageButton.setStyleName(ValoTheme.BUTTON_TINY);
        messageButton.setEnabled(false);
        openButton.setStyleName(ValoTheme.BUTTON_TINY);
        openButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        addComponent(buttonGroup, 2, 0);
        setComponentAlignment(buttonGroup, Alignment.TOP_RIGHT);

        citationSummaryLabel.setContentMode(ContentMode.HTML);
        addComponent(citationSummaryLabel, 0, 1, 1, 2);

        createdLabel.setStyleName(STYLE_LABEL_NOWRAP);
        createdLabel.setContentMode(ContentMode.HTML);
        createdLabel.setWidthUndefined();
        addComponent(createdLabel, 2, 1);
        setComponentAlignment(createdLabel, Alignment.BOTTOM_RIGHT);

        publishedLabel.setStyleName(STYLE_LABEL_NOWRAP);
        publishedLabel.setContentMode(ContentMode.HTML);
        publishedLabel.setWidthUndefined();
        addComponent(publishedLabel, 2, 2);
        setComponentAlignment(publishedLabel, Alignment.BOTTOM_RIGHT);

    }

    public void setItem(RegistrationDTO item, AbstractView<?> parentView){
        regDto = item;
        this.parentView = parentView;
        updateUI();
    }


    /**
     *
     */
    private void updateUI() {
        updateTypeStateLabel();
        getCitationSummaryLabel().setValue(regDto.getCitationString() + "</br>" + regDto.getSummary());
        updateIdentifierLink();
        getOpenButton().addClickListener(e -> parentView.getEventBus().publishEvent(new NavigationEvent(
                RegistrationWorkflowViewBean.NAME,
                RegistrationWorkflowViewBean.ACTION_EDIT,
                regDto.getSpecificIdentifier().toString()
                )));
        updateDateLabels();
    }


    /**
     *
     */
    private void updateTypeStateLabel() {

        FontAwesome icon;
        if(regDto.getRegistrationType().equals(RegistrationType.NAME)) {
            icon = FontAwesome.TAG;
        } else if(regDto.getRegistrationType().equals(RegistrationType.TYPIFICATION)) {
            icon = FontAwesome.TAGS;
        } else {
            icon = FontAwesome.WARNING;
        }
        typeStateLabel.setContentMode(ContentMode.HTML);
        typeStateLabel.setValue(icon.getHtml() + "&nbsp;" + StringUtils.capitalize((regDto.getStatus().name().toLowerCase())));
        typeStateLabel.addStyleName("status-" + regDto.getStatus().name());
    }

    /**
     *
     */
    private void updateIdentifierLink() {
        getIdentifierLink().setResource(new ExternalResource(regDto.getRegistrationId()));
        //TODO make responsive and use specificIdetifier in case the space gets too narrow
        getIdentifierLink().setCaption(regDto.getRegistrationId());
    }

    /**
     *
     */
    private void updateDateLabels() {
        getCreatedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_CREATED + "</span>&nbsp;" + regDto.getCreated().toString(ISODateTimeFormat.yearMonthDay()));
        if(regDto.getRegistrationDate() != null){
            getPublishedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_PUBLISHED + "</span>&nbsp;" + regDto.getRegistrationDate().toString(ISODateTimeFormat.yearMonthDay()));
        } else {
            getPublishedLabel().setVisible(false);
        }
    }

    /* ====== RegistrationItemDesign Getters ====== */
    /**
     * @return the typeStateLabel
     */
    public Label getTypeStateLabel() {
        return typeStateLabel;
    }

    /**
     * @return the identifierLink
     */
    public Link getIdentifierLink() {
        return identifierLink;
    }

    /**
     * @return the citationSummaryLabel
     */
    public Label getCitationSummaryLabel() {
        return citationSummaryLabel;
    }

    /**
     * @return the blockedByButton
     */
    public Button getBlockedByButton() {
        return blockedByButton;
    }

    /**
     * @return the messageButton
     */
    public Button getMessageButton() {
        return messageButton;
    }

    /**
     * @return the openButton
     */
    public Button getOpenButton() {
        return openButton;
    }

    /**
     * @return the createdLabel
     */
    public Label getCreatedLabel() {
        return createdLabel;
    }

    /**
     * @return the publishedLabel
     */
    public Label getPublishedLabel() {
        return publishedLabel;
    }

   /* --------------------------------------- */

}
