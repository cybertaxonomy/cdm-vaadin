/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.component.phycobank;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.ISODateTimeFormat;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;

import eu.etaxonomy.cdm.vaadin.design.phycobank.RegistrationItemDesign;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationDTO;
import eu.etaxonomy.cdm.vaadin.presenter.phycobank.RegistrationType;
import eu.etaxonomy.cdm.vaadin.view.phycobank.RegistrationTypeConverter;
import eu.etaxonomy.cdm.vaadin.view.phycobank.RegistrationWorkflowViewBean;
import eu.etaxonomy.vaadin.mvp.AbstractView;
import eu.etaxonomy.vaadin.ui.navigation.NavigationEvent;

/**
 * @author a.kohlbecker
 * @since Mar 17, 2017
 *
 */
public class RegistrationItem extends RegistrationItemDesign {

    private static final long serialVersionUID = -211003770452173644L;

    private RegistrationDTO regDto;

    private RegistrationTypeConverter regTypeConverter = new RegistrationTypeConverter();

    private AbstractView<?> parentView;

    /**
     *
     */
    public RegistrationItem(RegistrationDTO item, AbstractView<?> parentView) {
        super();
        setItem(item, parentView);
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
        getCitationLabel().setValue(regDto.getCitationString());
        getSummaryLabel().setValue(regDto.getSummary());
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
        typeStateLabel.setIcon(icon);
        typeStateLabel.setValue(StringUtils.capitalize((regDto.getStatus().name().toLowerCase())));
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
        getCreatedLabel().setDescription(regDto.getCreated().toString(ISODateTimeFormat.yearMonthDay()));
        if(regDto.getRegistrationDate() != null){
            getPublishedLabel().setDescription(regDto.getRegistrationDate().toString(ISODateTimeFormat.yearMonthDay()));
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
     * @return the citationLabel
     */
    public Label getCitationLabel() {
        return citationLabel;
    }

    /**
     * @return the summaryLabel
     */
    public Label getSummaryLabel() {
        return summaryLabel;
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
