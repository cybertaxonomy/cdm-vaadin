/**
* Copyright (C) 2017 EDIT
* European Distributed Institute of Taxonomy
* http://www.e-taxonomy.eu
*
* The contents of this file are subject to the Mozilla Public License Version 1.1
* See LICENSE.TXT at the top of this package for the full license terms.
*/
package eu.etaxonomy.cdm.vaadin.view.registration;

import java.util.Collection;
import java.util.List;

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

import eu.etaxonomy.cdm.ext.registration.messages.Message;
import eu.etaxonomy.cdm.vaadin.event.error.DelegatingErrorHandler;
import eu.etaxonomy.cdm.vaadin.security.AccessRestrictedView;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initContent() {

        messagesPanel = new Panel();

        newMessageField = new TextArea();
        newMessageField.setNullRepresentation("");
        newMessageField.addTextChangeListener(e -> {
            sendMessageButton.setEnabled(StringUtils.isNoneBlank(e.getText()));
        });

        sendMessageButton = new Button(FontAwesome.SEND);
        sendMessageButton.addClickListener(e -> postNewMessage());

        HorizontalLayout sendMessagebar = new HorizontalLayout(newMessageField, sendMessageButton);
        sendMessagebar.setComponentAlignment(sendMessageButton, Alignment.MIDDLE_RIGHT);
        sendMessagebar.setExpandRatio(newMessageField, 1f);

        mainLayout.addComponents(messagesPanel, sendMessagebar);

        mainLayout.setErrorHandler(errrorHandler);
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
    public void loadMessagesFor(Integer registrationEntityId) {
        getPresenter().loadMessagesFor(registrationEntityId);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showMessages(List<Message> messages) {

        VerticalLayout messagesList = new VerticalLayout();

        for(Message message : messages){
            Label item = new Label("<span class=\"date-time\">(" +  message.getId() + ")</span> <span class=\"user-name\"><span class=\"user-name\">" + message.getFrom().getUsername() + "</span>: "  + message.getText());
            item.setStyleName("message-item");
            item.setContentMode(ContentMode.HTML);
            messagesList.addComponent(item);

        }
        messagesPanel.setContent(messagesList);

    }

    // TODO move into AbstractPopupView?
    @Override
    public DelegatingErrorHandler getErrrorHandler(){
        return errrorHandler;
    }


}
