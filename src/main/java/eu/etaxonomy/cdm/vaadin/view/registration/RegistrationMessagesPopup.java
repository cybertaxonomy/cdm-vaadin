/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.security.core.GrantedAuthority;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import eu.etaxonomy.cdm.ext.registration.messages.Message;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.cdm.vaadin.permission.AccessRestrictedView;
import eu.etaxonomy.cdm.vaadin.theme.EditValoTheme;
import eu.etaxonomy.vaadin.mvp.AbstractPopupView;

@SpringComponent
@Scope("prototype")
public class RegistrationMessagesPopup extends AbstractPopupView<RegistrationMessagesPresenter>
    implements RegistrationMessagesView, AccessRestrictedView {

    private static final long serialVersionUID = 713522519903334889L;

    Panel messagesPanel;

    TextArea newMessageField;

    Button sendMessageButton;

    private VerticalLayout mainLayout;

    private DelegatingErrorHandler errrorHandler = new DelegatingErrorHandler();

    public RegistrationMessagesPopup() {

        mainLayout = new VerticalLayout();
        // IMPORTANT: mainLayout must be set to full size otherwise the
        // popup window may have problems with automatic resizing of its
        // content.
        mainLayout.setSizeFull();

        setCompositionRoot(mainLayout);
    }


    @Override
    protected void initContent() {

        messagesPanel = new Panel();
        messagesPanel.setStyleName(EditValoTheme.PANEL_CONTENT_PADDING_LEFT);

        newMessageField = new TextArea();
        newMessageField.setNullRepresentation("");
        newMessageField.addTextChangeListener(e -> {
            sendMessageButton.setEnabled(StringUtils.isNoneBlank(e.getText()));
        });
        newMessageField.setHeight("64px"); // height of the Submit button when ValoTheme.BUTTON_HUGE
        newMessageField.setWidth("100%");

        sendMessageButton = new Button(FontAwesome.SEND);
        sendMessageButton.addClickListener(e -> postNewMessage());
        sendMessageButton.setStyleName(ValoTheme.BUTTON_HUGE + " " +ValoTheme.BUTTON_PRIMARY);

        HorizontalLayout sendMessagebar = new HorizontalLayout(newMessageField, sendMessageButton);
        sendMessagebar.setComponentAlignment(sendMessageButton, Alignment.MIDDLE_RIGHT);
        sendMessagebar.setExpandRatio(newMessageField, 1f);
        sendMessagebar.setWidth("100%");

        mainLayout.addComponents(messagesPanel, sendMessagebar);

        mainLayout.setErrorHandler(errrorHandler);
        mainLayout.setComponentAlignment(sendMessagebar, Alignment.BOTTOM_CENTER);
    }


    @Override
    public int getWindowHeight() {
        // undefined
        return -1;
    }

    @Override
    public boolean isClosable() {
        return true;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    /**
     * @return
     */
    private void postNewMessage() {
        // quick and dirty implementation, better send an event
        String text = newMessageField.getValue();
        if(StringUtils.isNotBlank(text)){
            getPresenter().postMessage(text);
            newMessageField.setValue(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWindowCaption() {
        return "Messages";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void focusFirst() {
        // none
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean allowAnonymousAccess() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<Collection<GrantedAuthority>> allowedGrantedAuthorities() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancel() {
        // not needed

    }

    /**
     * @param identifier
     */
    public void loadMessagesFor(UUID registrationEntityUuid) {
        getPresenter().loadMessagesFor(registrationEntityUuid);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessages(String registrationLabel, List<Message> messages) {

        VerticalLayout messagesList = new VerticalLayout();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");

        for(Message message : messages){
            Label item = new Label("<span class=\"date-time\">(" +  dateFormat.format(message.getCreatedOn()) + ")</span> <span class=\"user-name\">" + message.getFrom().getUsername() + "</span>: <span class=\"message-text\">"  + message.getText() + "</span>");
            item.setStyleName("message-item");
            item.setContentMode(ContentMode.HTML);
            messagesList.addComponent(item);

        }
        messagesPanel.setCaption(registrationLabel);
        messagesPanel.setContent(messagesList);

    }

    // TODO move into AbstractPopupView?
    @Override
    public DelegatingErrorHandler getErrrorHandler(){
        return errrorHandler;
    }


}
