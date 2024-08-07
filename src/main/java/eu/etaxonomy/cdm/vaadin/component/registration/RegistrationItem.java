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
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.vaadin.spring.events.EventScope;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.Panel;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.api.service.dto.RegistrationWorkingSet;
import eu.etaxonomy.cdm.api.service.dto.RegistrationWrapperDTO;
import eu.etaxonomy.cdm.api.util.UserHelper;
import eu.etaxonomy.cdm.model.ICdmEntityUuidCacher;
import eu.etaxonomy.cdm.model.common.TimePeriod;
import eu.etaxonomy.cdm.model.name.RegistrationStatus;
import eu.etaxonomy.cdm.model.permission.CRUD;
import eu.etaxonomy.cdm.model.reference.Reference;
import eu.etaxonomy.cdm.service.UserHelperAccess;
import eu.etaxonomy.cdm.strategy.cache.TagEnum;
import eu.etaxonomy.cdm.strategy.cache.TaggedText;
import eu.etaxonomy.cdm.vaadin.component.BadgeButton;
import eu.etaxonomy.cdm.vaadin.event.ReferenceEditorAction;
import eu.etaxonomy.cdm.vaadin.event.ShowDetailsEvent;
import eu.etaxonomy.cdm.vaadin.permission.PermissionDebugUtils;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.cdm.vaadin.util.formatter.DateTimeFormat;
import eu.etaxonomy.cdm.vaadin.util.formatter.TimePeriodFormatter;
import eu.etaxonomy.cdm.vaadin.view.registration.RegistrationWorkingsetViewBean;
import eu.etaxonomy.vaadin.event.EditorActionType;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 17, 2017
 */
public class RegistrationItem extends GridLayout {

    public static final String VALIDATION_PROBLEMS = "validationProblems";

    public static final String BLOCKED_BY = "blockedBy";

    private static final String LABEL_CAPTION_CREATED = "Created";

    private static final String LABEL_CAPTION_PUBLISHED = "Published";

    private static final String LABEL_CAPTION_RELEASED = "Released";

    private static final int GRID_ROWS = 5;

    private static final int GRID_COLS = 3;

    private static final long serialVersionUID = -211003770452173644L;

    private AbstractView<?,?> parentView;

    private RegistrationWrapperDTO regDto;

    private TimePeriodFormatter timePeriodFormatter = new TimePeriodFormatter(DateTimeFormat.ISO8601_DATE);

    // --------------------------------------------------

    private RegistrationStatusLabel stateLabel = new RegistrationStatusLabel();
    private Link identifierLink = new Link();
    private Label citationSummaryLabel = new Label();
    private Button blockedByButton = new Button(FontAwesome.BAN);
    private BadgeButton validationProblemsButton;
    private Button openButton = new Button(FontAwesome.COGS);
    private Label submitterLabel = new Label();
    private Label createdLabel = new Label();
    private Label publishedLabel = new Label();
    private Label releasedLabel = new Label();

    private Panel blockingRelationsPanel;

    private ICdmEntityUuidCacher cache;

    public RegistrationItem(RegistrationWrapperDTO item, AbstractView<?,?> parentView, ICdmEntityUuidCacher cache) {
        super(GRID_COLS, GRID_ROWS);
        this.cache = cache;
        init();
        setItem(item, parentView);
    }

    public RegistrationItem(RegistrationWorkingSet workingSet, AbstractView<?,?> parentView, ICdmEntityUuidCacher cache) {
        super(GRID_COLS, GRID_ROWS);
        this.cache = cache;
        init();
        blockedByButton.setVisible(false);
        setWorkingSet(workingSet, parentView);
    }

    public void init() {

        setWidth(100, Unit.PERCENTAGE);
        addStyleName("registration-list-item");

        CssLayout stateUserContainer = new CssLayout();
        stateLabel.setStyleName(LABEL_NOWRAP + " registration-state");
        stateLabel.setVisible(false);

        submitterLabel.setStyleName(LABEL_NOWRAP + " submitter");
        submitterLabel.setIcon(FontAwesome.USER);
        submitterLabel.setContentMode(ContentMode.HTML);
        submitterLabel.setVisible(false);

        stateUserContainer.addComponents(stateLabel, submitterLabel);
        addComponent(stateUserContainer, 0, 0);
        setComponentAlignment(stateUserContainer, Alignment.TOP_LEFT);

        identifierLink.setVisible(false);
        addComponent(identifierLink, 1, 0);
        setComponentAlignment(identifierLink, Alignment.TOP_CENTER);
        setColumnExpandRatio(1, 1.0f);

        validationProblemsButton = new BadgeButton(FontAwesome.WARNING);
        CssLayout buttonGroup = new CssLayout(blockedByButton, validationProblemsButton, openButton);
        blockedByButton.setStyleName(ValoTheme.BUTTON_TINY);
        blockedByButton.setEnabled(false);
        validationProblemsButton.setStyleName(ValoTheme.BUTTON_TINY);
        validationProblemsButton.setEnabled(false);

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

    public void setItem(RegistrationWrapperDTO regDto, AbstractView<?,?> parentView){

        this.parentView = parentView;

        this.regDto = regDto;

        NavigationEvent navigationEvent = null;
        if(regDto.getCitationUuid() != null) {
            navigationEvent = new NavigationEvent(
                    RegistrationWorkingsetViewBean.NAME,
                    regDto.getCitationUuid().toString()
                    );
        } else {
            setComponentError(new UserError("Citation is missing"));
        }

        updateUI(regDto.getBibliographicInRefCitationString(), regDto.getCreated(), regDto.getDatePublished(), regDto.getValidationProblems().size(),
                navigationEvent, null, regDto, regDto.getSubmitterUserName());
    }

    public void setWorkingSet(RegistrationWorkingSet workingSet, AbstractView<?,?> parentView){
        this.parentView = parentView;

        ReferenceEditorAction referenceEditorAction = null;
        if(workingSet.getPublicationUnitUuid() != null){
            if(cdmUserHelper().userHasPermission(Reference.class, workingSet.getPublicationUnitUuid(), CRUD.UPDATE)){
                referenceEditorAction = new ReferenceEditorAction(EditorActionType.EDIT, workingSet.getPublicationUnitUuid(), null, null, parentView);
            }
            PermissionDebugUtils.addGainPerEntityPermissionButton(this, Reference.class, workingSet.getPublicationUnitUuid(), EnumSet.of(CRUD.UPDATE, CRUD.DELETE), null);
        } else {
            if(UserHelperAccess.userHelper().userHasPermission(Reference.class, CRUD.CREATE, null, null, parentView)){
                referenceEditorAction = new ReferenceEditorAction(EditorActionType.ADD);
            }
        }
        TimePeriod datePublished = null;
        String submitterName = null;
        if(workingSet.getRegistrationWrapperDTOs().size() > 0){
            datePublished = workingSet.getRegistrationWrapperDTOs().get(0).getDatePublished();
            // submitterName = workingSet.getRegistrationWrapperDTOs().get(0).getSubmitterUserName();
        }
        updateUI(workingSet.getCitation(), workingSet.getCreated(), datePublished, workingSet.validationProblemsCount(),
                referenceEditorAction, FontAwesome.EDIT, null, submitterName);
    }

    private UserHelper cdmUserHelper() {
        if(cache != null){
            return UserHelperAccess.userHelper().withCache(cache);
        } else {
            return UserHelperAccess.userHelper();
        }
    }

    private void updateUI(String citationString,  DateTime created, TimePeriod datePublished,  int validationProblemsCount,
            Object openButtonEvent, Resource openButtonIcon, RegistrationWrapperDTO regDto, String submitterUserName) {

        StringBuffer labelMarkup = new StringBuffer();
        DateTime registrationDate = null;

        if(validationProblemsCount > 0){
            getValidationProblemsButton().setEnabled(true);
            // getMessageButton().addStyleName(RegistrationStyles.STYLE_FRIENDLY_FOREGROUND);
            getValidationProblemsButton().addClickListener(e -> {
                ShowDetailsEvent<?,?> detailsEvent;
                if(regDto != null){
                    detailsEvent = new ShowDetailsEvent<RegistrationWrapperDTO, UUID>(
                            e,
                            RegistrationWrapperDTO.class,
                            regDto.getUuid(),
                            VALIDATION_PROBLEMS);
                } else {
                    detailsEvent = new ShowDetailsEvent<RegistrationWorkingSet, UUID>(
                            e,
                            RegistrationWorkingSet.class,
                            null,
                            VALIDATION_PROBLEMS);
                }
                publishEvent(detailsEvent);
                }
            );
            getValidationProblemsButton().setCaption(Integer.toString(validationProblemsCount));
        }

        if(regDto != null && regDto.isBlocked()){
            getBlockedByButton().setEnabled(true);
            getBlockedByButton().addStyleName(EditValoTheme.BUTTON_HIGHLIGHT);
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
            List<TaggedText> summaryTaggedText = regDto.getSummaryTaggedText();
            StringBuilder summaryBuilder = new StringBuilder();
            // needed to add missing blank after "Types:"
            // here we cover two cases, label tagged text with following separator tagged text
            // and label taggedText with colon included
            boolean lastTTwasTypes = false;
            for(TaggedText tt : summaryTaggedText) {
                if(tt.getType() == TagEnum.label) {
                    String tttext = tt.getText();
                    if(tttext.endsWith(":")) {
                        tttext += " ";
                    }
                    summaryBuilder.append("<strong>").append(tttext ).append("</strong>");
                } else {
                    summaryBuilder.append(tt.getText()).append(" ");
                }
                if(tt.getType() == TagEnum.separator && lastTTwasTypes) {
                    // add missing blank after "Types:"
                    summaryBuilder.append(" ");
                }
                lastTTwasTypes = tt.getText().equals("Types");
            }

            labelMarkup.append("</br>").append(summaryBuilder.toString());

            stateLabel.setVisible(true);
            stateLabel.update(regDto.getStatus());
            if(regDto.getIdentifier() != null){
                getIdentifierLink().setResource(new ExternalResource(regDto.getIdentifier()));
            }
            getIdentifierLink().setCaption(regDto.getIdentifier());
            //TODO make responsive and use specificIdentifier in case the space gets too narrow
            getIdentifierLink().setVisible(true);
            getIdentifierLink().setEnabled(regDto.getStatus() == RegistrationStatus.PUBLISHED);

            registrationDate = regDto.getRegistrationDate();
        }

        getCitationSummaryLabel().setValue(labelMarkup.toString());
        getSubmitterLabel().setValue(submitterUserName);
        getSubmitterLabel().setVisible(submitterUserName != null);
        updateDateLabels(created, datePublished, registrationDate);
    }

    private void updateDateLabels(DateTime created, TimePeriod datePublished, DateTime released) {
        if(created != null){
            getCreatedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_CREATED + "</span>&nbsp;" + created.toString(ISODateTimeFormat.yearMonthDay()));
        }
        if(datePublished != null){
            getPublishedLabel().setVisible(true);
            getPublishedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_PUBLISHED + "</span>&nbsp;" + timePeriodFormatter.print(datePublished));
        }
        if(released != null){
            getReleasedLabel().setVisible(true);
            getReleasedLabel().setValue("<span class=\"caption\">" + LABEL_CAPTION_RELEASED + "</span>&nbsp;" + released.toString(ISODateTimeFormat.yearMonthDay()));
        }
    }


    private void publishEvent(Object event) {
        if(event instanceof NavigationEvent){
            parentView.getViewEventBus().publish(EventScope.UI, this, event);
        } else {
            parentView.getViewEventBus().publish(this, event);
        }
    }

    public UUID getRegistrationUuid(){
        return regDto.getUuid();
    }

    public void showBlockingRegistrations(Set<RegistrationWrapperDTO> blockingRegDTOs) {

        if(blockingRelationsPanel == null) {

            if(regDto.isBlocked() && blockingRegDTOs.isEmpty()){
                throw new RuntimeException("Registration is blocked but tet of blocking registrations is empty");
            }
            if(!regDto.isBlocked() && !blockingRegDTOs.isEmpty()){
                throw new RuntimeException("No point showing blocking registrations for an unblocked registration");
            }

            blockingRelationsPanel = new RegistrationItemsPanel(parentView, "blocked by", blockingRegDTOs, cache);
            addComponent(blockingRelationsPanel, 0, 4, GRID_COLS - 1, 4);
        }

    }

    /* ====== RegistrationItemDesign Getters ====== */

    public Label getTypeStateLabel() {
        return stateLabel;
    }

    public Link getIdentifierLink() {
        return identifierLink;
    }

    public Label getCitationSummaryLabel() {
        return citationSummaryLabel;
    }

    public Button getBlockedByButton() {
        return blockedByButton;
    }

    public BadgeButton getValidationProblemsButton() {
        return validationProblemsButton;
    }

    public Button getOpenButton() {
        return openButton;
    }

    public Label getCreatedLabel() {
        return createdLabel;
    }

    public Label getPublishedLabel() {
        return publishedLabel;
    }

    public Label getReleasedLabel() {
        return releasedLabel;
    }

    public Label getSubmitterLabel() {
        return submitterLabel;
    }

    public boolean isShowBlockingRelations() {
        return blockingRelationsPanel != null;
    }
}