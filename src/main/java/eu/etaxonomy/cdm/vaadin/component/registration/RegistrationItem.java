/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.registration;

import static eu.etaxonomy.cdm.vaadin.component.registration.RegistrationStyles.LABEL_NOWRAP;

import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.vaadin.event.AbstractEditorAction.Type;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.model.registration.RegistrationWorkingSet;
import eu.etaxonomy.cdm.vaadin.util.formatter.DateTimeFormat;
import eu.etaxonomy.cdm.vaadin.util.formatter.TimePeriodFormatter;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationDTO;
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

    private static final String LABEL_CAPTION_RELEASED = "Released";

    private static final int GRID_ROWS = 4;

    private static final int GRID_COLS = 3;

    private static final long serialVersionUID = -211003770452173644L;

    private RegistrationTypeConverter regTypeConverter = new RegistrationTypeConverter();

    private AbstractView<?> parentView;

    private TimePeriodFormatter timePeriodFormatter = new TimePeriodFormatter(DateTimeFormat.ISO8601_DATE);

    // --------------------------------------------------
    private TypeStateLabel typeStateLabel = new TypeStateLabel();
    private Link identifierLink = new Link();
    private Label citationSummaryLabel = new Label();
    private Button blockedByButton = new Button(FontAwesome.WARNING);
    private Button messageButton;
    private Button openButton = new Button(FontAwesome.COGS);
    private Label createdLabel = new Label();
    private Label publishedLabel = new Label();
    private Label releasedLabel = new Label();

    /**
     *
     */
    public RegistrationItem(RegistrationDTO item, AbstractView<?> parentView) {
        super(GRID_COLS, GRID_ROWS);
        init();
        setItem(item, parentView);
    }

    /**
    *
    */
   public RegistrationItem(RegistrationWorkingSet workingSet, AbstractView<?> parentView) {
       super(GRID_COLS, GRID_ROWS);
       init();
       setWorkingSet(workingSet, parentView);
   }

    public void init() {

        setWidth(100, Unit.PERCENTAGE);
        addStyleName("registration-list-item");

        typeStateLabel.setStyleName(LABEL_NOWRAP);
        typeStateLabel.setVisible(false);
        addComponent(typeStateLabel, 0, 0);
        setComponentAlignment(typeStateLabel, Alignment.TOP_LEFT);

        identifierLink.setVisible(false);
        addComponent(identifierLink, 1, 0);
        setComponentAlignment(identifierLink, Alignment.TOP_CENTER);
        setColumnExpandRatio(1, 1.0f);

        messageButton = new Button(FontAwesome.COMMENT);
        CssLayout buttonGroup = new CssLayout(blockedByButton, messageButton, openButton);
        blockedByButton.setStyleName(ValoTheme.BUTTON_TINY);
        blockedByButton.setEnabled(false);
        messageButton.setStyleName(ValoTheme.BUTTON_TINY);
        messageButton.setEnabled(false);

        openButton.setStyleName(ValoTheme.BUTTON_TINY);
        openButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        openButton.setVisible(false);

        buttonGroup.setStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP);
        addComponent(buttonGroup, 2, 0);
        setComponentAlignment(buttonGroup, Alignment.TOP_RIGHT);

        citationSummaryLabel.setContentMode(ContentMode.HTML);
        addComponent(citationSummaryLabel, 0, 1, 1, 3);

        createdLabel.setStyleName(LABEL_NOWRAP);
        createdLabel.setContentMode(ContentMode.HTML);
        createdLabel.setWidthUndefined();
        addComponent(createdLabel, 2, 1);
        setComponentAlignment(createdLabel, Alignment.BOTTOM_RIGHT);

        publishedLabel.setStyleName(LABEL_NOWRAP);
        publishedLabel.setContentMode(ContentMode.HTML);
        publishedLabel.setWidthUndefined();
        publishedLabel.setVisible(false);
        addComponent(publishedLabel, 2, 2);
        setComponentAlignment(publishedLabel, Alignment.BOTTOM_RIGHT);

        releasedLabel.setStyleName(LABEL_NOWRAP);
        releasedLabel.setContentMode(ContentMode.HTML);
        releasedLabel.setWidthUndefined();
        releasedLabel.setVisible(false);
        addComponent(releasedLabel, 2, 3);
        setComponentAlignment(releasedLabel, Alignment.BOTTOM_RIGHT);

    }

    public void setItem(RegistrationDTO regDto, AbstractView<?> parentView){
        this.parentView = parentView;

        NavigationEvent navigationEvent = new NavigationEvent(
                RegistrationWorkflowViewBean.NAME,
                RegistrationWorkflowViewBean.ACTION_EDIT,
                Integer.toString(regDto.getId())
                );

        updateUI(regDto.getBibliographicCitationString(), regDto.getCreated(), regDto.getDatePublished(), regDto.getMessages().size(),
                navigationEvent, null, regDto);
    }

    public void setWorkingSet(RegistrationWorkingSet workingSet, AbstractView<?> parentView){
        this.parentView = parentView;

        ReferenceEditorAction referenceEditorAction;
        if(workingSet.getCitationId() != null){
            referenceEditorAction = new ReferenceEditorAction(Type.EDIT, workingSet.getCitationId());
        } else {
            referenceEditorAction = new ReferenceEditorAction(Type.ADD);
        }
        TimePeriod datePublished = workingSet.getRegistrationDTOs().get(0).getDatePublished();
        updateUI(workingSet.getCitation(), workingSet.getCreated(), datePublished, workingSet.messagesCount(),
                referenceEditorAction, FontAwesome.EDIT, null);
    }

    /**
     *
     */
    private void updateUI(String citationString,  DateTime created, TimePeriod datePublished,  int messagesCount,
            Object openButtonEvent, Resource openButtonIcon, RegistrationDTO regDto) {

        StringBuffer labelMarkup = new StringBuffer();
        DateTime registrationDate = null;

        if(messagesCount > 0){
            getMessageButton().setEnabled(true);
            // getMessageButton().addStyleName(RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);
            getMessageButton().addClickListener(e -> {
                ShowDetailsEvent detailsEvent;
                if(regDto != null){
                    detailsEvent = new ShowDetailsEvent<RegistrationDTO, Integer>(
                            e,
                            RegistrationDTO.class,
                            regDto.getId(),
                            "messages");
                } else {
                    detailsEvent = new ShowDetailsEvent<RegistrationWorkingSet, Integer>(
                            e,
                            RegistrationWorkingSet.class,
                            null,
                            "messages");
                }
                publishEvent(detailsEvent);
                }
            );
            getMessageButton().setCaption("<span class=\"" + RegistrationStyles.BUTTON_BADGE +"\"> " + messagesCount + "</span>");
            getMessageButton().setCaptionAsHtml(true);
        }

        labelMarkup.append(citationString);

        if(openButtonEvent != null){
            // Buttons
            getOpenButton().setVisible(true);
            Collection<?> removeCandidates = getOpenButton().getListeners(ClickListener.class);
            removeCandidates.forEach(l -> getOpenButton().removeClickListener((ClickListener)l));
            getOpenButton().addClickListener(e -> publishEvent(openButtonEvent));
        }

        if(openButtonIcon != null){
            getOpenButton().setIcon(openButtonIcon);
        }

        if(regDto != null){
            labelMarkup.append("</br>").append(regDto.getSummary());

            typeStateLabel.setVisible(true);
            typeStateLabel.update(regDto.getRegistrationType(), regDto.getStatus());
            getIdentifierLink().setResource(new ExternalResource(regDto.getIdentifier()));
            //TODO make responsive and use specificIdentifier in case the space gets too narrow
            getIdentifierLink().setVisible(true);
            getIdentifierLink().setCaption(regDto.getIdentifier());

            registrationDate = regDto.getRegistrationDate();
        }


        getCitationSummaryLabel().setValue(labelMarkup.toString());
        updateDateLabels(created, datePublished, registrationDate);
    }


    /**
     *
     */
    private void updateDateLabels(DateTime created, TimePeriod datePublished, DateTime released) {
        getCreatedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_CREATED + "</span>&nbsp;" + created.toString(ISODateTimeFormat.yearMonthDay()));
        if(datePublished != null){
            getPublishedLabel().setVisible(true);


            getPublishedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_PUBLISHED + "</span>&nbsp;" + timePeriodFormatter.print(datePublished));
        }
        if(released != null){
            getReleasedLabel().setVisible(true);
            getReleasedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_RELEASED + "</span>&nbsp;" + released.toString(ISODateTimeFormat.yearMonthDay()));
        }
        // LABEL_CAPTION_RELEASED
    }



    private void publishEvent(Object event) {
        parentView.getEventBus().publishEvent(event);
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


    /**
     * @return
     */
    public Label getReleasedLabel() {
        return releasedLabel;
    }

   /* --------------------------------------- */

}
